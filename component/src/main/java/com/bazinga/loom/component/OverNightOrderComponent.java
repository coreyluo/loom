package com.bazinga.loom.component;


import com.bazinga.constant.CommonConstant;
import com.bazinga.loom.event.InsertOrderEvent;
import com.bazinga.loom.model.LoomStockPool;
import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.query.LoomStockPoolQuery;
import com.bazinga.loom.service.LoomStockPoolService;
import com.bazinga.loom.service.StockCloseSnapshotService;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OverNightOrderComponent {

    @Autowired
    private StockCloseSnapshotService stockCloseSnapshotService;

    @Autowired
    private LoomStockPoolService loomStockPoolService;

    @Autowired
    private CommonComponent commonComponent;

    @Autowired
    private ApplicationContext applicationContext;

    private boolean isOrder = false;

    public void overNight(){

        Date preTradeDate = commonComponent.preTradeDate(new Date());
        String preTradeDateStr = DateUtil.format(preTradeDate,DateUtil.yyyyMMdd);

        LoomStockPoolQuery loomQuery  = new LoomStockPoolQuery();
        loomQuery.setKbarDate(preTradeDateStr);
        List<LoomStockPool> loomStockPools = loomStockPoolService.listByCondition(loomQuery);
        if(CollectionUtils.isEmpty(loomStockPools)){
            log.info("织布池中无股票数据");
            return;
        }
        if(!TradeApiComponent.isConnected){
            log.info("交易未连接");
            return;
        }
        if(isOrder){
            log.info("已下隔夜单");
            return;
        }
        for (LoomStockPool loomStockPool : loomStockPools) {
            String uniqueKey = loomStockPool.getUniqueKey();
            StockCloseSnapshot byUniqueKey = stockCloseSnapshotService.getByUniqueKey(uniqueKey);
            if(byUniqueKey ==null){
                continue;
            }
            if(byUniqueKey.getBid1Price()==null|| byUniqueKey.getBid2Price()==null || byUniqueKey.getAsk1Price() ==null
                || byUniqueKey.getAsk2Price() == null ){
                continue;
            }
            if(byUniqueKey.getBid1Volume() < (byUniqueKey.getBid1Volume()+ byUniqueKey.getBid2Volume())/20){
                log.info("单子薄不挂往下顺延 stockCode{}",byUniqueKey.getStockCode());
                applicationContext.publishEvent(new InsertOrderEvent(this,loomStockPool.getStockCode(),byUniqueKey.getBid2Price(),0L,"",1));
               /* if(byUniqueKey.getBid3Price() != null){
                    applicationContext.publishEvent(new InsertOrderEvent(this,loomStockPool.getStockCode(),byUniqueKey.getBid3Price(),0L,"",2));
                }*/
            }else {
                applicationContext.publishEvent(new InsertOrderEvent(this,loomStockPool.getStockCode(),byUniqueKey.getBid1Price(),0L,"",1));
            }

            if(byUniqueKey.getAsk1Volume() < (byUniqueKey.getAsk1Volume()+ byUniqueKey.getAsk2Volume())/20){
                log.info("单子薄不挂往下顺延 stockCode{}",byUniqueKey.getStockCode());
                applicationContext.publishEvent(new InsertOrderEvent(this,loomStockPool.getStockCode(),byUniqueKey.getAsk2Price(),0L,"",-1));
              /*  if(byUniqueKey.getAsk3Price() != null){
                    applicationContext.publishEvent(new InsertOrderEvent(this,loomStockPool.getStockCode(),byUniqueKey.getAsk3Price(),0L,"",-2));
                }*/
            }else {
                applicationContext.publishEvent(new InsertOrderEvent(this,loomStockPool.getStockCode(),byUniqueKey.getAsk1Price(),0L,"",-1));
            }
        }
        isOrder = true;


    }
}
