package com.bazinga.loom.component;


import com.bazinga.enums.MarketTypeEnum;
import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.query.CirculateInfoQuery;
import com.bazinga.loom.service.CirculateInfoService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ListenQuoteComponent {


    @Autowired
    private QuoteL2SHComponent quoteL2SHComponent;

    @Autowired
    private QuoteL2SZComponent quoteL2SZComponent;

    @Autowired
    private CirculateInfoService circulateInfoService;

    @Autowired
    private CommonComponent commonComponent;

    public void listenL2Quote(){
        CirculateInfoQuery query = new CirculateInfoQuery();
        query.setMarketType(MarketTypeEnum.GENERAL.getCode());
        List<CirculateInfo> circulateInfos = circulateInfoService.listByCondition(query);

        List<String> shStockCodeList = circulateInfos.stream().filter(item -> item.getStockCode().startsWith("6"))
                .map(CirculateInfo::getStockCode).collect(Collectors.toList());
        List<String> szStockCodeList = circulateInfos.stream().filter(item -> !item.getStockCode().startsWith("6"))
                .map(CirculateInfo::getStockCode).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(shStockCodeList)){
            quoteL2SHComponent.substractStockCodeList(shStockCodeList);
        }
        if(!CollectionUtils.isEmpty(szStockCodeList)){
            quoteL2SZComponent.substractStockCodeList(szStockCodeList);
        }

        Set<String> l1shStockSet = Sets.newHashSet();
        Set<String> l1szStockSet = Sets.newHashSet();
        /*if(DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().size()>0){
            log.info("初始化小池子票行情订阅{}", JSONObject.toJSONString(DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet()));
            List<String> shStockCodeList = DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().stream().filter(item -> item.startsWith("6")).collect(Collectors.toList());
            List<String> szStockCodeList = DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().stream().filter(item -> !item.startsWith("6")).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(shStockCodeList)){
                quoteL2SHComponent.substractStockCodeList(shStockCodeList);
            }
            if(!CollectionUtils.isEmpty(szStockCodeList)){
                quoteL2SZComponent.substractStockCodeList(szStockCodeList);
            }
        }*/

    }

    public void listenBestL1Quote(){
      /*  if(DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().size()>0){
            log.info("小池子票L1行情订阅{}", JSONObject.toJSONString(DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet()));
            List<String> shStockCodeList = DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().stream().filter(item -> item.startsWith("6")).collect(Collectors.toList());
            List<String> szStockCodeList = DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().stream().filter(item -> !item.startsWith("6")).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(shStockCodeList)){
                quoteL1Component.subcribeSH(shStockCodeList);
            }
            if(!CollectionUtils.isEmpty(szStockCodeList)){
                quoteL1Component.subcribeSZ(szStockCodeList);
            }
        }*/
        CirculateInfoQuery query = new CirculateInfoQuery();
        query.setMarketType(MarketTypeEnum.GENERAL.getCode());
        List<CirculateInfo> circulateInfos = circulateInfoService.listByCondition(query);

        List<String> shStockCodeList = circulateInfos.stream().filter(item -> item.getStockCode().startsWith("6"))
                .map(CirculateInfo::getStockCode).collect(Collectors.toList());
        List<String> szStockCodeList = circulateInfos.stream().filter(item -> !item.getStockCode().startsWith("6"))
                .map(CirculateInfo::getStockCode).collect(Collectors.toList());


    }



    public void  listenOne(String stockCode){
        List<String> stockCodeList = Lists.newArrayList();
        stockCodeList.add(stockCode);
        if(stockCode.startsWith("6")){
            quoteL2SHComponent.substractStockCodeList(stockCodeList);
        }else {
            quoteL2SZComponent.substractStockCodeList(stockCodeList);
        }

    }

    public void  unListenOne(String stockCode){
        List<String> stockCodeList = Lists.newArrayList();
        stockCodeList.add(stockCode);
        if(stockCode.startsWith("6")){
           // quoteL2SHComponent.unSubstractStockCodeList(stockCodeList);
        }else {
           // quoteL2SZComponent.unSubstractStockCodeList(stockCodeList);
        }

    }


}
