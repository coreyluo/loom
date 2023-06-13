package com.bazinga.loom.cache;

import com.alibaba.fastjson.JSONObject;

import com.bazinga.constant.SymbolConstants;
import com.bazinga.enums.OrderCancelPoolStatusEnum;
import com.bazinga.loom.component.CommonComponent;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.dto.DetailOrderDTO;
import com.bazinga.loom.dto.TransactionDTO;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.model.StockKbar;
import com.bazinga.loom.model.TradeAccount;
import com.bazinga.loom.query.OrderCancelPoolQuery;
import com.bazinga.loom.service.OrderCancelPoolService;
import com.bazinga.loom.service.StockKbarService;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import com.bazinga.util.PriceUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class InsertCacheManager implements InitializingBean {
    @Autowired
    private OrderCancelPoolService orderCancelPoolService;

    @Autowired
    private StockKbarService stockKbarService;

    @Autowired
    private CommonComponent commonComponent;

    public static Map<String,Long> DUPLICATION_ORDER_MAP = new ConcurrentHashMap<>(256);

    public static Map<String,String> OPEN_PLANK_TIME_MAP = new ConcurrentHashMap<>(256);

    public static TradeAccount TRADE_ACCOUNT;

    public static Map<String,List<OrderCancelPool>> ORDER_CANCEL_POOL_MAP = new ConcurrentHashMap<>(256);
    public static Map<String,List<OrderCancelPool>> ORDER_CANCEL_POOL_TO_LOG_MAP = new ConcurrentHashMap<>(256);
    public static Map<String,Long> ORDER_FIRST_DEAL_TIME = new ConcurrentHashMap<>(256);

    public static Map<String,List<String>> ORDER_CANCEL_POOL_TIMES_MAP = new ConcurrentHashMap<>(256);


    public static Map<String, BigDecimal> UPPER_PRICE_MAP = new ConcurrentHashMap<>(1024);




    public static AtomicLong ORDER_REF =new AtomicLong(0L);

    public static Map<String,Boolean> ZGC_INSERT_TIMES  = new ConcurrentHashMap<>(128);


    public static Map<String, TransactionDTO> LASTEST_TRANSACTION_MAP  = new ConcurrentHashMap<>(2048);

    public static List<String> FORCE_TWO_PLANK_CHEN_WEI = new ArrayList<>();

    public static Map<String,Long> CHECK_DETAIL_TRADE_TIME_MAP = new ConcurrentHashMap<>(64);

    public static Map<String,BigDecimal> GROUP_BY_PRICE_MAP = new ConcurrentHashMap<>(256);

    public static BigDecimal YESTERDAY_PLANK_CLOSE_RATE = BigDecimal.ZERO;


    public static Set<String> ROT_PLANK_TWO_SET = new HashSet<>(8);

    public static Long DELAY_300_MILLION = 0L;

    public static List<String> RISK_V2_LIST = new ArrayList<>(64);


    @Override
    public void afterPropertiesSet() throws Exception {
        initOrderCancelPool();
        log.info("初始化orderCancelPool成功 map：{}",JSONObject.toJSONString(ORDER_CANCEL_POOL_MAP));
        log.info("初始化orderCancelPoolTimes下单次数成功 map：{}",JSONObject.toJSONString(ORDER_CANCEL_POOL_TIMES_MAP));
        initYesterdayPlankCloseRate();
        log.info("初始化昨日涨停收盘指数{}",YESTERDAY_PLANK_CLOSE_RATE);

    }

    private void initYesterdayPlankCloseRate() {
        Date date = new Date();
        if(commonComponent.isTradeDate(date)){
            Date pre1TradeDate = commonComponent.preTradeDate(date);
            Date pre2TradeDate = commonComponent.preTradeDate(pre1TradeDate);
            String uniqueKey = "880863"+ SymbolConstants.UNDERLINE + DateUtil.format(pre1TradeDate,DateUtil.yyyyMMdd);
            StockKbar pre1Kbar = stockKbarService.getByUniqueKey(uniqueKey);
            uniqueKey = "880863"+ SymbolConstants.UNDERLINE + DateUtil.format(pre2TradeDate,DateUtil.yyyyMMdd);
            StockKbar pre2Kbar = stockKbarService.getByUniqueKey(uniqueKey);
            if(pre1Kbar!=null && pre2Kbar!=null){
                YESTERDAY_PLANK_CLOSE_RATE = PriceUtil.getPricePercentRate(pre1Kbar.getClosePrice().subtract(pre2Kbar.getClosePrice()),pre2Kbar.getClosePrice());
            }
        }

    }


    public static String getOrderRef(){
        return String.valueOf(ORDER_REF.incrementAndGet());
    }


    public void initOrderCancelPool(){
        OrderCancelPoolQuery query = new OrderCancelPoolQuery();
        query.setCreateTimeFrom(DateTimeUtils.getDate000000(new Date()));
        query.setCreateTimeTo(DateTimeUtils.getDate235959(new Date()));
        List<OrderCancelPool> orderCancelPools = orderCancelPoolService.listByCondition(query);
        if(CollectionUtils.isEmpty(orderCancelPools)){
            return;
        }
        for (OrderCancelPool orderCancelPool:orderCancelPools){
            String key = orderCancelPool.getStockCode() + orderCancelPool.getOrderTimeMillis();
            List<String> orderCancelPoolTimes = ORDER_CANCEL_POOL_TIMES_MAP.get(orderCancelPool.getStockCode());
            if((CollectionUtils.isEmpty(orderCancelPoolTimes))){
                orderCancelPoolTimes = new ArrayList<>();
                ORDER_CANCEL_POOL_TIMES_MAP.put(orderCancelPool.getStockCode(),orderCancelPoolTimes);
            }
            if(!orderCancelPoolTimes.contains(key)){
                orderCancelPoolTimes.add(key);
            }

            if(OrderCancelPoolStatusEnum.INIT.getCode()==orderCancelPool.getStatus()||OrderCancelPoolStatusEnum.SUCCESS.getCode()==orderCancelPool.getStatus()){
                List<OrderCancelPool> pools = ORDER_CANCEL_POOL_MAP.get(orderCancelPool.getStockCode());
                if(CollectionUtils.isEmpty(pools)){
                    pools = Lists.newArrayList();
                    ORDER_CANCEL_POOL_MAP.put(orderCancelPool.getStockCode(),pools);
                    ORDER_CANCEL_POOL_TO_LOG_MAP.put(orderCancelPool.getStockCode(),pools);
                }
                pools.add(orderCancelPool);
            }
        }
    }
    public void putOrderCancelPoolTimesMap(OrderCancelPool orderCancelPool){
            String key = orderCancelPool.getStockCode() + orderCancelPool.getOrderTimeMillis();
            List<String> orderCancelPoolTimes = ORDER_CANCEL_POOL_TIMES_MAP.get(orderCancelPool.getStockCode());
            if((CollectionUtils.isEmpty(orderCancelPoolTimes))){
                orderCancelPoolTimes = new ArrayList<>();
                ORDER_CANCEL_POOL_TIMES_MAP.put(orderCancelPool.getStockCode(),orderCancelPoolTimes);
            }
            if(!orderCancelPoolTimes.contains(key)){
                orderCancelPoolTimes.add(key);
            }
            log.info("初始化orderCancelPoolTimes下单次数成功 stockCode：{} keys：{}",orderCancelPool.getStockCode(),JSONObject.toJSONString(orderCancelPoolTimes));
        }

}
