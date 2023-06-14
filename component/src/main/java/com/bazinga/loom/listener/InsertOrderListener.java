package com.bazinga.loom.listener;


import com.alibaba.fastjson.JSONObject;

import com.bazinga.constant.CommonConstant;
import com.bazinga.constant.DateConstant;
import com.bazinga.enums.OperateStatusEnum;
import com.bazinga.enums.OrderCancelPoolStatusEnum;
import com.bazinga.loom.cache.CacheManager;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.component.TradeApiComponent;
import com.bazinga.loom.dto.DisableInsertStockDTO;
import com.bazinga.loom.dto.OrderRequestDTO;
import com.bazinga.loom.event.InsertOrderEvent;
import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.service.DisableInsertStockPoolService;
import com.bazinga.loom.service.OrderCancelPoolService;
import com.bazinga.loom.util.ExchangeIdUtil;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import com.bazinga.util.ThreadPoolUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InsertOrderListener implements ApplicationListener<InsertOrderEvent> {

    private ExecutorService SINGLE_THREAD_POOL = ThreadPoolUtils.createSingle(4);
    public static Map<String, Integer> INSERT_ORDER_TIMES_MAP = new HashMap<>(256);


    @Autowired
    private TradeApiComponent tradeApiComponent;

    @Autowired
    private OrderCancelPoolService orderCancelPoolService;

    @Autowired
    private DisableInsertStockPoolService disableInsertStockPoolService;

    @Override
    public void onApplicationEvent(InsertOrderEvent insertOrderEvent) {
        String stockCode = insertOrderEvent.getStockCode();
        Long orderTimeStamp;


        synchronized (insertOrderEvent.getStockCode()) {
            orderTimeStamp = InsertCacheManager.DUPLICATION_ORDER_MAP.get(insertOrderEvent.getStockCode() + insertOrderEvent.getGearLevel());
            InsertCacheManager.DUPLICATION_ORDER_MAP.put(insertOrderEvent.getStockCode()+ insertOrderEvent.getGearLevel(), System.currentTimeMillis());
        }
        addStock2System(stockCode);
        if (orderTimeStamp != null && System.currentTimeMillis() - orderTimeStamp < TimeUnit.SECONDS.toMillis(6)) {
            log.info("触碰重发下单 stockCode = {}", insertOrderEvent.getStockCode());
            return;
        }
        List<OrderCancelPool> orderCancelPoolList = buildOrder(insertOrderEvent);
        if(CollectionUtils.isEmpty(orderCancelPoolList)){
            log.info("未构建出订单 stockCode{}", stockCode);
            return;
        }
        addToDisableInsertPool(insertOrderEvent.getStockCode()+ insertOrderEvent.getGearLevel());
        String shareHolderId = stockCode.startsWith("6")?InsertCacheManager.TRADE_ACCOUNT.getShGdCode():InsertCacheManager.TRADE_ACCOUNT.getSzGdCode();
        synToCacheAndDb(stockCode,insertOrderEvent.getGearLevel(),orderCancelPoolList);
        for (int i = 0; i < orderCancelPoolList.size(); i++) {
            OrderCancelPool orderCancelPool = orderCancelPoolList.get(i);
            OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
            orderRequestDTO.setExchangeId(ExchangeIdUtil.getExchangeId(stockCode));
            orderRequestDTO.setOrderPrice(insertOrderEvent.getOrderPrice());
            orderRequestDTO.setStockCode(stockCode);
            orderRequestDTO.setShareholderId(shareHolderId);
            orderRequestDTO.setLocalSign(orderCancelPool.getLocalSign());
            orderRequestDTO.setVolume(orderCancelPool.getOrderQuantity());
            try {
                if(insertOrderEvent.getGearLevel()>0){
                    tradeApiComponent.insertOrder(orderRequestDTO);
                }else {
                    tradeApiComponent.sellOrder(orderRequestDTO);
                }
            } catch (Exception e) {
                log.error(e.getMessage()+"stockCode{} 下单异常",stockCode,e);
            }
        }
        SINGLE_THREAD_POOL.execute(()->{
            try {
                for (OrderCancelPool orderCancelPool : orderCancelPoolList) {
                    orderCancelPoolService.save(orderCancelPool);
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });


    }



    private void synToCacheAndDb(String stockCode, Integer gearLevel,List<OrderCancelPool> orderCancelPoolList) {
        for (OrderCancelPool orderCancelPool : orderCancelPoolList) {
            orderCancelPool.setLocalSign(InsertCacheManager.getOrderRef());
        }
        InsertCacheManager.ORDER_CANCEL_POOL_MAP.put(stockCode + gearLevel,orderCancelPoolList);
        InsertCacheManager.ORDER_CANCEL_POOL_TO_LOG_MAP.put(stockCode + gearLevel,orderCancelPoolList);

    }


    private List<OrderCancelPool> buildOrder(InsertOrderEvent insertOrderEvent) {
        List<OrderCancelPool> orderCancelPoolList = null;
        try {
            int quantity;
            BigDecimal position;
            orderCancelPoolList = Lists.newArrayList();
            if(!isStatusOrder(insertOrderEvent.getStockCode())){
                log.info("按钮拦截下单 stockCode{} 账号信息{}", insertOrderEvent.getStockCode(), JSONObject.toJSONString(InsertCacheManager.TRADE_ACCOUNT));
                return null;
            }
            INSERT_ORDER_TIMES_MAP.merge(insertOrderEvent.getStockCode(), 1, Integer::sum);




            OrderCancelPool orderCancelPool = new OrderCancelPool();
            orderCancelPool.setStockName("");
            orderCancelPool.setStockCode(insertOrderEvent.getStockCode());
            orderCancelPool.setGearType(insertOrderEvent.getGearLevel());
            orderCancelPool.setOrderPrice(insertOrderEvent.getOrderPrice());
            orderCancelPool.setStatus(OrderCancelPoolStatusEnum.INIT.getCode());
            orderCancelPool.setOrderTimeMillis(System.currentTimeMillis());
            orderCancelPool.setOrderStamp(DateTimeUtils.trans2CommonFormat(insertOrderEvent.getQuoteTime(),false));
            orderCancelPool.setRealCannonQuantity(insertOrderEvent.getCannonQuantity());
            quantity = InsertCacheManager.TRADE_ACCOUNT.getPosition().divide(CommonConstant.DECIMAL_HUNDRED,0,BigDecimal.ROUND_DOWN).divide(insertOrderEvent.getOrderPrice(),0, BigDecimal.ROUND_DOWN).intValue() * 100;

            if (quantity >= CommonConstant.HUNDRED) {
                //超过万手处理
                //单笔最大手数
                Integer maxOrderNo = InsertCacheManager.TRADE_ACCOUNT.getMaxOrderNo();
                int breakingCount = 6;
                if(insertOrderEvent.getStockCode().startsWith("3")){
                    maxOrderNo = InsertCacheManager.TRADE_ACCOUNT.getMaxOrderNo() - 700000;
                    breakingCount = 10;
                }
                int orderTimes = quantity / maxOrderNo;
                for (int i = 1; i <= orderTimes +1 && i <= breakingCount; i++) {
                    if (i == orderTimes + 1) {
                        orderCancelPool.setOrderQuantity(quantity - (orderTimes * maxOrderNo));
                        orderCancelPoolList.add(orderCancelPool);
                    } else {
                        OrderCancelPool copyOrder = new OrderCancelPool();
                        BeanUtils.copyProperties(orderCancelPool, copyOrder);
                        copyOrder.setOrderQuantity(maxOrderNo);
                        orderCancelPoolList.add(copyOrder);
                    }
                }
            } else {
                log.info("当前股票价格过高 stockCode ={}", insertOrderEvent.getStockCode());
                orderCancelPool.setOrderQuantity(100);
                orderCancelPoolList.add(orderCancelPool);
            }
        } catch (Exception e) {
            log.error("下单逻辑异常{}" + e.getMessage(),insertOrderEvent.getStockCode(),e);
        }
        return orderCancelPoolList;

    }



    public boolean isStatusOrder(String stockCode){
        Integer insertOrderTimes = INSERT_ORDER_TIMES_MAP.get(stockCode);
        return true;
    }

    private void addToDisableInsertPool(String key) {
        DisableInsertStockDTO disableInsertStockDTO = new DisableInsertStockDTO();
        disableInsertStockDTO.setStockCodeGear(key);
        disableInsertStockDTO.setOperateStatus(OperateStatusEnum.INSERT_ORDER.getCode());
        log.info("设置 stockCode ={} 为已下单状态", key);
        CacheManager.DISABLE_INSERT_STOCK_POOL.put(key, disableInsertStockDTO);
        DisableInsertStockPool selectByStockCode = disableInsertStockPoolService.getByStockCodeGear(key);
        if (selectByStockCode == null) {
            DisableInsertStockPool disableInsertStock = new DisableInsertStockPool();
            disableInsertStock.setStockCodeGear(key);
            disableInsertStock.setOperateStatus(OperateStatusEnum.INSERT_ORDER.getCode());
            SINGLE_THREAD_POOL.execute(() -> disableInsertStockPoolService.save(disableInsertStock));
        } else {
            if (OperateStatusEnum.MANUAL.getCode().intValue() != selectByStockCode.getOperateStatus()) {
                selectByStockCode.setOperateStatus(OperateStatusEnum.INSERT_ORDER.getCode());
                SINGLE_THREAD_POOL.execute(() -> disableInsertStockPoolService.updateById(selectByStockCode));
            }
        }

    }


    private void addStock2System(String stockCode) {
        if(!CacheManager.DISABLE_INSERT_STOCK_POOL.keySet().contains(stockCode)){
            log.info("设置为系统禁止状态 stockCode{}", stockCode);
            DisableInsertStockDTO disableInsertStockDTO = new DisableInsertStockDTO();
            disableInsertStockDTO.setStockCodeGear(stockCode);
            disableInsertStockDTO.setOperateStatus(OperateStatusEnum.SYSTEM.getCode());
            CacheManager.DISABLE_INSERT_STOCK_POOL.put(stockCode, disableInsertStockDTO);
        }
    }
}
