package com.bazinga.loom.component;


import com.bazinga.loom.cache.InsertCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DailyInitComponent {

    @Autowired
    private LoomFilterComponent  loomFilterComponent;

    public void dailyInit(){
        loomFilterComponent.initLoomPool();
    }
}
