package com.bazinga.loom.cache;

import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.dto.DisableInsertStockDTO;
import com.bazinga.loom.model.TradeDatePool;
import com.bazinga.loom.query.TradeDatePoolQuery;
import com.bazinga.loom.service.TradeDatePoolService;
import com.bazinga.queue.LimitQueue;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CacheManager implements InitializingBean {

    @Autowired
    private TradeDatePoolService tradeDatePoolService;


    public static final Map<String, Integer> TRADE_DATE_MAP = new ConcurrentHashMap<>();
    public static final Map<String, DisableInsertStockDTO> DISABLE_INSERT_STOCK_POOL = new ConcurrentHashMap<>();

    public static Map<String, LimitQueue<Long>>  STOCK_PRICE_LIMIT_QUEUE_MAP= new ConcurrentHashMap<>();

    public static final Map<String , CommonQuoteDTO> LAST_QUOTE_MAP = new ConcurrentHashMap<>(64);

    public void initTradeDate(){
        TradeDatePoolQuery query = new TradeDatePoolQuery();
        List<TradeDatePool> tradeDatePools = tradeDatePoolService.listByCondition(query);
        for(TradeDatePool tradeDatePool:tradeDatePools){
            CacheManager.TRADE_DATE_MAP.put(DateUtil.format(tradeDatePool.getTradeDate(),DateUtil.yyyy_MM_dd),1);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initTradeDate();
        log.info("初始化交易日期成功");
    }
}
