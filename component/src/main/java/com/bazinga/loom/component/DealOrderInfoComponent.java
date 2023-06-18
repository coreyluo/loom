package com.bazinga.loom.component;



import com.bazinga.constant.DateConstant;
import com.bazinga.enums.OrderCancelPoolStatusEnum;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.query.DealOrderPoolQuery;
import com.bazinga.loom.query.OrderCancelPoolQuery;
import com.bazinga.loom.service.DealOrderPoolService;
import com.bazinga.loom.service.OrderCancelPoolService;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class DealOrderInfoComponent {

    @Autowired
    private OrderCancelPoolComponent orderCancelPoolComponent;
    @Autowired
    private OrderCancelPoolService orderCancelPoolService;
    @Autowired
    private DealOrderPoolService dealOrderPoolService;
    @Autowired
    private DisableInsertStockPoolComponent disableInsertStockPoolComponent;
    @Autowired
    private CommonComponent commonComponent;


    /**
     * 将stockCode添加  默认时间为当天
     * @param stockCode
     */
    public void dealOrderInsertPool(String stockCode, String orderNo, BigDecimal tradePrice){
        try {
            OrderCancelPoolQuery orderCancelPoolQuery = new OrderCancelPoolQuery();
            orderCancelPoolQuery.setStockCode(stockCode);
            orderCancelPoolQuery.setOrderNo(orderNo);
            List<OrderCancelPool> orderCancelPools = orderCancelPoolService.listByCondition(orderCancelPoolQuery);
            if (CollectionUtils.isEmpty(orderCancelPools)) {
                log.info("已成交交易信息 未查询到orderCancelPool存在 stockCode:{} orderNo:{} ",stockCode,orderNo);
                return;
            }
            OrderCancelPool orderCancelPool = orderCancelPools.get(0);
            String stockCodeDay = productStockCodeDay(stockCode, orderCancelPool.getGearType(), new Date());
            DealOrderPoolQuery dealOrderPoolQuery = new DealOrderPoolQuery();
            dealOrderPoolQuery.setStockCode(stockCode);
            dealOrderPoolQuery.setGearType(orderCancelPool.getGearType());
            dealOrderPoolQuery.setStockCodeGearTypeDay(stockCodeDay);
            List<DealOrderPool> dealOrderPools = dealOrderPoolService.listByCondition(dealOrderPoolQuery);
            if(!CollectionUtils.isEmpty(dealOrderPools)){
                log.info("已成交交易信息 已经入库 stockCode:{} orderNo:{} ",stockCode,orderNo);
                return;
            }
            DealOrderPool dealOrderPool = new DealOrderPool();
            dealOrderPool.setStockCode(stockCode);
            dealOrderPool.setStockName(orderCancelPool.getStockName());
            dealOrderPool.setGearType(orderCancelPool.getGearType());
            dealOrderPool.setStockCodeGearTypeDay(stockCodeDay);
            dealOrderPool.setTradePrice(tradePrice);
            dealOrderPoolService.save(dealOrderPool);
            //修改撤单池子状态
            Long firstDealTime = InsertCacheManager.ORDER_FIRST_DEAL_TIME.get(orderNo);
            if(firstDealTime==null){
                InsertCacheManager.ORDER_FIRST_DEAL_TIME.put(orderNo,new Date().getTime());
            }
            orderCancelPoolComponent.updateOrderCancelPoolStatus(stockCode, orderNo, OrderCancelPoolStatusEnum.DEAL_SUCCESS);
            log.info("已成交交易信息 更新orderCancelPool数据库数据成功 stockCode：{} orderNo:{}",stockCode,orderNo);
            orderCancelPoolComponent.removeOrderCancelPoolFromCache(stockCode, orderNo);
            log.info("已成交交易信息 删除orderCancelPool内存数据成功 stockCode：{} orderNo:{}",stockCode,orderNo);
            //放入禁止下单池

            disableInsertStockPoolComponent.addManualDisableStockCode(stockCode,orderCancelPool.getGearType());
            log.info("已成交交易信息添加禁止下单池成功 stockCode：{} orderNo:{}",stockCode,orderNo);
        } catch (Exception e){
            log.error("已成交交易信息 存入异常",e);
        }

    }


    /**
     * 将stockCode添加  默认时间为当天
     */
    public void dealDelay(){
        try {
            Map<String, Long> map = InsertCacheManager.ORDER_FIRST_DEAL_TIME;
            for (String key:map.keySet()){
                OrderCancelPoolQuery orderCancelPoolQuery = new OrderCancelPoolQuery();
                orderCancelPoolQuery.setOrderNo(key);
                List<OrderCancelPool> orderCancelPools = orderCancelPoolService.listByCondition(orderCancelPoolQuery);
                if (CollectionUtils.isEmpty(orderCancelPools)) {
                    log.info("已成交交易信息未查询到orderCancelPool存在 orderNo:{} ",key);
                    continue;
                }
                OrderCancelPool orderCancelPool = orderCancelPools.get(0);
                Long firstDealTime = map.get(key);
                if(new Date().getTime()<firstDealTime+120000){
                    log.info("已成交交易信息未查到第一笔成交后2min stockCode:{} orderNo:{} ",key);
                    continue;
                }
                if(!orderCancelPool.getStatus().equals(OrderCancelPoolStatusEnum.DEAL_SUCCESS.getCode())) {
                    orderCancelPoolComponent.updateOrderCancelPoolStatus(orderCancelPool.getStockCode(), orderCancelPool.getOrderNo(), OrderCancelPoolStatusEnum.DEAL_SUCCESS);
                    log.info("已成交交易信息 更新orderCancelPool数据库数据成功 stockCode：{} orderNo:{}", orderCancelPool.getStockCode(), orderCancelPool.getOrderNo());
                    orderCancelPoolComponent.removeOrderCancelPoolFromCache(orderCancelPool.getStockCode(), orderCancelPool.getOrderNo());
                    log.info("已成交交易信息 删除orderCancelPool内存数据成功 stockCode：{} orderNo:{}", orderCancelPool.getStockCode(), orderCancelPool.getOrderNo());
                }
            }
        } catch (Exception e){
            log.error("已成交交易信息 存入异常",e);
        }

    }

    public List<String> todayDealStock(){
        List<String> list = Lists.newArrayList();
        Date tradeDate = DateTimeUtils.getDate000000(new Date());
        if(!commonComponent.isTradeDate(new Date())||new Date().after(DateConstant.PM_15_30_00)){
            tradeDate = commonComponent.afterTradeDate(new Date());
        }
        tradeDate = DateTimeUtils.getDate000000(tradeDate);
        DealOrderPoolQuery dealOrderPoolQuery = new DealOrderPoolQuery();
        dealOrderPoolQuery.setCreateTimeFrom(tradeDate);
        List<DealOrderPool> dealOrderPools = dealOrderPoolService.listByCondition(dealOrderPoolQuery);
        if(!dealOrderPools.isEmpty()){
            for (DealOrderPool dealOrderPool:dealOrderPools){
                list.add(dealOrderPool.getStockCode());
            }
        }
        return list;
    }

    public static String productStockCodeDay(String stockCode,Integer gearType,Date date){
        return stockCode+"-"+ gearType + DateUtil.format(date,DateUtil.yyyyMMdd);
    }

}
