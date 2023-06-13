package com.bazinga.loom.component;



import com.bazinga.base.Sort;
import com.bazinga.constant.CommonConstant;
import com.bazinga.loom.cache.CacheManager;
import com.bazinga.loom.model.TradeDatePool;
import com.bazinga.loom.query.TradeDatePoolQuery;
import com.bazinga.loom.service.TradeDatePoolService;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CommonComponent {
    @Autowired
    private TradeDatePoolService tradeDatePoolService;



    public Date preTradeDate(Date date){
        TradeDatePoolQuery query = new TradeDatePoolQuery();
        query.setTradeDateTo(DateTimeUtils.getDate000000(date));
        query.addOrderBy("trade_date", Sort.SortType.DESC);
        List<TradeDatePool> dates = tradeDatePoolService.listByCondition(query);
        if(CollectionUtils.isEmpty(dates)){
            return new Date();
        }
        return dates.get(0).getTradeDate();
    }

    public Date afterTradeDate(Date date){
        TradeDatePoolQuery query = new TradeDatePoolQuery();
        query.setTradeDateFrom(DateTimeUtils.getDate235959(date));
        query.addOrderBy("trade_date",Sort.SortType.ASC);
        List<TradeDatePool> dates = tradeDatePoolService.listByCondition(query);
        if(CollectionUtils.isEmpty(dates)){
            return null;
        }
        return dates.get(0).getTradeDate();
    }

    public List<TradeDatePool> allHistoryTradeDate(Date date){
        TradeDatePoolQuery query = new TradeDatePoolQuery();
        query.setTradeDateTo(DateTimeUtils.getDate000000(date));
        query.addOrderBy("trade_date",Sort.SortType.DESC);
        List<TradeDatePool> dates = tradeDatePoolService.listByCondition(query);
        return dates;
    }

    public List<TradeDatePool> preNTradeDate(Date date,Integer count){
        TradeDatePoolQuery query = new TradeDatePoolQuery();
        query.setTradeDateTo(DateTimeUtils.getDate235959(date));
        query.addOrderBy("trade_date",Sort.SortType.DESC);
        query.setLimit(count);
        List<TradeDatePool> dates = tradeDatePoolService.listByCondition(query);
        return dates;
    }

    public List<TradeDatePool> allTradeDateHaveSelf(Date date){
        TradeDatePoolQuery query = new TradeDatePoolQuery();
        query.setTradeDateTo(DateTimeUtils.getDate235959(date));
        query.addOrderBy("trade_date",Sort.SortType.DESC);
        List<TradeDatePool> dates = tradeDatePoolService.listByCondition(query);
        return dates;
    }


    public static boolean idSuddenPrice(BigDecimal sellOnePrice,BigDecimal yestodayPrice){
        if(sellOnePrice==null){
            return false;
        }
        return sellOnePrice.compareTo(yestodayPrice.multiply(CommonConstant.SUDDEN_RATE).setScale(2, BigDecimal.ROUND_HALF_UP)) == 0;
    }

    public boolean isTradeDate(Date date) {

        Integer value = CacheManager.TRADE_DATE_MAP.get(DateUtil.format(date, DateUtil.yyyy_MM_dd));
        if(value!=null){
            return true;
        }
        return false;
    }




    @Data
    class BoundaryResultDTO{
        private Integer quoteTimeInteger;
        private Long twoBQuantity;

        public BoundaryResultDTO(Integer quoteTimeInteger, Long twoBQuantity) {
            this.quoteTimeInteger = quoteTimeInteger;
            this.twoBQuantity = twoBQuantity;
        }
    }


}
