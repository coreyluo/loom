package com.bazinga.loom.component;


import com.bazinga.constant.DateConstant;
import com.bazinga.constant.SymbolConstants;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.service.StockCloseSnapshotService;
import com.bazinga.util.DateTimeUtils;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class SnapshotComponent {

    @Autowired
    private StockCloseSnapshotService stockCloseSnapshotService;

    public void saveSnapshot(CommonQuoteDTO commonQuoteDTO){
        try {
            /*String quoteTime = DateTimeUtils.trans2CommonFormat(commonQuoteDTO.getQuoteTime(),false);
            Date quoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + commonQuoteDTO.getQuoteTime(), DateUtil.yyyyMMddHHmmssSSS);*/
            if(new Date().after(DateConstant.PM_15_00_03)){
                String kabrDate = DateUtil.format(new Date(),DateUtil.yyyyMMdd);
                String uniqueKey = commonQuoteDTO.getStockCode() + SymbolConstants.UNDERLINE + kabrDate;
                StockCloseSnapshot byUniqueKey = stockCloseSnapshotService.getByUniqueKey(uniqueKey);
                if(byUniqueKey !=null){
                    return;
                }
                StockCloseSnapshot stockCloseSnapshot = new StockCloseSnapshot();
                stockCloseSnapshot.setAsk1Price(commonQuoteDTO.getSellOnePrice());
                stockCloseSnapshot.setAsk2Price(commonQuoteDTO.getSellTwoPrice());
                stockCloseSnapshot.setAsk1Volume(commonQuoteDTO.getSellOneQuantity());
                stockCloseSnapshot.setAsk2Volume(commonQuoteDTO.getSellTwoQuantity());
                stockCloseSnapshot.setBid1Price(commonQuoteDTO.getBuyOnePrice());
                stockCloseSnapshot.setBid2Price(commonQuoteDTO.getBuyTwoPrice());
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

}
