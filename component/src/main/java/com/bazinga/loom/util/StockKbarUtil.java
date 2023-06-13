package com.bazinga.loom.util;

import com.bazinga.loom.model.StockKbar;
import com.bazinga.util.PriceUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class StockKbarUtil {


    /**
     * 判断是否涨停
     * @param stockKbar 当前交易日
     * @param preStockKbar 上一个交易日
     * @return 是否涨停
     */
    public static boolean isUpperPrice(StockKbar stockKbar, StockKbar preStockKbar){
        if(stockKbar.getAdjFactor().compareTo(preStockKbar.getAdjFactor())==0){
            return PriceUtil.isUpperPrice(stockKbar.getStockCode(),stockKbar.getClosePrice(),preStockKbar.getClosePrice());
        }else {
            return PriceUtil.isUpperPrice(stockKbar.getStockCode(),stockKbar.getAdjClosePrice(),preStockKbar.getAdjClosePrice());
        }
    }
    public static boolean is10UpperPrice(StockKbar stockKbar, StockKbar preStockKbar){
        if(stockKbar.getAdjFactor().compareTo(preStockKbar.getAdjFactor())==0){
            return PriceUtil.isUpperPrice(stockKbar.getClosePrice(),preStockKbar.getClosePrice());
        }else {
            return PriceUtil.isUpperPrice(stockKbar.getAdjClosePrice(),preStockKbar.getAdjClosePrice());
        }
    }

    public static boolean isHighUpperPrice(StockKbar stockKbar, StockKbar preStockKbar){
        if(stockKbar.getAdjFactor().compareTo(preStockKbar.getAdjFactor())==0){
            return PriceUtil.isUpperPrice(stockKbar.getStockCode(),stockKbar.getHighPrice(),preStockKbar.getClosePrice());
        }else {
            return PriceUtil.isUpperPrice(stockKbar.getStockCode(),stockKbar.getAdjHighPrice(),preStockKbar.getAdjClosePrice());
        }
    }

    public static BigDecimal getNDaysUpperRateDesc(List<StockKbar> stockKbarList45, int i) {
        StockKbar stockKbar = stockKbarList45.get(i);
        StockKbar currentStockKbar = stockKbarList45.get(0);

        return PriceUtil.getPricePercentRate(currentStockKbar.getAdjClosePrice().subtract(stockKbar.getAdjClosePrice()), stockKbar.getAdjClosePrice());
    }

    public static BigDecimal getNDaysUpperRate(List<StockKbar> stockKbarList45, int i) {
        StockKbar stockKbar = stockKbarList45.get(stockKbarList45.size() - i-1);
        StockKbar currentStockKbar = stockKbarList45.get(stockKbarList45.size() - 1);

        return PriceUtil.getPricePercentRate(currentStockKbar.getAdjClosePrice().subtract(stockKbar.getAdjClosePrice()), stockKbar.getAdjClosePrice());
    }

    public static BigDecimal getNDaysLowCloseRate(List<StockKbar> stockKbarList45, int i) {
        StockKbar currentStockKbar = stockKbarList45.get(stockKbarList45.size() - 1);
        BigDecimal lowPrice = currentStockKbar.getAdjLowPrice();
        for (int j = 1; j < i; j++) {
            StockKbar stockKbar = stockKbarList45.get(stockKbarList45.size() - j);
            if(lowPrice.compareTo(stockKbar.getLowPrice())>0){
                lowPrice = stockKbar.getAdjLowPrice();
            }
        }
        StockKbar stockKbar  = stockKbarList45.get(stockKbarList45.size()-i-1);
        return PriceUtil.getPricePercentRate(currentStockKbar.getAdjClosePrice().subtract(lowPrice), stockKbar.getAdjOpenPrice());
    }
}
