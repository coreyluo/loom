package com.bazinga.loom.component;

import com.bazinga.base.Sort;
import com.bazinga.constant.CommonConstant;
import com.bazinga.constant.SymbolConstants;
import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.model.LoomStockPool;
import com.bazinga.loom.model.StockKbar;
import com.bazinga.loom.query.CirculateInfoQuery;
import com.bazinga.loom.query.StockKbarQuery;
import com.bazinga.loom.service.CirculateInfoService;
import com.bazinga.loom.service.LoomStockPoolService;
import com.bazinga.loom.service.StockKbarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@Slf4j
public class LoomFilterComponent {

    @Autowired
    private StockKbarService stockKbarService;

    @Autowired
    private CirculateInfoService circulateInfoService;

    @Autowired
    private LoomStockPoolService loomStockPoolService;

    public void filterLoom(){

        List<CirculateInfo> circulateInfos = circulateInfoService.listByCondition(new CirculateInfoQuery());
        for (CirculateInfo circulateInfo : circulateInfos) {
            StockKbarQuery query = new StockKbarQuery();
            query.setStockCode(circulateInfo.getStockCode());
            query.addOrderBy("kbar_date", Sort.SortType.DESC);
            query.setLimit(5);
            List<StockKbar> stockKbars = stockKbarService.listByCondition(query);
            if(CollectionUtils.isEmpty(stockKbars) || stockKbars.size()<5){
                continue;
            }
            StockKbar lastStockKbar = stockKbars.get(0);
            if(lastStockKbar.getClosePrice().compareTo( CommonConstant.DECIMAL_TWO) >= 0){
                continue;
            }
            BigDecimal sumTotalAmount = stockKbars.stream().map(StockKbar::getTradeAmount).reduce(BigDecimal::add).get();
            try {
                if(sumTotalAmount.divide(new BigDecimal("5"),0, RoundingMode.HALF_UP).compareTo(CommonConstant.DECIMAL_POINT_EIGHT_BILLION)>0){
                    log.info("符合进入织布池条件stockCode{}",circulateInfo.getStockCode());
                    String uniqueKey = circulateInfo.getStockCode() + SymbolConstants.UNDERLINE + lastStockKbar.getKbarDate();
                    LoomStockPool loomStockPool = new LoomStockPool();
                    loomStockPool.setStockCode(circulateInfo.getStockCode());
                    loomStockPool.setStockName(circulateInfo.getStockName());
                    loomStockPool.setKbarDate(lastStockKbar.getKbarDate());
                    loomStockPool.setUniqueKey(uniqueKey);
                    loomStockPoolService.save(loomStockPool);
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }

        }


    }
}
