package com.bazinga.loom.component;

import com.alibaba.fastjson.JSONObject;
import com.bazinga.constant.DateConstant;
import com.bazinga.enums.GearLevelEnum;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

import static com.bazinga.loom.cache.InsertCacheManager.ORDER_CANCEL_POOL_MAP;

@Component
@Slf4j
public class QuoteL2BusComponent {

    @Autowired
    private OrderCancelPoolComponent orderCancelPoolComponent;

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
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    }

    //集合逻辑处理
    private void callMarket(CommonQuoteDTO commonQuoteDTO){
        List<OrderCancelPool> sellOneOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -1);
        List<OrderCancelPool> sellTwoOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + -2);
        List<OrderCancelPool> buyOneorderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 1);
        List<OrderCancelPool> buyTwoorderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + 2);
        OrderCancelPool sellOneOrderCancelPool = sellOneOrderCancelPools.get(0);
        OrderCancelPool sellTwoOrderCancelPool = sellTwoOrderCancelPools.get(0);

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
                    orderCancelPoolComponent.invokeCancel(buyOneorderCancelPools);
                }
            }
        }
        for (int i = -2; i <=-1 ; i++) {
            List<OrderCancelPool> sellOrderCancelPools = ORDER_CANCEL_POOL_MAP.get(commonQuoteDTO.getStockCode() + i);
            if(!CollectionUtils.isEmpty(sellOrderCancelPools)){
                OrderCancelPool orderCancelPool = sellOrderCancelPools.get(0);
                if(commonQuoteDTO.getSellOnePrice().compareTo(commonQuoteDTO.getBuyOnePrice()) < 0){
                    log.info("撤低于集合的卖单stockCode{} gearLevel{}",orderCancelPool.getStockCode(),orderCancelPool.getGearType());
                    orderCancelPoolComponent.invokeCancel(buyOneorderCancelPools);
                }
            }
        }
    }

    private void runMarket(CommonQuoteDTO commonQuoteDTO){

    }

}
