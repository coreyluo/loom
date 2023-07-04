package com.bazinga.loom.component;


import com.bazinga.base.Sort;
import com.bazinga.constant.DateConstant;
import com.bazinga.constant.SymbolConstants;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.model.StockOpenSnapshot;
import com.bazinga.loom.query.StockOpenSnapshotQuery;
import com.bazinga.loom.service.StockCloseSnapshotService;
import com.bazinga.loom.service.StockOpenSnapshotService;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class SnapshotComponent {

    @Autowired
    private StockCloseSnapshotService stockCloseSnapshotService;

    @Autowired
    private StockOpenSnapshotService stockOpenSnapshotService;


    public Long getAvgQuantity(String stockCode){
        StockOpenSnapshotQuery query = new StockOpenSnapshotQuery();
        query.setStockCode(stockCode);
        query.addOrderBy("kbar_date", Sort.SortType.DESC);
        query.setLimit(5);
        List<StockOpenSnapshot> stockOpenSnapshots = stockOpenSnapshotService.listByCondition(query);

        if(CollectionUtils.isEmpty(stockOpenSnapshots)){
            return 0L;
        }
        long sum = stockOpenSnapshots.stream().mapToLong(StockOpenSnapshot::getTradeQuantity).sum();
        return sum/stockOpenSnapshots.size();
    }

    public void saveSnapshot(CommonQuoteDTO commonQuoteDTO){
        try {
            /*String quoteTime = DateTimeUtils.trans2CommonFormat(commonQuoteDTO.getQuoteTime(),false);
            Date quoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + commonQuoteDTO.getQuoteTime(), DateUtil.yyyyMMddHHmmssSSS);*/
            if(new Date().after(DateConstant.PM_14_58_00)){
                String kabrDate = DateUtil.format(new Date(),DateUtil.yyyyMMdd);
                String uniqueKey = commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + kabrDate;
                StockCloseSnapshot byUniqueKey = stockCloseSnapshotService.getByUniqueKey(uniqueKey);
                if(byUniqueKey !=null){
                    return;
                }
                StockCloseSnapshot stockCloseSnapshot = new StockCloseSnapshot();
                stockCloseSnapshot.setAsk1Price(commonQuoteDTO.getSellOnePrice());
                stockCloseSnapshot.setAsk2Price(commonQuoteDTO.getSellTwoPrice());
                stockCloseSnapshot.setAsk3Price(commonQuoteDTO.getSellThreePrice());
                stockCloseSnapshot.setAsk1Volume(commonQuoteDTO.getSellOneQuantity());
                stockCloseSnapshot.setAsk2Volume(commonQuoteDTO.getSellTwoQuantity());
                stockCloseSnapshot.setBid1Price(commonQuoteDTO.getBuyOnePrice());
                stockCloseSnapshot.setBid2Price(commonQuoteDTO.getBuyTwoPrice());
                stockCloseSnapshot.setBid3Price(commonQuoteDTO.getBuyThreePrice());
                stockCloseSnapshot.setBid1Volume(commonQuoteDTO.getBuyOneQuantity());
                stockCloseSnapshot.setBid2Volume(commonQuoteDTO.getBuyTwoQuantity());
                stockCloseSnapshot.setStockCode(commonQuoteDTO.getStockCode());
                stockCloseSnapshot.setStockName(commonQuoteDTO.getStockCodeName());
                stockCloseSnapshot.setKbarDate(kabrDate);
                stockCloseSnapshot.setUniqueKey(uniqueKey);
                stockCloseSnapshotService.save(stockCloseSnapshot);
                log.info("收盘快照保存成功stockCode{}",commonQuoteDTO.getStockCode());
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    }

    public void saveOpenSnapshot(CommonQuoteDTO commonQuoteDTO){
        try {
            /*String quoteTime = DateTimeUtils.trans2CommonFormat(commonQuoteDTO.getQuoteTime(),false);
            Date quoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + commonQuoteDTO.getQuoteTime(), DateUtil.yyyyMMddHHmmssSSS);*/
            if(new Date().before(DateConstant.AM_09_29_57)){
                String kabrDate = DateUtil.format(new Date(),DateUtil.yyyyMMdd);
                String uniqueKey = commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + kabrDate;
                StockOpenSnapshot byUniqueKey = stockOpenSnapshotService.getByUniqueKey(uniqueKey);
                if(byUniqueKey !=null){
                    return;
                }
                StockOpenSnapshot stockOpenSnapshot = new StockOpenSnapshot();
                stockOpenSnapshot.setAsk1Price(commonQuoteDTO.getSellOnePrice());
                stockOpenSnapshot.setAsk2Price(commonQuoteDTO.getSellTwoPrice());
                stockOpenSnapshot.setAsk3Price(commonQuoteDTO.getSellThreePrice());
                stockOpenSnapshot.setAsk1Volume(commonQuoteDTO.getSellOneQuantity());
                stockOpenSnapshot.setAsk2Volume(commonQuoteDTO.getSellTwoQuantity());
                stockOpenSnapshot.setBid1Price(commonQuoteDTO.getBuyOnePrice());
                stockOpenSnapshot.setBid2Price(commonQuoteDTO.getBuyTwoPrice());
                stockOpenSnapshot.setBid3Price(commonQuoteDTO.getBuyThreePrice());
                stockOpenSnapshot.setBid1Volume(commonQuoteDTO.getBuyOneQuantity());
                stockOpenSnapshot.setBid2Volume(commonQuoteDTO.getBuyTwoQuantity());
                stockOpenSnapshot.setStockCode(commonQuoteDTO.getStockCode());
                stockOpenSnapshot.setStockName(commonQuoteDTO.getStockCodeName());
                stockOpenSnapshot.setKbarDate(kabrDate);
                stockOpenSnapshot.setUniqueKey(uniqueKey);
                stockOpenSnapshot.setTradeQuantity(commonQuoteDTO.getTotalTradeQuantity());
                stockOpenSnapshotService.save(stockOpenSnapshot);
                log.info("收盘快照保存成功stockCode{}",commonQuoteDTO.getStockCode());
            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }


    }

}
