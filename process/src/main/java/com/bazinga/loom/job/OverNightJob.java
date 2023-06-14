package com.bazinga.loom.job;

import com.bazinga.loom.component.OverNightOrderComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OverNightJob {

    @Autowired
    private OverNightOrderComponent overNightOrderComponent;



    public void execute(){
        log.info("<--------------OverNightJob start --------------->");
        overNightOrderComponent.overNight();
        log.info("<--------------OverNightJob end --------------->");
    }
}
