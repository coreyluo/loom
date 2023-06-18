package com.bazinga.loom.component;


import com.alibaba.fastjson.JSONObject;
import com.bazinga.enums.OperateStatusEnum;
import com.bazinga.enums.OrderCancelPoolStatusEnum;
import com.bazinga.loom.cache.CacheManager;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.dto.CancelOrderRequestDTO;
import com.bazinga.loom.dto.DisableInsertStockDTO;
import com.bazinga.loom.dto.ReturnOrderDTO;
import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.query.OrderCancelPoolQuery;
import com.bazinga.loom.service.DisableInsertStockPoolService;
import com.bazinga.loom.service.OrderCancelPoolService;
import com.bazinga.loom.util.ExchangeIdUtil;
import com.bazinga.util.DateTimeUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class OrderCancelPoolComponent {

    @Autowired
    private OrderCancelPoolService orderCancelPoolService;

    @Autowired
    private DisableInsertStockPoolService disableInsertStockPoolService;

    @Autowired
    private TradeApiComponent tradeApiComponent;

    public void change2SystemStatus(String stockCode){
        try {
            if(CacheManager.DISABLE_INSERT_STOCK_POOL.keySet().contains(stockCode)){
                DisableInsertStockDTO disableInsertStockDTO = CacheManager.DISABLE_INSERT_STOCK_POOL.get(stockCode);
                if(OperateStatusEnum.INSERT_ORDER.getCode().equals(disableInsertStockDTO.getOperateStatus())){
                    log.info("可用资金不足 更改下单状态为系统状态 stockCode{}",stockCode);
                    disableInsertStockDTO.setOperateStatus(OperateStatusEnum.SYSTEM.getCode());
                    TimeUnit.SECONDS.sleep(1);
                    DisableInsertStockPool disableInsertStockPool = disableInsertStockPoolService.getByStockCodeGear(stockCode);
                    disableInsertStockPool.setOperateStatus(OperateStatusEnum.SYSTEM.getCode());
                    disableInsertStockPoolService.updateById(disableInsertStockPool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

    }

    public void invokeCancel(List<OrderCancelPool> orderCancelPools){
        List<CancelOrderRequestDTO> cancelOrderRequestDTOS = buildCancelOrderRequestDTOS(orderCancelPools);
        for (CancelOrderRequestDTO cancelOrderRequestDTO : cancelOrderRequestDTOS) {
          //  CancelCacheManager.cleanCancelMap(orderCancelDto.getStockCode());
            tradeApiComponent.cancelOrder(cancelOrderRequestDTO);
            log.info("触发撤单条件 stockCode ={}", cancelOrderRequestDTO.getStockCode());
        }
    }

    public List<CancelOrderRequestDTO> buildCancelOrderRequestDTOS(List<OrderCancelPool> orderCancelPools){
        List<CancelOrderRequestDTO>  list = new ArrayList<>(4);
        OrderCancelPool first = orderCancelPools.get(0);
        String stockCode = first.getStockCode();
        int i = 0;
        for (OrderCancelPool orderCancelPool:orderCancelPools) {
            i++;
            String localSign = InsertCacheManager.getOrderRef();
            CancelOrderRequestDTO cancelOrderRequestDTO = new CancelOrderRequestDTO();
            cancelOrderRequestDTO.setExchangeId(ExchangeIdUtil.getExchangeId(stockCode));
            cancelOrderRequestDTO.setStockCode(stockCode);
            cancelOrderRequestDTO.setOrderSysId(orderCancelPool.getOrderNo());
            cancelOrderRequestDTO.setOrderRef(localSign);
            cancelOrderRequestDTO.setOrderQuantity(orderCancelPool.getOrderQuantity());
            list.add(cancelOrderRequestDTO);
            /*cancelOrderLogComponent.saveInitCancelOrderLog(orderCancelDto.getStockCode(),orderCancelPool.getStockName(),orderCancelPool.getOrderNo(),localSign,
                    cancelStrategyResult.getCancelStrategyCode(),cancelStrategyResult.getStrategyValue());*/
        }
        return list;
    }


    public void updateData(ReturnOrderDTO returnOrderDTO){
        List<OrderCancelPool> orderCancelPoolList = new ArrayList<>();
        for (int i = -2; i < 3; i++) {
            if(i==0){
                continue;
            }
            List<OrderCancelPool> orderCancelPools = InsertCacheManager.ORDER_CANCEL_POOL_MAP.get(returnOrderDTO.getStockCode() + i);
            if(!CollectionUtils.isEmpty(orderCancelPools)){
                orderCancelPoolList.addAll(orderCancelPools);
            }
        }
        if(CollectionUtils.isEmpty(orderCancelPoolList)){
            log.info("未存缓存中找到委托单子 stockCode{}", returnOrderDTO.getStockCode());
            return;
        }
        try {
            for (OrderCancelPool orderCancelPool : orderCancelPoolList) {
                if(orderCancelPool.getLocalSign().equals(returnOrderDTO.getLocalSign())){
                    if(OrderCancelPoolStatusEnum.INIT.getCode().equals(orderCancelPool.getStatus())){
                        orderCancelPool.setStatus(OrderCancelPoolStatusEnum.SUCCESS.getCode());
                        orderCancelPool.setOrderNo(returnOrderDTO.getOrderNo());
                        log.info("stockCode{} orderNo{} orderRef{} 返回委托编号时间距离下单时间毫秒数{}", orderCancelPool.getStockCode()
                                ,returnOrderDTO.getOrderNo(),returnOrderDTO.getLocalSign(), System.currentTimeMillis()-orderCancelPool.getOrderTimeMillis());
                        TimeUnit.MILLISECONDS.sleep(100);
                        orderCancelPoolService.updateById(orderCancelPool);
                        log.info("更新委托编号成功 stockCode{} orderNo{}", returnOrderDTO.getStockCode(),returnOrderDTO.getOrderNo());
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    }

    public void updateOrderCancelPoolStatus(String stockCode,String orderNo,OrderCancelPoolStatusEnum orderCancelPoolStatusEnum){
        OrderCancelPoolQuery query = new OrderCancelPoolQuery();
        query.setStockCode(stockCode);
        query.setOrderNo(orderNo);
        List<OrderCancelPool> orderCancelPools = orderCancelPoolService.listByCondition(query);
        if(CollectionUtils.isEmpty(orderCancelPools)){
            return;
        }
        OrderCancelPool orderCancelPool = orderCancelPools.get(0);
        orderCancelPool.setStatus(orderCancelPoolStatusEnum.getCode());
        orderCancelPoolService.updateById(orderCancelPool);
    }

    public void removeOrderCancelPoolFromCache(String stockCode,String orderNo){
        List<OrderCancelPool> orderCancelPools = InsertCacheManager.ORDER_CANCEL_POOL_MAP.get(stockCode);
        log.info("移除内存orderCancelPool数据前 stockCode:{} orderNo:{} map：{}", stockCode,orderNo,JSONObject.toJSONString(orderCancelPools));
        if(CollectionUtils.isEmpty(orderCancelPools)){
            return;
        }
        Integer index  = null;
        int i = 0;
        for (OrderCancelPool orderCancelPool:orderCancelPools){
            if(orderNo.equals(orderCancelPool.getOrderNo())){
                index = i;
                log.info("移除内存orderCancelPool数据中前 stockCode:{} orderNo:{} index:{} map：{}", stockCode,orderNo,index,JSONObject.toJSONString(orderCancelPools));
            }
            i++;
        }
        if(index!=null){
            log.info("移除内存orderCancelPool数据中后 stockCode:{} orderNo:{} index:{} map：{}", stockCode,orderNo,index,JSONObject.toJSONString(orderCancelPools));
            int removeIndex = index;
            orderCancelPools.remove(removeIndex);

        }
        log.info("移除内存orderCancelPool数据后 stockCode:{} orderNo:{} map：{}", stockCode,orderNo,JSONObject.toJSONString(orderCancelPools));
        if(CollectionUtils.isEmpty(orderCancelPools)){
            InsertCacheManager.ORDER_CANCEL_POOL_MAP.remove(stockCode);
        }
    }

    public static void main(String[] args) {

        Map<String, List<OrderCancelPool>> map = new HashMap<>();
        OrderCancelPool orderCancelPool1 = new OrderCancelPool();
        OrderCancelPool orderCancelPool2 = new OrderCancelPool();
        OrderCancelPool orderCancelPool3 = new OrderCancelPool();
        OrderCancelPool orderCancelPool4 = new OrderCancelPool();
        List<OrderCancelPool> list = Lists.newArrayList(orderCancelPool1);
        map.put("000001",list);

        List<OrderCancelPool> orderCancelPools = map.get("000001");
        OrderCancelPool remove = orderCancelPools.remove(0);
        List<OrderCancelPool> orderCancelPools1 = map.get("000001");
        System.out.println("1111111111");
    }

    public List<OrderCancelPool> listWaitingCancelOrderByDay(String stockCode, Date day){
        Date start = DateTimeUtils.getDate000000(day);
        Date end = DateTimeUtils.getDate235959(day);
        OrderCancelPoolQuery query = new OrderCancelPoolQuery();
        if(StringUtils.isNotBlank(stockCode)) {
            query.setStockCode(stockCode);
        }
        query.setCreateTimeFrom(start);
        query.setCreateTimeTo(end);
        List<OrderCancelPool> pools = orderCancelPoolService.listByCondition(query);
        List<OrderCancelPool> list = filterInitAndStopCancel(pools);
        return list;
    }

    public List<OrderCancelPool> filterInitAndStopCancel(List<OrderCancelPool> pools){
        ArrayList<OrderCancelPool> list = new ArrayList<>();
        if(org.apache.commons.collections.CollectionUtils.isEmpty(pools)){
            return list;
        }
        pools.forEach(pool->{
            if(pool.getStatus()!=null&&StringUtils.isNotBlank(pool.getOrderNo())&&(pool.getStatus()== OrderCancelPoolStatusEnum.INIT.getCode()||pool.getStatus()==OrderCancelPoolStatusEnum.SUCCESS.getCode()
                    ||pool.getStatus()==OrderCancelPoolStatusEnum.STOP_CANCEL.getCode())){
                list.add(pool);
            }
        });
        return list;
    }

    public boolean stopCancelHandle(Long id){
        OrderCancelPool pool = orderCancelPoolService.getById(id);
        pool.setStatus(OrderCancelPoolStatusEnum.STOP_CANCEL.getCode());
        orderCancelPoolService.updateById(pool);
        Map<String, List<OrderCancelPool>> orderCancelPoolMap = InsertCacheManager.ORDER_CANCEL_POOL_MAP;
        List<OrderCancelPool> orderCancelPools = orderCancelPoolMap.get(pool.getStockCode());
        if(!CollectionUtils.isEmpty(orderCancelPools)){
            for (OrderCancelPool orderCancelPool:orderCancelPools){
                if(orderCancelPool.getOrderNo().equals(pool.getOrderNo())){
                    orderCancelPool.setStatus(OrderCancelPoolStatusEnum.STOP_CANCEL.getCode());
                }
            }
        }
        return true;
    }

    public boolean resume(Long id){
        OrderCancelPool pool = orderCancelPoolService.getById(id);
        if(pool!=null&&pool.getStatus()==OrderCancelPoolStatusEnum.STOP_CANCEL.getCode()) {
            pool.setStatus(OrderCancelPoolStatusEnum.SUCCESS.getCode());
            orderCancelPoolService.updateById(pool);

            Map<String, List<OrderCancelPool>> orderCancelPoolMap = InsertCacheManager.ORDER_CANCEL_POOL_MAP;
            List<OrderCancelPool> orderCancelPools = orderCancelPoolMap.get(pool.getStockCode());
            if(!CollectionUtils.isEmpty(orderCancelPools)){
                for (OrderCancelPool orderCancelPool:orderCancelPools){
                    if(orderCancelPool.getOrderNo().equals(pool.getOrderNo())){
                        orderCancelPool.setStatus(OrderCancelPoolStatusEnum.SUCCESS.getCode());
                    }
                }
            }

        }
        return true;
    }


}
