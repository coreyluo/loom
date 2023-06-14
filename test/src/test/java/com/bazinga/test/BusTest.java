package com.bazinga.test;

import com.bazinga.loom.component.LoomFilterComponent;
import com.bazinga.loom.component.OverNightOrderComponent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class BusTest extends BaseTestCase {

    @Autowired
    private LoomFilterComponent loomFilterComponent;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private OverNightOrderComponent overNightOrderComponent;

    @Test
    public  void test(){
        loomFilterComponent.filterLoom();
    }

    @Test
    public void test2(){
        overNightOrderComponent.overNight();
    }
}
