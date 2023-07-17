package com.bazinga.loom.job;

import com.bazinga.loom.component.OverNightOrderComponent;
import com.bazinga.loom.component.PositionBalanceComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PositionBalanceJob {

    @Autowired
    private PositionBalanceComponent positionBalanceComponent;



    public void execute(){
        log.info("<--------------PositionBalanceJob start --------------->");
        positionBalanceComponent.closeBlance();
        log.info("<--------------PositionBalanceJob end --------------->");
    }
}
