package com.bazinga.loom.job;

import com.bazinga.loom.component.LoomFilterComponent;
import com.bazinga.loom.component.TradeApiComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetryInitTradeApiJob {

    @Autowired
    private TradeApiComponent tradeApiComponent;

    @Autowired
    private LoomFilterComponent loomFilterComponent;

    public void execute(){
        log.info("<--------------RetryInitTradeApiJob start --------------->");
        try {
            tradeApiComponent.reInit();
            loomFilterComponent.initLoomPool();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        log.info("<--------------RetryInitTradeApiJob end --------------->");
    }
}
