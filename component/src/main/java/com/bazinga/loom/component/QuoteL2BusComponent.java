package com.bazinga.loom.component;

import com.alibaba.fastjson.JSONObject;
import com.bazinga.constant.DateConstant;
import com.bazinga.constant.SymbolConstants;
import com.bazinga.enums.GearLevelEnum;
import com.bazinga.enums.OrderCancelPoolStatusEnum;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.event.InsertOrderEvent;
import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.service.DealOrderPoolService;
import com.bazinga.loom.util.UniqueKeyUtil;
import com.bazinga.queue.LimitQueue;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.bazinga.loom.cache.CacheManager.STOCK_PRICE_LIMIT_QUEUE_MAP;
import static com.bazinga.loom.cache.InsertCacheManager.ORDER_CANCEL_POOL_MAP;

@Component
@Slf4j
public class QuoteL2BusComponent {

    @Autowired
    private OrderCancelPoolComponent orderCancelPoolComponent;

    @Autowired
    private DealOrderPoolService dealOrderPoolService;

    @Autowired
    private SnapshotComponent snapshotComponent;

    @Autowired
    private ApplicationContext applicationContext;

    public void dealWithQuote(CommonQuoteDTO commonQuoteDTO){
        try {
            Date date = new Date();
            String quoteTime = DateTimeUtils.trans2CommonFormat(commonQuoteDTO.getQuoteTime(),false);
            Date quoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + quoteTime, DateUtil.yyyyMMddHHmmssSSS);

            if(quoteDate.before(DateConstant.AM_09_25_10)){
                log.info("集合行情数据{}", JSONObject.toJSONString(commonQuoteDTO));
                callMarket(commonQuoteDTO);
            }else {
                runMarket(commonQuoteDTO);
                log.info("盘中行情数据{}",JSONObject.toJSONString(commonQuoteDTO));
                if(quoteDate.after(DateConstant.PM_14_57_00)){
                    if(commonQuoteDTO.getBuyThreePrice()!=null || commonQuoteDTO.getSellThreePrice()!=null){
                        log.info("保存收盘行情快照stockCode{}",commonQuoteDTO.getStockCode());
                        snapshotComponent.saveSnapshot(commonQuoteDTO);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    }

    //集合逻辑处理
    private void callMarket(CommonQuoteDTO commonQuoteDTO){
      /*  List<OrderCancelPool> sellOneOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -1);
        List<OrderCancelPool> sellTwoOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -2);
        List<OrderCancelPool> buyOneorderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 1);
        List<OrderCancelPool> buyTwoorderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 2);*/


        Long unaviliable = commonQuoteDTO.getBuyTwoQuantity()==null? commonQuoteDTO.getSellTwoQuantity():commonQuoteDTO.getBuyTwoQuantity();

        //匹配量异常放量 撤当前价单子
        if(commonQuoteDTO.getBuyOneQuantity() * 10 > unaviliable * 3){
            for (GearLevelEnum value : GearLevelEnum.values()) {
                List<OrderCancelPool> orderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + value.getCode());
                if(CollectionUtils.isEmpty(orderCancelPools)){
                    continue;
                }
                OrderCancelPool orderCancelPool = orderCancelPools.get(0);
                if(orderCancelPool.getOrderPrice().compareTo(commonQuoteDTO.getBuyOnePrice())==0){
                    log.info("匹配量异常放量 撤当前价单子stockCode{} gearLevel{}",orderCancelPool.getStockCode(),value.getCode());
                    orderCancelPoolComponent.invokeCancel(orderCancelPools);
                }
            }
        }

        // 高于集合的买单 低于集合的卖单撤单
        for (int i = 1; i <=2 ; i++) {
            List<OrderCancelPool> buyOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + i);
            if(!CollectionUtils.isEmpty(buyOrderCancelPools)){
                OrderCancelPool orderCancelPool = buyOrderCancelPools.get(0);
                if(orderCancelPool.getOrderPrice().compareTo(commonQuoteDTO.getBuyOnePrice())>0){
                    log.info("撤高于集合的买单stockCode{} gearLevel{}",orderCancelPool.getStockCode(),orderCancelPool.getGearType());
                    orderCancelPoolComponent.invokeCancel(buyOrderCancelPools);
                }
            }
        }
        for (int i = -2; i <=-1 ; i++) {
            List<OrderCancelPool> sellOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + i);
            if(!CollectionUtils.isEmpty(sellOrderCancelPools)){
                OrderCancelPool orderCancelPool = sellOrderCancelPools.get(0);
                if(commonQuoteDTO.getSellOnePrice().compareTo(commonQuoteDTO.getBuyOnePrice()) < 0){
                    log.info("撤低于集合的卖单stockCode{} gearLevel{}",orderCancelPool.getStockCode(),orderCancelPool.getGearType());
                    orderCancelPoolComponent.invokeCancel(sellOrderCancelPools);
                }
            }
        }
    }

    private void runMarket(CommonQuoteDTO commonQuoteDTO){
        String stockCode = commonQuoteDTO.getStockCode();

        LimitQueue<Long> buyOneLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyOnePrice().toString());
        LimitQueue<Long> sellOneLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellOnePrice().toString());
        LimitQueue<Long> buyTwoLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyTwoPrice().toString());
        LimitQueue<Long> sellTwoLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellTwoPrice().toString());

        try {
            logic(commonQuoteDTO,1,1,buyOneLimitQueue);
            logic(commonQuoteDTO,1,2,buyOneLimitQueue);
            logic(commonQuoteDTO,-1,1,sellOneLimitQueue);
            logic(commonQuoteDTO,-1,2,sellOneLimitQueue);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        log.info("封单数据入队列 stockCode={}",commonQuoteDTO.getStockCode());
        buyOneLimitQueue = initLimitQueue(buyOneLimitQueue);
        buyTwoLimitQueue = initLimitQueue(buyTwoLimitQueue);
        sellOneLimitQueue = initLimitQueue(sellOneLimitQueue);
        sellTwoLimitQueue = initLimitQueue(sellTwoLimitQueue);
        buyOneLimitQueue.offer(commonQuoteDTO.getBuyOneQuantity());
        buyTwoLimitQueue.offer(commonQuoteDTO.getBuyTwoQuantity());
        sellOneLimitQueue.offer(-commonQuoteDTO.getSellOneQuantity());
        sellTwoLimitQueue.offer(-commonQuoteDTO.getSellTwoQuantity());
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyOnePrice().toString(),buyOneLimitQueue);
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyTwoPrice().toString(),buyTwoLimitQueue);
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellOnePrice().toString(),sellOneLimitQueue);
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellTwoPrice().toString(),sellTwoLimitQueue);
    }

    private LimitQueue<Long> initLimitQueue(LimitQueue<Long> limitQueue){
        if(limitQueue==null){
            limitQueue = new LimitQueue<>(10);
        }

        return limitQueue;
    }


    //撤挂逻辑
    private void logic(CommonQuoteDTO commonQuoteDTO,int direction, int level,LimitQueue<Long> limitQueue){

        if(!"600157".equals(commonQuoteDTO.getStockCode())){
            return;
        }
        List<OrderCancelPool> orderCancelPools = InsertCacheManager.ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + (direction*level));
        if(!CollectionUtils.isEmpty(orderCancelPools)){
            OrderCancelPool orderCancelPool = orderCancelPools.get(0);
            if(OrderCancelPoolStatusEnum.SUCCESS.getCode().equals(orderCancelPool.getStatus())){
                BigDecimal comparePrice = direction == 1 ? commonQuoteDTO.getBuyOnePrice():commonQuoteDTO.getSellOnePrice();
                comparePrice = orderCancelPool.getOrderPrice();
                if(orderCancelPool.getOrderPrice().compareTo(comparePrice)== 0){
                    if(!CollectionUtils.isEmpty(limitQueue)){
                        Long peek = limitQueue.peek();
                        Long currentVolume = direction==1? commonQuoteDTO.getBuyOneQuantity():commonQuoteDTO.getSellOneQuantity();
                        if(currentVolume * 2 < Math.abs(peek)){
                            log.info("触发撤单stockCode{} 当前封单量{} limitQueue{}",commonQuoteDTO.getStockCode(),currentVolume,JSONObject.toJSONString(limitQueue));
                            orderCancelPoolComponent.invokeCancel(orderCancelPools);
                            if(direction==1){
                                BigDecimal orderPrice = level==1 ? commonQuoteDTO.getBuyThreePrice():commonQuoteDTO.getBuyTwoPrice();
                                if(level==1){
                                    List<OrderCancelPool> orderCancelPools2 = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + direction * 2);
                                    //第2对变1对
                                    for (OrderCancelPool cancelPool : orderCancelPools2) {
                                        cancelPool.setGearType(direction);
                                    }
                                    ORDER_CANCEL_POOL_MAP.put(commonQuoteDTO.getStockCode() + direction,orderCancelPools2);
                                }
                                log.info("封单减少撤单stockCode{} direction{} level{} 行情{}",commonQuoteDTO.getStockCode(),direction,level,JSONObject.toJSONString(commonQuoteDTO));
                                applicationContext.publishEvent(new InsertOrderEvent(this,commonQuoteDTO.getStockCode(),orderPrice,0L,"",direction*2));

                            }
                        }
                    }
                }
            }
        }else {
            //止损
            String dealUniqueKey = UniqueKeyUtil.getDealUniqueKey(commonQuoteDTO.getStockCode(), direction*level, new Date());
            DealOrderPool dealOrderPool = dealOrderPoolService.getByStockCodeGearTypeDay(dealUniqueKey);
            if(dealOrderPool!=null){
                String reverseUniqueKey = UniqueKeyUtil.getDealUniqueKey(commonQuoteDTO.getStockCode(), -direction*level, new Date());
                DealOrderPool reverse = dealOrderPoolService.getByStockCodeGearTypeDay(reverseUniqueKey);
                if(reverse==null){
                    if(!CollectionUtils.isEmpty(limitQueue)){
                        Long peek = limitQueue.peek();
                        BigDecimal comparePrice = direction == 1 ? commonQuoteDTO.getBuyOnePrice():commonQuoteDTO.getSellOnePrice();
                        Long currentVolume = direction==1? commonQuoteDTO.getBuyOneQuantity():commonQuoteDTO.getSellOneQuantity();
                        if(dealOrderPool.getTradePrice().compareTo(comparePrice)== 0){
                            if(currentVolume *5 < Math.abs(peek)){
                                List<OrderCancelPool> orderCancelPools2 = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + direction * 2);
                                orderCancelPoolComponent.invokeCancel(orderCancelPools);
                                if(level==1){
                                    //第2对变1对
                                    for (OrderCancelPool cancelPool : orderCancelPools2) {
                                        cancelPool.setGearType(direction);
                                    }
                                    ORDER_CANCEL_POOL_MAP.put(commonQuoteDTO.getStockCode() + direction,orderCancelPools2);
                                }
                                log.info("平价止损stockCode{} direction{} level{} 行情{}",commonQuoteDTO.getStockCode(),direction,level,JSONObject.toJSONString(commonQuoteDTO));
                                applicationContext.publishEvent(new InsertOrderEvent(this,commonQuoteDTO.getStockCode(),comparePrice,0L,"",-direction*level));

                            }
                        }else{
                            if(direction ==1 && dealOrderPool.getTradePrice().compareTo(commonQuoteDTO.getBuyOnePrice())>0){
                                if(currentVolume *2 < peek){
                                    log.info("亏钱止损stockCode{} direction{} level{} 行情{}",commonQuoteDTO.getStockCode(),direction,level,JSONObject.toJSONString(commonQuoteDTO));
                                    //撤卖二直接吃货止损
                                }
                            }

                        }
                    }
                }

            }
        }
    }





}
