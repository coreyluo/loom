package com.bazinga.loom.component;

import com.alibaba.fastjson.JSONObject;
import com.bazinga.base.Sort;
import com.bazinga.constant.CommonConstant;
import com.bazinga.constant.SymbolConstants;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.model.LoomStockPool;
import com.bazinga.loom.model.StockKbar;
import com.bazinga.loom.query.CirculateInfoQuery;
import com.bazinga.loom.query.LoomStockPoolQuery;
import com.bazinga.loom.query.StockKbarQuery;
import com.bazinga.loom.service.CirculateInfoService;
import com.bazinga.loom.service.LoomStockPoolService;
import com.bazinga.loom.service.StockKbarService;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
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

    @Autowired
    private CommonComponent commonComponent;

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

    public void initLoomPool() {
        log.info("初始化织布池");
        Date preTradeDate = commonComponent.preTradeDate(new Date());
        String preTradeDateStr = DateUtil.format(preTradeDate,DateUtil.yyyyMMdd);

        LoomStockPoolQuery loomQuery  = new LoomStockPoolQuery();
        loomQuery.setKbarDate(preTradeDateStr);
        List<LoomStockPool> loomStockPools = loomStockPoolService.listByCondition(loomQuery);
        if(CollectionUtils.isEmpty(loomStockPools)){
            log.info("织布池中无股票数据");
            return;
        }
        for (LoomStockPool loomStockPool : loomStockPools) {
            InsertCacheManager.LOOM_LIST.add(loomStockPool.getStockCode());
        }
        log.info("初始化后织布池{}", JSONObject.toJSONString(InsertCacheManager.LOOM_LIST));
    }
}
