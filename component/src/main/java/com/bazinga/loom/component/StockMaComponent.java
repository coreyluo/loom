package com.bazinga.loom.component;


import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.model.StockKbar;
import com.bazinga.loom.query.CirculateInfoQuery;
import com.bazinga.loom.query.StockKbarQuery;
import com.bazinga.loom.service.CirculateInfoService;
import com.bazinga.loom.service.StockKbarService;
import com.bazinga.loom.service.StockMaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class StockMaComponent {

    @Autowired
    private StockMaService stockMaService;

    @Autowired
    private CirculateInfoService circulateInfoService;

    @Autowired
    private StockKbarService stockKbarService;

    public void  save2DB(){
        List<CirculateInfo> circulateInfos = circulateInfoService.listByCondition(new CirculateInfoQuery());
        for (CirculateInfo circulateInfo : circulateInfos) {

            StockKbarQuery query = new StockKbarQuery();
            List<StockKbar> stockKbars = stockKbarService.listByCondition(query);


        }
    }
}
