package com.bazinga.test;

import com.bazinga.loom.component.LoomFilterComponent;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BusTest extends BaseTestCase {

    @Autowired
    private LoomFilterComponent loomFilterComponent;

    @Test
    public  void test(){
        loomFilterComponent.filterLoom();
    }
}
