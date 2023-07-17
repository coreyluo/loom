package com.bazinga.loom.component;


import com.bazinga.loom.cache.CacheManager;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.dto.OrderRequestDTO;
import com.bazinga.loom.event.InsertOrderEvent;
import com.bazinga.loom.util.ExchangeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class PositionBalanceComponent {

    @Autowired
    private TradeApiComponent tradeApiComponent;

    @Autowired
    private ApplicationContext applicationContext;

    public void closeBlance(){
        tradeApiComponent.stockPositionQry("");
    }

    public void justPosition(String stockCode,Integer currentPosition){
        String shareHolderId = stockCode.startsWith("6")?InsertCacheManager.TRADE_ACCOUNT.getShGdCode():InsertCacheManager.TRADE_ACCOUNT.getSzGdCode();
        CommonQuoteDTO commonQuoteDTO = CacheManager.LAST_QUOTE_MAP.get(stockCode);
        if(InsertCacheManager.LOOM_LIST.contains(stockCode)){
            BigDecimal position = InsertCacheManager.TRADE_ACCOUNT.getPosition();
            OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
            orderRequestDTO.setExchangeId(ExchangeIdUtil.getExchangeId(stockCode));
            orderRequestDTO.setStockCode(stockCode);
            orderRequestDTO.setShareholderId(shareHolderId);
            orderRequestDTO.setLocalSign(InsertCacheManager.getOrderRef());
            if(currentPosition>position.intValue()){
                if(commonQuoteDTO.getBuyOnePrice().compareTo(BigDecimal.ZERO)==0){
                    log.info("当前票跌停stockCode{}",stockCode);
                    return;
                }
                log.info("动态补齐仓位stockCode{}",stockCode);
                orderRequestDTO.setOrderPrice(commonQuoteDTO.getBuyOnePrice());
                orderRequestDTO.setVolume(currentPosition-position.intValue());
                tradeApiComponent.sellOrder(orderRequestDTO);
            }
            if(currentPosition<position.intValue()){
                log.info("动态补齐仓位stockCode{}",stockCode);
                orderRequestDTO.setOrderPrice(commonQuoteDTO.getSellOnePrice());
                orderRequestDTO.setVolume(position.intValue()-currentPosition);
                tradeApiComponent.insertOrder(orderRequestDTO);
            }
        }else {
            BigDecimal position = InsertCacheManager.TRADE_ACCOUNT.getPosition();
            OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
            orderRequestDTO.setExchangeId(ExchangeIdUtil.getExchangeId(stockCode));
            orderRequestDTO.setStockCode(stockCode);
            orderRequestDTO.setShareholderId(shareHolderId);
            orderRequestDTO.setLocalSign(InsertCacheManager.getOrderRef());
            log.info("首次补齐仓位stockCode{}",stockCode);
            orderRequestDTO.setOrderPrice(commonQuoteDTO.getSellOnePrice());
            orderRequestDTO.setVolume(position.intValue());
            tradeApiComponent.insertOrder(orderRequestDTO);
        }

    }

}
