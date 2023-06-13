package com.bazinga.loom.component;


import com.bazinga.constant.CommonConstant;
import com.bazinga.loom.service.StockCloseSnapshotService;
import com.bazinga.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class OverNightOrderComponent {

    @Autowired
    private StockCloseSnapshotService stockCloseSnapshotService;

    @Autowired
    private CommonComponent commonComponent;

    public void overNihgt(){

        Date preTradeDate = commonComponent.preTradeDate(new Date());
        String preTradeDateStr = DateUtil.format(preTradeDate,DateUtil.yyyyMMdd);




    }
}
