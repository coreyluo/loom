package com.bazinga.loom.component;

import com.alibaba.fastjson.JSONObject;

import com.bazinga.enums.MarketTypeEnum;
import com.bazinga.loom.dto.CommonQuoteDTO;
import com.bazinga.loom.dto.DetailOrderDTO;
import com.bazinga.loom.dto.TransactionDTO;
import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.model.QuoteIpConfig;
import com.bazinga.loom.query.CirculateInfoQuery;
import com.bazinga.loom.query.QuoteIpConfigQuery;
import com.bazinga.loom.service.CirculateInfoService;
import com.bazinga.loom.service.QuoteIpConfigService;
import com.tora.lev2mdapi.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QuoteL2SHComponent extends CTORATstpLev2MdSpi implements InitializingBean {


    @Autowired
    private QuoteIpConfigService quoteIpConfigService;


    @Autowired
    private CirculateInfoService circulateInfoService;

    @Autowired
    private QuoteL2BusComponent quoteL2BusComponent;

    private CTORATstpLev2MdApi lev2Api;
    private int requestId;

    static {
        System.loadLibrary("javalev2mdapi");
    }

    public void OnFrontConnected() {
        log.info("L2SH OnFrontConnected");
        CTORATstpReqUserLoginField req_user_login_field = new CTORATstpReqUserLoginField();
        int ret = lev2Api.ReqUserLogin(req_user_login_field, ++requestId);
        if (ret != 0) {
            log.error("ReqUserLogin fail, ret{}", ret);
        }
    }

    public void OnFrontDisconnected(int nReason) {
        System.out.printf("OnFrontDisconnected, reason[%d]\n", nReason);
    }

    public void substractStockCodeList(List<String> stockCodeList) {
        lev2Api.SubscribeMarketData(stockCodeList.toArray(new String[]{}), lev2mdapi.getTORA_TSTP_EXD_SSE());
        log.info("订阅L2行情成功 {}", JSONObject.toJSONString(stockCodeList));
    }

    public void unSubstractStockCodeList(List<String> stockCodeList) {
        lev2Api.UnSubscribeMarketData(stockCodeList.toArray(new String[]{}), lev2mdapi.getTORA_TSTP_EXD_SSE());
        log.info("退订L2行情成功{}", JSONObject.toJSONString(stockCodeList));
    }


    public void substractDetailTrade(List<String> stockCodeList) {
        lev2Api.SubscribeTransaction(stockCodeList.toArray(new String[]{}), lev2mdapi.getTORA_TSTP_EXD_SSE());
        log.info("订阅逐笔成交成功 {}", JSONObject.toJSONString(stockCodeList));
    }

    public void substractDetailOrder(List<String> stockCodeList) {
        lev2Api.SubscribeOrderDetail(stockCodeList.toArray(new String[]{}), lev2mdapi.getTORA_TSTP_EXD_SSE());
        log.info("订阅逐笔委托成功 {}", JSONObject.toJSONString(stockCodeList));
    }

    public void unSubstractDetailTrade(List<String> stockCodeList) {
        lev2Api.UnSubscribeTransaction(stockCodeList.toArray(new String[]{}), lev2mdapi.getTORA_TSTP_EXD_SSE());
        log.info("退订逐笔成交成功{}", JSONObject.toJSONString(stockCodeList));
    }

    public void unSubstractDetailOrder(List<String> stockCodeList) {
        lev2Api.UnSubscribeOrderDetail(stockCodeList.toArray(new String[]{}), lev2mdapi.getTORA_TSTP_EXD_SSE());
        log.info("退订逐笔成交成功{}", JSONObject.toJSONString(stockCodeList));
    }

    public void OnRspUserLogin(CTORATstpRspUserLoginField pRspUserLogin, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            log.info("l2sh login success!");
        } else {
            System.out.printf("login fail, error_id[%d] error_msg[%s]!!!\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspSubMarketData(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            log.info("subscribe market data success! stockCode{}", pSpecificSecurity.getSecurityID());
        } else {
            log.error("subscribe market data fail, error_id{} error_msg{}", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspUnSubMarketData(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            System.out.printf("unsubscribe market data success!\n");
        } else {
            System.out.printf("unsubscribe market data fail, error_id[%d] error_msg[%s]!\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspSubIndex(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            System.out.printf("subscribe index success!\n");
        } else {
            System.out.printf("subscribe index fail, error_id[%d] error_msg[%s]!\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspUnSubIndex(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            System.out.printf("unsubscribe index success!\n");
        } else {
            System.out.printf("unsubscribe index fail, error_id[%d] error_msg[%s]!\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspSubTransaction(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            log.info("subscribe transaction success! stockCode={}", pSpecificSecurity.getSecurityID());
        } else {
            log.info("subscribe transaction fail, stockCode={} error_id{} error_msg{}!", pSpecificSecurity.getSecurityID(), pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspUnSubTransaction(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            log.info("unsubscribe transaction success! stockCode{}", pSpecificSecurity.getSecurityID());
        } else {
            log.info("unsubscribe transaction fail, stockCode={} error_id{} error_msg{}!", pSpecificSecurity.getSecurityID(), pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspSubOrderDetail(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            System.out.printf("subscribe order detail success!\n");
        } else {
            System.out.printf("subscribe order detail fail, error_id[%d] error_msg[%s]!\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspUnSubOrderDetail(CTORATstpSpecificSecurityField pSpecificSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast) {
        if (pRspInfo.getErrorID() == 0) {
            System.out.printf("unsubscribe order detail success!\n");
        } else {
            System.out.printf("unsubscribe order detail fail, error_id[%d] error_msg[%s]!\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRtnMarketData(CTORATstpLev2MarketDataField pMarketData, int FirstLevelBuyNum, SWIGTYPE_p_int FirstLevelBuyOrderVolumes, int FirstLevelSellNum, SWIGTYPE_p_int FirstLevelSellOrderVolumes) {
        //  QUOTE_L2_SH_THREAD_POOL.execute(()->{
        CommonQuoteDTO commonQuoteDTO = new CommonQuoteDTO();
        commonQuoteDTO.setStockCode(pMarketData.getSecurityID());
        commonQuoteDTO.setStockCodeName("");
        commonQuoteDTO.setBuyOnePrice(new BigDecimal(pMarketData.getBidPrice1()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setBuyTwoPrice(new BigDecimal(pMarketData.getBidPrice2()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setBuyThreePrice(new BigDecimal(pMarketData.getBidPrice3()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setSellOnePrice(new BigDecimal(pMarketData.getAskPrice1()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setSellTwoPrice(new BigDecimal(pMarketData.getAskPrice2()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setSellThreePrice(new BigDecimal(pMarketData.getAskPrice3()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setBuyOneQuantity(pMarketData.getBidVolume1());
        commonQuoteDTO.setBuyTwoQuantity(pMarketData.getBidVolume2());
        commonQuoteDTO.setSellOneQuantity(pMarketData.getAskVolume1());
        commonQuoteDTO.setSellTwoQuantity(pMarketData.getAskVolume2());
        commonQuoteDTO.setBuyOneQuantity(pMarketData.getBidVolume1());
        commonQuoteDTO.setBuyTwoQuantity(pMarketData.getBidVolume2());
        commonQuoteDTO.setTotalSellQuantity(pMarketData.getTotalAskVolume());
        commonQuoteDTO.setQuoteTime(String.valueOf(pMarketData.getDataTimeStamp()));
        commonQuoteDTO.setYesterdayPrice(new BigDecimal(pMarketData.getPreClosePrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setUpperLimitPrice(new BigDecimal(pMarketData.getUpperLimitPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setTotalTradeQuantity(pMarketData.getTotalVolumeTrade());
        commonQuoteDTO.setTotalTradeMoney(new BigDecimal(pMarketData.getTotalValueTrade()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setOpenPrice(new BigDecimal(pMarketData.getOpenPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setCurrentPrice(new BigDecimal(pMarketData.getLastPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setLowestPrice(new BigDecimal(pMarketData.getLowestPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        commonQuoteDTO.setHighestPrice(new BigDecimal(pMarketData.getHighestPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        quoteL2BusComponent.dealWithQuote(commonQuoteDTO);


    }

    public void OnRtnIndex(CTORATstpLev2IndexField pIndex) {
        System.out.printf("=======OnRtnIndex======\n");
        System.out.printf("ExchangeID[%c]\n", pIndex.getExchangeID());
        System.out.printf("SecurityID[%s]\n", pIndex.getSecurityID());
        System.out.printf("LastIndex[%.3f]\n\n", pIndex.getLastIndex());
    }

    public void OnRtnTransaction(CTORATstpLev2TransactionField pTransaction) {
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setStockCode(pTransaction.getSecurityID());
        transactionDTO.setTradePrice(new BigDecimal(pTransaction.getTradePrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        transactionDTO.setTradeTime(String.valueOf(pTransaction.getTradeTime()));
        transactionDTO.setTradeQuantity(pTransaction.getTradeVolume());
        transactionDTO.setTradeType(pTransaction.getTradeBSFlag());
        //  pTransaction.get
        //log.info("逐笔成交数据{}",JSONObject.toJSONString(transactionDTO));
    }

    public void OnRtnOrderDetail(CTORATstpLev2OrderDetailField pOrderDetail) {
        DetailOrderDTO detailOrderDTO = new DetailOrderDTO();
        detailOrderDTO.setOrderTime(String.valueOf(pOrderDetail.getOrderTime()));
        detailOrderDTO.setOrderPrice(new BigDecimal(pOrderDetail.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP));
        detailOrderDTO.setStockCode(pOrderDetail.getSecurityID());
        if ('A' == pOrderDetail.getOrderStatus()) {
            detailOrderDTO.setSide(pOrderDetail.getSide() == '1' ? "B" : "S");
        } else if ('D' == pOrderDetail.getOrderStatus()) {
            detailOrderDTO.setSide(pOrderDetail.getSide() == '1' ? "BC" : "SC");
        }
        detailOrderDTO.setOrderStatus(String.valueOf(pOrderDetail.getOrderStatus()));
        detailOrderDTO.setOrderType(pOrderDetail.getOrderType());
        detailOrderDTO.setOrderQuantity(pOrderDetail.getVolume());
        detailOrderDTO.setMainSeq(pOrderDetail.getMainSeq());
        detailOrderDTO.setSubSeq(pOrderDetail.getSubSeq());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        QuoteIpConfigQuery query = new QuoteIpConfigQuery();
        query.setQuoteType(2);
        List<QuoteIpConfig> quoteIpConfigs = quoteIpConfigService.listByCondition(query);
        query.setQuoteType(4);
        List<QuoteIpConfig> localIpList = quoteIpConfigService.listByCondition(query);
        if (CollectionUtils.isEmpty(quoteIpConfigs)) {
            throw new Exception("ip未配置");
        }
        CirculateInfoQuery circulateInfoQuery = new CirculateInfoQuery();
        circulateInfoQuery.setMarketType(MarketTypeEnum.GENERAL.getCode());
        List<CirculateInfo> circulateInfos = circulateInfoService.listByCondition(circulateInfoQuery);

        List<String> shStockCodeList = circulateInfos.stream().filter(item -> item.getStockCode().startsWith("6"))
                .map(CirculateInfo::getStockCode).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(shStockCodeList)) {
            log.info("股票池无上海票无需新建上海L2实例");
            return;
        }
        log.info("L2行情包版本 {}", CTORATstpLev2MdApi.GetApiVersion());
        if (false) {
            log.info("使用组播方式 serverIp{} localIp{}", quoteIpConfigs.get(0).getServerIp(), localIpList.get(0).getServerIp());
            lev2Api = CTORATstpLev2MdApi.CreateTstpLev2MdApi(lev2mdapi.getTORA_TSTP_MST_MCAST());
            lev2Api.RegisterSpi(this);
            lev2Api.RegisterMulticast(quoteIpConfigs.get(0).getServerIp(), localIpList.get(0).getServerIp(), null);
        } else {
            log.info("使用tcp方式 serverIp{}", quoteIpConfigs.get(0).getServerIp());
            lev2Api = CTORATstpLev2MdApi.CreateTstpLev2MdApi();
            lev2Api.RegisterSpi(this);
            lev2Api.RegisterFront(quoteIpConfigs.get(0).getServerIp());
        }
        lev2Api.Init();
    }
}
