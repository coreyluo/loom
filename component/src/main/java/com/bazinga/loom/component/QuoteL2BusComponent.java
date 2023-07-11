package com.bazinga.loom.component;

import com.alibaba.fastjson.JSONObject;
import com.bazinga.constant.DateConstant;
import com.bazinga.constant.SymbolConstants;
import com.bazinga.enums.GearLevelEnum;
import com.bazinga.enums.OperateStatusEnum;
import com.bazinga.enums.OrderCancelPoolStatusEnum;
import com.bazinga.loom.cache.CacheManager;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.dto.DisableInsertStockDTO;
import com.bazinga.loom.dto.ReInsertInfoDTO;
import com.bazinga.loom.event.InsertOrderEvent;
import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.service.DealOrderPoolService;
import com.bazinga.loom.service.OrderCancelPoolService;
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
    private OrderCancelPoolService orderCancelPoolService;

    @Autowired
    private DisableInsertStockPoolComponent disableInsertStockPoolComponent;

    @Autowired
    private ApplicationContext applicationContext;

    public void dealWithQuote(CommonQuoteDTO commonQuoteDTO) {
        try {
            String quoteTime = DateTimeUtils.trans2CommonFormat(commonQuoteDTO.getQuoteTime(), false);
            Date quoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + quoteTime, DateUtil.yyyyMMddHHmmssSSS);
            if (quoteDate == null) {
                log.info("时间解析错误quoteTime{}", commonQuoteDTO.getQuoteTime());
                return;
            }
            if (quoteDate.before(DateConstant.AM_09_25_10)) {
                log.info("集合行情数据{}", JSONObject.toJSONString(commonQuoteDTO));
                callMarket(commonQuoteDTO);
                if (commonQuoteDTO.getBuyThreePrice().compareTo(BigDecimal.ZERO) > 0
                        || commonQuoteDTO.getSellThreePrice().compareTo(BigDecimal.ZERO) > 0) {
                    log.info("保存开盘行情快照stockCode{}", commonQuoteDTO.getStockCode());
                    stockOpen(commonQuoteDTO);
                    snapshotComponent.saveOpenSnapshot(commonQuoteDTO);
                }
            } else {
                log.info("盘中行情数据{}", JSONObject.toJSONString(commonQuoteDTO));
                runMarket(commonQuoteDTO,quoteDate);
                if (quoteDate.after(DateConstant.PM_14_57_00)) {
                    if (commonQuoteDTO.getBuyThreePrice().compareTo(BigDecimal.ZERO) > 0
                            || commonQuoteDTO.getSellThreePrice().compareTo(BigDecimal.ZERO) > 0) {
                        log.info("保存收盘行情快照stockCode{} quote{}", commonQuoteDTO.getStockCode(), JSONObject.toJSONString(commonQuoteDTO));
                        snapshotComponent.saveSnapshot(commonQuoteDTO);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }


    }

    private void stockOpen(CommonQuoteDTO commonQuoteDTO) {
        if(commonQuoteDTO.getBuyOnePrice().subtract(new BigDecimal("0.02")).compareTo(commonQuoteDTO.getYesterdayPrice())>0){
            List<OrderCancelPool> orderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 1);
            if(!CollectionUtils.isEmpty(orderCancelPools)){
                log.info("高开过多撤单 不织布stockCode{}",commonQuoteDTO.getStockCode());
                orderCancelPoolComponent.invokeCancel(orderCancelPools,1);
                InsertCacheManager.LOOM_LIST.remove(commonQuoteDTO.getStockCode());
            }
        }
        if(commonQuoteDTO.getSellOnePrice().add(new BigDecimal("0.02")).compareTo(commonQuoteDTO.getYesterdayPrice())<0){
            List<OrderCancelPool> sellOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -1);
            if(!CollectionUtils.isEmpty(sellOrderCancelPools)){
                log.info("低开过多撤单 不织布stockCode{}",commonQuoteDTO.getStockCode());
                orderCancelPoolComponent.invokeCancel(sellOrderCancelPools,-1);
                InsertCacheManager.LOOM_LIST.remove(commonQuoteDTO.getStockCode());
            }
        }

    }

    //集合逻辑处理
    private void callMarket(CommonQuoteDTO commonQuoteDTO) {
      /*  List<OrderCancelPool> sellOneOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -1);
        List<OrderCancelPool> sellTwoOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -2);
        List<OrderCancelPool> buyOneorderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 1);
        List<OrderCancelPool> buyTwoorderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 2);*/
        if (commonQuoteDTO.getBuyOneQuantity() == 0) {
            return;
        }
        if(!InsertCacheManager.LOOM_LIST.contains(commonQuoteDTO.getStockCode())){
            return;
        }
        Long unaviliable = commonQuoteDTO.getBuyTwoQuantity() == null ? commonQuoteDTO.getSellTwoQuantity() : commonQuoteDTO.getBuyTwoQuantity();
        Long avgQuantity = snapshotComponent.getAvgQuantity(commonQuoteDTO.getStockCode());
        //匹配量异常放量 撤当前价单子
        if (commonQuoteDTO.getBuyOneQuantity() > 3 * avgQuantity) {
            for (GearLevelEnum value : GearLevelEnum.values()) {
                List<OrderCancelPool> orderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + value.getCode());
                if (CollectionUtils.isEmpty(orderCancelPools)) {
                    continue;
                }
                OrderCancelPool orderCancelPool = orderCancelPools.get(0);
                if (orderCancelPool.getOrderPrice().compareTo(commonQuoteDTO.getBuyOnePrice()) == 0) {
                    log.info("匹配量异常放量 撤当前价单子stockCode{} gearLevel{}", orderCancelPool.getStockCode(), value.getCode());
                    orderCancelPoolComponent.invokeCancel(orderCancelPools, value.getCode());
                    ORDER_CANCEL_POOL_MAP.remove(commonQuoteDTO.getStockCode() + value.getCode());
                }
            }
        }

        // 高于集合的买单 低于集合的卖单撤单
        List<OrderCancelPool> buyOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 1);
        if (!CollectionUtils.isEmpty(buyOrderCancelPools)) {
            OrderCancelPool orderCancelPool = buyOrderCancelPools.get(0);
            if (orderCancelPool.getOrderPrice().compareTo(commonQuoteDTO.getBuyOnePrice()) > 0) {
                log.info("撤高于集合的买单stockCode{} gearLevel{}", orderCancelPool.getStockCode(), orderCancelPool.getGearType());
                orderCancelPoolComponent.invokeCancel(buyOrderCancelPools, 1);
            }
        }

        List<OrderCancelPool> sellOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -1);
        if (!CollectionUtils.isEmpty(sellOrderCancelPools)) {
            OrderCancelPool orderCancelPool = sellOrderCancelPools.get(0);
            if (orderCancelPool.getOrderPrice().compareTo(commonQuoteDTO.getBuyOnePrice()) < 0) {
                log.info("撤低于集合的卖单stockCode{} gearLevel{}", orderCancelPool.getStockCode(), orderCancelPool.getGearType());
                orderCancelPoolComponent.invokeCancel(sellOrderCancelPools, -1);
            }
        }

    }

    private void runMarket(CommonQuoteDTO commonQuoteDTO,Date quoteDate) {
        if(!InsertCacheManager.LOOM_LIST.contains(commonQuoteDTO.getStockCode())){
            return;
        }
        if(quoteDate.after(DateConstant.PM_14_56_57)){
            return;
        }
        LimitQueue<Long> buyOneLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyOnePrice().toString());
        LimitQueue<Long> sellOneLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellOnePrice().toString());
        LimitQueue<Long> buyTwoLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyTwoPrice().toString());
        LimitQueue<Long> sellTwoLimitQueue = STOCK_PRICE_LIMIT_QUEUE_MAP.get(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellTwoPrice().toString());

        try {
            inserOrderLogic(1, commonQuoteDTO, buyOneLimitQueue);
            inserOrderLogic(-1, commonQuoteDTO, sellOneLimitQueue);
            logic(commonQuoteDTO, 1,  buyOneLimitQueue);
            logic(commonQuoteDTO, -1,  sellOneLimitQueue);
        } catch (Exception e) {
            log.error(e.getMessage()+ "stockCode"+ commonQuoteDTO.getStockCode(), e);
        }
        log.info("封单数据入队列 stockCode={}", commonQuoteDTO.getStockCode());
        buyOneLimitQueue = initLimitQueue(buyOneLimitQueue);
        buyTwoLimitQueue = initLimitQueue(buyTwoLimitQueue);
        sellOneLimitQueue = initLimitQueue(sellOneLimitQueue);
        sellTwoLimitQueue = initLimitQueue(sellTwoLimitQueue);
        buyOneLimitQueue.offer(commonQuoteDTO.getBuyOneQuantity());
        buyTwoLimitQueue.offer(commonQuoteDTO.getBuyTwoQuantity());
        sellOneLimitQueue.offer(-commonQuoteDTO.getSellOneQuantity());
        sellTwoLimitQueue.offer(-commonQuoteDTO.getSellTwoQuantity());
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyOnePrice().toString(), buyOneLimitQueue);
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getBuyTwoPrice().toString(), buyTwoLimitQueue);
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellOnePrice().toString(), sellOneLimitQueue);
        STOCK_PRICE_LIMIT_QUEUE_MAP.put(commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + commonQuoteDTO.getSellTwoPrice().toString(), sellTwoLimitQueue);
    }

    private void inserOrderLogic(int direction, CommonQuoteDTO commonQuoteDTO, LimitQueue<Long> limitQueue) {
        String key = commonQuoteDTO.getStockCode() + direction;
        List<OrderCancelPool> orderCancelPools = InsertCacheManager.ORDER_CANCEL_POOL_MAP.get(key);
        Integer dealDirection = InsertCacheManager.DEAL_DIRECTION_STOCK_MAP.get(key);
        if(dealDirection!=null && direction ==dealDirection){
            return;
        }
        //买
        if (direction == 1) {
            if (CollectionUtils.isEmpty(orderCancelPools) ) {
                if (commonQuoteDTO.getBuyOneQuantity() * 6 > commonQuoteDTO.getSellOneQuantity() + commonQuoteDTO.getSellTwoQuantity()) {
                    log.info("满足下单条件查看前面行情情况");
                    boolean insertFlag = false;
                    if (limitQueue.size() < 2) {
                        insertFlag = true;
                    } else {
                        Long lastTwo = limitQueue.getLastTwo();
                        if (commonQuoteDTO.getBuyOneQuantity() > lastTwo) {
                            insertFlag = true;
                        }
                    }
                    if (insertFlag) {
                        log.info("满足买入下单条件stockCode{} quote{}", commonQuoteDTO.getStockCode(), JSONObject.toJSONString(commonQuoteDTO));
                        applicationContext.publishEvent(new InsertOrderEvent(this, commonQuoteDTO.getStockCode(), commonQuoteDTO.getBuyOnePrice(),
                                commonQuoteDTO.getBuyOneQuantity(), "", direction));
                    }
                }

            }else {
                //价位跃分
                OrderCancelPool orderCancelPool = orderCancelPools.get(0);
                if(commonQuoteDTO.getBuyOnePrice().compareTo(orderCancelPool.getOrderPrice())>0){
                    if (commonQuoteDTO.getBuyOneQuantity() * 6 > commonQuoteDTO.getSellOneQuantity() + commonQuoteDTO.getSellTwoQuantity()){
                        log.info("价位越级先撤再挂stockCode{}",commonQuoteDTO.getStockCode());
                        ReInsertInfoDTO reInsertInfoDTO = new ReInsertInfoDTO();
                        reInsertInfoDTO.setDirection(direction);
                        reInsertInfoDTO.setOrderPrice(commonQuoteDTO.getBuyOnePrice());
                        reInsertInfoDTO.setStockCode(commonQuoteDTO.getStockCode());
                        if(orderCancelPools.get(0).getOrderNo()!=null){
                            InsertCacheManager.ORDER_NO_REINSET_MAP.put(orderCancelPools.get(0).getOrderNo(),reInsertInfoDTO);
                            orderCancelPoolComponent.invokeCancel(orderCancelPools,direction);
                        }
                    }
                }
            }
        } else {
            if (CollectionUtils.isEmpty(orderCancelPools)) {
                if (commonQuoteDTO.getSellOneQuantity() * 6 > commonQuoteDTO.getBuyOneQuantity() + commonQuoteDTO.getBuyTwoQuantity()) {
                    log.info("满足下单条件查看前面行情情况");
                    boolean insertFlag = false;
                    if (limitQueue.size() < 2) {
                        insertFlag = true;
                    } else {
                        Long lastTwo = limitQueue.getLast();
                        if (-commonQuoteDTO.getSellOneQuantity() < lastTwo) {
                            insertFlag = true;
                        }
                    }
                    if (insertFlag) {
                        log.info("满足卖出下单条件stockCode{} quote{}", commonQuoteDTO.getStockCode(), JSONObject.toJSONString(commonQuoteDTO));
                        applicationContext.publishEvent(new InsertOrderEvent(this, commonQuoteDTO.getStockCode(), commonQuoteDTO.getSellOnePrice(), 0L, "", direction));
                    }
                }
            }else {
                //价位跃分
                OrderCancelPool orderCancelPool = orderCancelPools.get(0);
                if(orderCancelPool.getOrderNo()!=null && commonQuoteDTO.getSellOnePrice().compareTo(orderCancelPool.getOrderPrice())<0){
                    if (commonQuoteDTO.getSellOneQuantity() * 6 > commonQuoteDTO.getBuyOneQuantity() + commonQuoteDTO.getBuyTwoQuantity()){
                        log.info("价位越级先撤再挂stockCode{}",commonQuoteDTO.getStockCode());
                        ReInsertInfoDTO reInsertInfoDTO = new ReInsertInfoDTO();
                        reInsertInfoDTO.setDirection(direction);
                        reInsertInfoDTO.setOrderPrice(commonQuoteDTO.getSellOnePrice());
                        reInsertInfoDTO.setStockCode(commonQuoteDTO.getStockCode());
                        InsertCacheManager.ORDER_NO_REINSET_MAP.put(orderCancelPools.get(0).getOrderNo(),reInsertInfoDTO);
                        orderCancelPoolComponent.invokeCancel(orderCancelPools,direction);
                    }
                }

            }
        }
    }

    private LimitQueue<Long> initLimitQueue(LimitQueue<Long> limitQueue) {
        if (limitQueue == null) {
            limitQueue = new LimitQueue<>(20);
        }
        return limitQueue;
    }


    //撤挂逻辑
    private void logic(CommonQuoteDTO commonQuoteDTO, int direction, LimitQueue<Long> limitQueue) {

        List<OrderCancelPool> orderCancelPools = InsertCacheManager.ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + (direction));
        if (!CollectionUtils.isEmpty(orderCancelPools)) {
            OrderCancelPool orderCancelPool = orderCancelPools.get(0);
            if (OrderCancelPoolStatusEnum.SUCCESS.getCode().equals(orderCancelPool.getStatus())) {
                BigDecimal comparePrice = direction == 1 ? commonQuoteDTO.getBuyOnePrice() : commonQuoteDTO.getSellOnePrice();
                // comparePrice = orderCancelPool.getOrderPrice();
                if (orderCancelPool.getOrderPrice().compareTo(comparePrice) == 0) {
                    if (!CollectionUtils.isEmpty(limitQueue)) {
                        Long peek = limitQueue.peek();
                        Long currentVolume = direction == 1 ? commonQuoteDTO.getBuyOneQuantity() : commonQuoteDTO.getSellOneQuantity();
                        if (currentVolume * 2 < Math.abs(peek)) {
                            log.info("触发撤单stockCode{} 当前封单量{} peek{} limitQueue{}", commonQuoteDTO.getStockCode(), currentVolume, peek, JSONObject.toJSONString(limitQueue));
                            orderCancelPoolComponent.invokeCancel(orderCancelPools, direction);
                            /*if(direction==1){
                                BigDecimal orderPrice = level==1 ? commonQuoteDTO.getBuyThreePrice():commonQuoteDTO.getBuyTwoPrice();
                                *//*if(level==1){
                                    List<OrderCancelPool> orderCancelPools2 = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + direction * 2);
                                    //第2对变1对
                                    for (OrderCancelPool cancelPool : orderCancelPools2) {
                                        cancelPool.setGearType(direction);
                                        orderCancelPoolService.updateById(orderCancelPool);
                                    }
                                    ORDER_CANCEL_POOL_MAP.put(commonQuoteDTO.getStockCode() + direction,orderCancelPools2);
                                }*//*
                                log.info("封单减少撤单stockCode{} direction{} level{} 行情{}",commonQuoteDTO.getStockCode(),direction,level,JSONObject.toJSONString(commonQuoteDTO));
                              //  applicationContext.publishEvent(new InsertOrderEvent(this,commonQuoteDTO.getStockCode(),orderPrice,0L,"",direction*2));

                            }*/
                        }
                    }
                }
            }
        } else {
            //止损
            String dealUniqueKey = UniqueKeyUtil.getDealUniqueKey(commonQuoteDTO.getStockCode(), direction , new Date());
            DealOrderPool dealOrderPool = dealOrderPoolService.getByStockCodeGearTypeDay(dealUniqueKey);
            if (dealOrderPool != null) {
                String reverseUniqueKey = UniqueKeyUtil.getDealUniqueKey(commonQuoteDTO.getStockCode(), -direction, new Date());
                DealOrderPool reverse = dealOrderPoolService.getByStockCodeGearTypeDay(reverseUniqueKey);
                if (reverse == null) {
                    log.info("进入是否需要止损判断stockCode{}",commonQuoteDTO.getStockCode());
                    if (!CollectionUtils.isEmpty(limitQueue)) {
                        Long peek = limitQueue.peek();
                        BigDecimal comparePrice = direction == 1 ? commonQuoteDTO.getBuyOnePrice() : commonQuoteDTO.getSellOnePrice();
                        Long currentVolume = direction == 1 ? commonQuoteDTO.getBuyOneQuantity() : commonQuoteDTO.getSellOneQuantity();
                        if (dealOrderPool.getTradePrice().compareTo(comparePrice) == 0) {
                            if (currentVolume * 5 < Math.abs(peek)) {
                                log.info("平价止损stockCode{} direction{} level{} 行情{}", commonQuoteDTO.getStockCode(), direction, direction, JSONObject.toJSONString(commonQuoteDTO));
                                ReInsertInfoDTO reInsertInfoDTO = new ReInsertInfoDTO();
                                reInsertInfoDTO.setDirection(-direction);
                                reInsertInfoDTO.setOrderPrice(comparePrice);
                                reInsertInfoDTO.setStockCode(commonQuoteDTO.getStockCode());
                                InsertCacheManager.ORDER_NO_REINSET_MAP.put(orderCancelPools.get(0).getOrderNo(),reInsertInfoDTO);
                                orderCancelPoolComponent.invokeCancel(orderCancelPools, direction);
                               /* if(level==1){
                                    //第2对变1对
                                    for (OrderCancelPool cancelPool : orderCancelPools2) {
                                        cancelPool.setGearType(direction);
                                    }
                                    ORDER_CANCEL_POOL_MAP.put(commonQuoteDTO.getStockCode() + direction,orderCancelPools2);
                                }*/
                               // applicationContext.publishEvent(new InsertOrderEvent(this, commonQuoteDTO.getStockCode(), comparePrice, 0L, "", -direction));
                            }
                        } else {
                            if (direction > 0) {
                                if (dealOrderPool.getTradePrice().compareTo(commonQuoteDTO.getBuyOnePrice()) > 0) {
                                    if (currentVolume * 2 < peek) {
                                        log.info("亏钱止损stockCode{} direction{} 行情{}", commonQuoteDTO.getStockCode(), direction, JSONObject.toJSONString(commonQuoteDTO));
                                        ReInsertInfoDTO reInsertInfoDTO = new ReInsertInfoDTO();
                                        reInsertInfoDTO.setDirection(-direction);
                                        reInsertInfoDTO.setOrderPrice(commonQuoteDTO.getBuyOnePrice());
                                        reInsertInfoDTO.setStockCode(commonQuoteDTO.getStockCode());
                                        InsertCacheManager.ORDER_NO_REINSET_MAP.put(orderCancelPools.get(0).getOrderNo(),reInsertInfoDTO);
                                        orderCancelPoolComponent.invokeCancel(orderCancelPools, direction);
                                        //撤卖二直接吃货止损
                                    //    applicationContext.publishEvent(new InsertOrderEvent(this, commonQuoteDTO.getStockCode(), commonQuoteDTO.getBuyOnePrice(), 0L, "", -direction));
                                    }
                                }
                            }else {
                                if (dealOrderPool.getTradePrice().compareTo(commonQuoteDTO.getSellOnePrice()) < 0) {
                                    if (currentVolume * 2 < Math.abs(peek)) {
                                        log.info("亏钱止损stockCode{} direction{} 行情{}", commonQuoteDTO.getStockCode(), direction, JSONObject.toJSONString(commonQuoteDTO));
                                        ReInsertInfoDTO reInsertInfoDTO = new ReInsertInfoDTO();
                                        reInsertInfoDTO.setDirection(-direction);
                                        reInsertInfoDTO.setOrderPrice(commonQuoteDTO.getSellOnePrice());
                                        reInsertInfoDTO.setStockCode(commonQuoteDTO.getStockCode());
                                        InsertCacheManager.ORDER_NO_REINSET_MAP.put(orderCancelPools.get(0).getOrderNo(),reInsertInfoDTO);
                                        orderCancelPoolComponent.invokeCancel(orderCancelPools, direction);
                                        //撤卖二直接吃货止损
                                     //   applicationContext.publishEvent(new InsertOrderEvent(this, commonQuoteDTO.getStockCode(), commonQuoteDTO.getSellOnePrice(), 0L, "", -direction));
                                    }
                                }
                            }
                        }
                        }
                    }

                }
            }
        }


    }
