package com.bazinga.loom.component;


import com.alibaba.fastjson.JSONObject;
import com.bazinga.loom.cache.InsertCacheManager;
import com.bazinga.loom.dto.CancelOrderRequestDTO;
import com.bazinga.loom.dto.OrderRequestDTO;
import com.bazinga.loom.dto.ReturnOrderDTO;
import com.bazinga.loom.model.TradeAccount;
import com.bazinga.loom.query.TradeAccountQuery;
import com.bazinga.loom.service.TradeAccountService;
import com.bazinga.util.AesUtil;
import com.tora.traderapi.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class TradeApiComponent extends CTORATstpTraderSpi implements InitializingBean {

    @Autowired
    private TradeAccountService tradeAccountService;

    @Autowired
    private OrderCancelPoolComponent orderCancelPoolComponent;

    @Autowired
    private DealOrderInfoComponent dealOrderInfoComponent;

    static
    {
        System.loadLibrary("javatraderapi");
    }
    private CTORATstpTraderApi traderApi;
    int requestId;

    /**
     * 下单
     * @param requestDTO 参数
     */
    public void insertOrder(OrderRequestDTO requestDTO){

        CTORATstpInputOrderField request = new CTORATstpInputOrderField();
        request.setExchangeID(requestDTO.getExchangeId());
        request.setSecurityID(requestDTO.getStockCode());
        request.setOrderRef(Integer.valueOf(requestDTO.getLocalSign()));
        request.setShareholderID(requestDTO.getShareholderId());
        request.setDirection(traderapi.getTORA_TSTP_D_Buy());
        request.setVolumeTotalOriginal(requestDTO.getVolume());
        request.setLimitPrice(requestDTO.getOrderPrice().doubleValue());
        request.setOrderPriceType(traderapi.getTORA_TSTP_OPT_LimitPrice());
        request.setTimeCondition(traderapi.getTORA_TSTP_TC_GFD());
        request.setVolumeCondition(traderapi.getTORA_TSTP_VC_AV());
        int result = traderApi.ReqOrderInsert(request, ++requestId);
        log.info("stockCode ={} 下单参数{} 下单结果{}", requestDTO.getStockCode(),JSONObject.toJSONString(requestDTO),result);
    }

    /**
     * 卖出
     * @param requestDTO 参数
     */
    public void sellOrder(OrderRequestDTO requestDTO){
        CTORATstpInputOrderField request = new CTORATstpInputOrderField();
        request.setExchangeID(requestDTO.getExchangeId());
        request.setSecurityID(requestDTO.getStockCode());
        request.setOrderRef(Integer.valueOf(requestDTO.getLocalSign()));
        request.setShareholderID(requestDTO.getShareholderId());
        request.setDirection(traderapi.getTORA_TSTP_D_Sell());
        request.setVolumeTotalOriginal(requestDTO.getVolume());
        request.setLimitPrice(requestDTO.getOrderPrice().doubleValue());
        request.setOrderPriceType(traderapi.getTORA_TSTP_OPT_LimitPrice());
        request.setTimeCondition(traderapi.getTORA_TSTP_TC_GFD());
        request.setVolumeCondition(traderapi.getTORA_TSTP_VC_AV());
        int result = traderApi.ReqOrderInsert(request, ++requestId);
        log.info("卖出orderRef stockCode ={} orderPrice{} orderRef{} orderQuantity{} 下单结果{}", requestDTO.getStockCode(),requestDTO.getOrderPrice(),requestDTO.getLocalSign(),requestDTO.getVolume(),result);
    }

    /**
     * 撤单
     * @param cancelOrderRequestDTO 参数
     */
    public void cancelOrder(CancelOrderRequestDTO cancelOrderRequestDTO){
        CTORATstpInputOrderActionField request = new CTORATstpInputOrderActionField();
        request.setExchangeID(cancelOrderRequestDTO.getExchangeId());
        request.setActionFlag(traderapi.getTORA_TSTP_AF_Delete());
        request.setOrderSysID(cancelOrderRequestDTO.getOrderSysId());
        request.setOrderRef(Integer.valueOf(cancelOrderRequestDTO.getOrderRef()));
       // request.setSecurityID(cancelOrderRequestDTO.getStockCode());
        int result = traderApi.ReqOrderAction(request, ++requestId);
        log.info("执行撤单 stockCode ={} 参数{} 撤单结果{}", cancelOrderRequestDTO.getStockCode(), JSONObject.toJSONString(request),result);
    }

    public void findPosition(){
        try {
            CTORATstpQryTradingAccountField qry_trading_account_field = new CTORATstpQryTradingAccountField();

            qry_trading_account_field.setInvestorID(InsertCacheManager.TRADE_ACCOUNT.getUserId());
            //qry_trading_account_field.setAccountID(InsertCacheManager.TRADE_ACCOUNT.getUserId());

            int ret = traderApi.ReqQryTradingAccount(qry_trading_account_field, ++requestId);
            log.info("可用资金拉去进入了 accountRet:{} inverstId:{} accountId:{}", ret, InsertCacheManager.TRADE_ACCOUNT.getUserId(), InsertCacheManager.TRADE_ACCOUNT.getUserId());
            if (ret != 0) {
                System.out.printf("ReqQryTradingAccount fail, ret[%d]\n", ret);
            }
        } catch(Exception e){
            log.error(e.getMessage(),e);
        }
    }

    public void OnFrontConnected()
    {
        log.info("tradeOnFrontConnected");

        CTORATstpReqUserLoginField req_user_login_field = new CTORATstpReqUserLoginField();

        req_user_login_field.setLogInAccount(InsertCacheManager.TRADE_ACCOUNT.getUserId());
        req_user_login_field.setLogInAccountType(traderapi.getTORA_TSTP_LACT_UserID());
        String password = AesUtil.decrypt(InsertCacheManager.TRADE_ACCOUNT.getTradePassword(),"singular20220724");
        log.info("解密pwd{}",password);
        req_user_login_field.setPassword(password);
        req_user_login_field.setUserProductInfo("singular");
        req_user_login_field.setTerminalInfo(InsertCacheManager.TRADE_ACCOUNT.getHdSerial());

        int ret = traderApi.ReqUserLogin(req_user_login_field, ++requestId);
        log.info("登陆结果{} 账号{} hdSerial{}",ret, InsertCacheManager.TRADE_ACCOUNT.getUserId(),InsertCacheManager.TRADE_ACCOUNT.getHdSerial());
        if (ret != 0)
        {
            System.out.printf("ReqUserLogin fail, ret[%d]\n", ret);
        }
    }

    public void stockPositionQry(String stockCode){
        CTORATstpQryPositionField qry_position_field = new CTORATstpQryPositionField();
        qry_position_field.setInvestorID(InsertCacheManager.TRADE_ACCOUNT.getUserId());
        log.info("查询持仓信息 investID：{}",InsertCacheManager.TRADE_ACCOUNT.getUserId());
        if(StringUtils.isNotBlank(stockCode)){
            qry_position_field.setSecurityID(stockCode);
        }
        int ret = traderApi.ReqQryPosition(qry_position_field, ++requestId);
        if (ret != 0) {
            log.info("ReqQryPositionfail investID：{} stockCode：{} ret:{}",InsertCacheManager.TRADE_ACCOUNT.getUserId(),stockCode,ret);
            //System.out.printf("ReqQryPosition fail, ret[%d]\n", ret);
        }
    }

    public void OnFrontDisconnected(int nReason)
    {
        System.out.printf("OnFrontDisconnected, reason[%d]\n", nReason);
    }

    public void OnRspUserLogin(CTORATstpRspUserLoginField pRspUserLoginField, CTORATstpRspInfoField pRspInfo, int nRequestID)
    {
        log.info("交易登陆回调结果 {}",pRspInfo.getErrorID());
        if (pRspInfo.getErrorID() == 0)
        {
            log.info("tradeapi login success! maxOrderRef{}",pRspUserLoginField.getMaxOrderRef());
			/*if (true)
			{
				// ��ѯ��Լ��Ϣ
				CTORATstpQrySecurityField qry_security_field = new CTORATstpQrySecurityField();

				qry_security_field.setExchangeID(traderapiConstants.TORA_TSTP_EXD_SSE);
				qry_security_field.setSecurityID("600000");

				int ret = traderApi.ReqQrySecurity(qry_security_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqQrySecurity fail, ret[%d]\n", ret);
				}
			}*/

			/*if (true)
			{
				// ��ѯͶ������Ϣ
				CTORATstpQryInvestorField qry_investor_field = new CTORATstpQryInvestorField();

				qry_investor_field.setInvestorID(InvestorID);

				int ret = traderApi.ReqQryInvestor(qry_investor_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqQryInvestor fail, ret[%d]\n", ret);
				}
			}*/

           /* if (true)
            {
                CTORATstpQryShareholderAccountField qry_shareholder_account_field = new CTORATstpQryShareholderAccountField();

                qry_shareholder_account_field.setExchangeID(traderapiConstants.TORA_TSTP_EXD_SZSE);
                qry_shareholder_account_field.setInvestorID(investorId);

                int ret = traderApi.ReqQryShareholderAccount(qry_shareholder_account_field, ++requestId);
                if (ret != 0)
                {
                    System.out.printf("ReqQryShareholderAccount fail, ret[%d]\n", ret);
                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }*/

			/*if (true)
			{
				// ��ѯ�ʽ��˺�
				CTORATstpQryTradingAccountField qry_trading_account_field = new CTORATstpQryTradingAccountField();

				qry_trading_account_field.setInvestorID(InvestorID);
				qry_trading_account_field.setAccountID(AccountID);

				int ret = traderApi.ReqQryTradingAccount(qry_trading_account_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqQryTradingAccount fail, ret[%d]\n", ret);
				}
			}*/

			/*if (true)
			{
				// ��ѯ�ֲ�
				CTORATstpQryPositionField qry_position_field = new CTORATstpQryPositionField();

				qry_position_field.setInvestorID(InvestorID);
				qry_position_field.setSecurityID("600000");

				int ret = traderApi.ReqQryPosition(qry_position_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqQryPosition fail, ret[%d]\n", ret);
				}
			}*/

			/*if (true)
			{
				// ��ѯ����
				CTORATstpQryOrderField qry_order_field = new CTORATstpQryOrderField();

				//qry_order_field.setSecurityID("600000");
				//qry_order_field.setInsertTimeStart("09:35:00");
				//qry_order_field.setInsertTimeEnd("10:30:00");

				int ret = traderApi.ReqQryOrder(qry_order_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqQryOrder fail, ret[%d]\n", ret);
				}
			}*/

			/*if (true)
			{
				// ���󱨵�
				CTORATstpInputOrderField input_order_field = new CTORATstpInputOrderField();

				input_order_field.setExchangeID(traderapiConstants.TORA_TSTP_EXD_SSE);
				input_order_field.setSecurityID("600000");
				input_order_field.setShareholderID(SSEShareholderID);
				input_order_field.setDirection(traderapiConstants.TORA_TSTP_D_Buy);
				input_order_field.setVolumeTotalOriginal(100);
				input_order_field.setLimitPrice(81.3);
				input_order_field.setOrderPriceType(traderapiConstants.TORA_TSTP_OPT_LimitPrice);
				input_order_field.setTimeCondition(traderapiConstants.TORA_TSTP_TC_GFD);
				input_order_field.setVolumeCondition(traderapiConstants.TORA_TSTP_VC_AV);

				int ret = traderApi.ReqOrderInsert(input_order_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqOrderInsert fail, ret[%d]\n", ret);
				}
			}*/

			/*if (true)
			{
				// ���󳷵�
				CTORATstpInputOrderActionField input_order_action_field = new CTORATstpInputOrderActionField();

				input_order_action_field.setExchangeID(traderapiConstants.TORA_TSTP_EXD_SSE);
				input_order_action_field.setActionFlag(traderapiConstants.TORA_TSTP_AF_Delete);
				input_order_action_field.setOrderSysID("110019400000005");

				int ret = traderApi.ReqOrderAction(input_order_action_field, ++requestId);
				if (ret != 0)
				{
					System.out.printf("ReqOrderAction fail, ret[%d]\n", ret);
				}
			}*/
        }
        else
        {
            System.out.printf("login fail, error_id[%d], error_msg[%s]\n", pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }

    public void OnRspQrySecurity(CTORATstpSecurityField pSecurity, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        if (pSecurity != null)
        {
            System.out.printf("OnRspQrySecurity[%d]: SecurityID[%s] SecurityName[%s] UpperLimitPrice[%.3f] LowerLimitPrice[%.3f]\n",
                    nRequestID, pSecurity.getSecurityID(), pSecurity.getSecurityName(),
                    pSecurity.getUpperLimitPrice(), pSecurity.getLowerLimitPrice());
        }

        if (bIsLast)
        {
            System.out.printf("��ѯ��Լ��Ϣ����!\n");
        }
    }

    public void OnRspQryInvestor(CTORATstpInvestorField pInvestor, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        if (pInvestor != null)
        {
            System.out.printf("OnRspQryInvestor[%d]: InvestorID[%s] InvestorName[%s] Operways[%s]\n",
                    nRequestID, pInvestor.getInvestorID(), pInvestor.getInvestorName(),
                    pInvestor.getOperways());
        }

        if (bIsLast)
        {
            System.out.printf("��ѯͶ������Ϣ����!\n");
        }
    }

    public void OnRspQryShareholderAccount(CTORATstpShareholderAccountField pShareholderAccount, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        if (pShareholderAccount != null)
        {
            System.out.printf("OnRspQryShareholderAccount[%d]: InvestorID[%s] ExchangeID[%c] MarketID[%c] ShareholderID[%s]\n",
                    nRequestID, pShareholderAccount.getInvestorID(), pShareholderAccount.getExchangeID(),
                    pShareholderAccount.getMarketID(), pShareholderAccount.getShareholderID());
        }

        if (bIsLast)
        {
            System.out.printf("��ѯ�ɶ��˺Ž���!\n");
        }
    }

    public void OnRspQryTradingAccount(CTORATstpTradingAccountField pTradingAccount, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        log.info("可用资金回调进入了 errorMsg：{} pTradingAccount:{}",pRspInfo.getErrorMsg(),JSONObject.toJSONString(pTradingAccount));
        try {
            if (pTradingAccount != null) {
                log.info("账户可用余额查询 available：{}", pTradingAccount.getUsefulMoney());
                double available = pTradingAccount.getUsefulMoney();
            //    sellAvailableComponent.handleAccountAvailable(new BigDecimal(available));
            }

            if (bIsLast) {
                System.out.printf("��ѯ�ʽ��˺Ž���!\n");
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    public void OnRspQryPosition(CTORATstpPositionField pPosition, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        if (pPosition != null)
        {
            log.info("仓位回查接口OnRspQryPosition:{} InvestorID：{} SecurityID：{} HistoryPos：{} TodayBSPos：{} TodayPRPos：{} a:{} b:{}",nRequestID, pPosition.getInvestorID(), pPosition.getSecurityID(),
                    pPosition.getHistoryPos(), pPosition.getTodayBSPos(), pPosition.getTodayPRPos(),pPosition.getAvailablePosition(),pPosition.getCurrentPosition());
        }

        if (bIsLast)
        {
            System.out.printf("��ѯ�ֲֽ���!\n");
        }
    }

    public void OnRspQryOrder(CTORATstpOrderField pOrder, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        if (pOrder != null)
        {
            System.out.printf("OnRspQryOrder[%d]: SecurityID[%s] OrderLocalID[%s] OrderRef[%s] OrderSysID[%s] VolumeTraded[%d] OrderStatus[%c] OrderSubmitStatus[%c] StatusMsg[%s]\n",
                    nRequestID, pOrder.getSecurityID(), pOrder.getOrderLocalID(), pOrder.getOrderRef(),
                    pOrder.getOrderSysID(), pOrder.getVolumeTraded(), pOrder.getOrderStatus(),
                    pOrder.getOrderSubmitStatus(), pOrder.getStatusMsg());
        }

        if (bIsLast)
        {
            System.out.printf("��ѯ��������!\n");
        }
    }

    public void OnRspOrderInsert(CTORATstpInputOrderField pInputOrderField, CTORATstpRspInfoField pRspInfo, int nRequestID)
    {
        if (pRspInfo.getErrorID() == 0) {
            log.info("OnRspOrderInsert: OK {} stockCode{}",	nRequestID,pInputOrderField.getSecurityID());
        }
        else {
            log.info("OnRspOrderInsert: Error! {} errorId{} errMsg{} stockCode{}", nRequestID, pRspInfo.getErrorID(), pRspInfo.getErrorMsg(),pInputOrderField.getSecurityID());
            if(pRspInfo.getErrorMsg().contains("资金不足")){
              //  orderCancelPoolComponent.change2SystemStatus(pInputOrderField.getSecurityID());
            }
        }
    }

    public void OnRtnOrder(CTORATstpOrderField pOrder)
    {
        log.info("OnRtnOrder: stockCode{} InvestorID{} SecurityID{} OrderRef{} OrderLocalID{} LimitPrice{} VolumeTotalOriginal{} OrderSysID{} direction:{} OrderStatus{}",
                pOrder.getSecurityID(), pOrder.getInvestorID(), pOrder.getSecurityID(), pOrder.getOrderRef(), pOrder.getOrderLocalID(),
                pOrder.getLimitPrice(), pOrder.getVolumeTotalOriginal(), pOrder.getOrderSysID(),pOrder.getDirection(),
                pOrder.getOrderStatus());
        ReturnOrderDTO returnOrderDTO = new ReturnOrderDTO();
        returnOrderDTO.setStockCode(pOrder.getSecurityID());
        returnOrderDTO.setLocalSign(String.valueOf(pOrder.getOrderRef()));
        returnOrderDTO.setOrderNo(pOrder.getOrderSysID());
        returnOrderDTO.setOrderStatus(pOrder.getOrderStatus());
        orderCancelPoolComponent.updateData(returnOrderDTO);
        String direction = String.valueOf(pOrder.getDirection());
        String orderStatus = String.valueOf(pOrder.getOrderStatus());

    }

    public void OnRtnTrade(CTORATstpTradeField pTrade)
    {
        log.info("已成交交易信息 stockCode：{} 订单编号：{}  成交方向：{} 成交编号：{}",pTrade.getSecurityID(),pTrade.getOrderSysID(),pTrade.getDirection(),pTrade.getTradeID());
        String direction = String.valueOf(pTrade.getDirection());

        log.info("已成交交易信息 买入 stockCode：{} 订单编号：{}  成交方向：{}",pTrade.getSecurityID(),pTrade.getOrderSysID(),pTrade.getDirection());
        dealOrderInfoComponent.dealOrderInsertPool(pTrade.getSecurityID(),pTrade.getOrderSysID(),new BigDecimal(Double.valueOf(pTrade.getPrice()).toString()));

    }

    public void OnRspOrderAction(CTORATstpInputOrderActionField pInputOrderActionField, CTORATstpRspInfoField pRspInfo, int nRequestID)
    {
        if (pRspInfo.getErrorID() == 0) {
            log.info("撤单请求回调触发成功 回调参数：{} 委托编号：{}",pInputOrderActionField.getOrderRef(),
                    pInputOrderActionField.getOrderSysID());
            int orderActionRef = pInputOrderActionField.getOrderRef();
            String orderRefStr = String.valueOf(orderActionRef);
          //  cancelOrderLogComponent.cancelSuccess(orderRefStr,pInputOrderActionField.getOrderSysID());
        } else {
            log.info("撤单请求回调触发失败 回调参数：{} 委托编号：{}  失败代码：{} 失败描述：{}",pInputOrderActionField.getOrderRef(),
                    pInputOrderActionField.getOrderSysID(),pRspInfo.getErrorID(),pRspInfo.getErrorMsg());
            int orderActionRef = pInputOrderActionField.getOrderRef();
            String orderRefStr = String.valueOf(orderActionRef);
         //   cancelOrderLogComponent.updateFailCancelOrderLog(orderRefStr);
        }
    }

    public void OnErrRtnOrderAction(CTORATstpOrderActionField pOrderAction, CTORATstpRspInfoField pRspInfo) {
        log.info("撤单失败回调接口触发 成功 买入订单委托编号：{}  回调参数：{} 失败代码：{} 失败描述", pOrderAction.getOrderSysID(),
                pOrderAction.getOrderRef(),pRspInfo.getErrorID(),pRspInfo.getErrorMsg());
    }


    public void OnRspTransferFund(CTORATstpInputTransferFundField pInputTransferFundField, CTORATstpRspInfoField pRspInfo, int nRequestID, boolean bIsLast)
    {
        if (pRspInfo.getErrorID() == 0)
        {
            System.out.printf("OnRspTransferFund: OK! [%d]\n", nRequestID);
        }
        else
        {
            System.out.printf("OnRspTransferFund: Error! [%d] [%d] [%s]\n", nRequestID, pRspInfo.getErrorID(), pRspInfo.getErrorMsg());
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        System.loadLibrary("javatraderapi");
        List<TradeAccount> tradeAccounts = tradeAccountService.listByCondition(new TradeAccountQuery());
        InsertCacheManager.TRADE_ACCOUNT = tradeAccounts.get(0);
        InsertCacheManager.TRADE_ACCOUNT.setOrderRef(InsertCacheManager.TRADE_ACCOUNT.getOrderRef()+10000L);
        tradeAccountService.updateById(InsertCacheManager.TRADE_ACCOUNT);
        InsertCacheManager.ORDER_REF = new AtomicLong(InsertCacheManager.TRADE_ACCOUNT.getOrderRef());
        traderApi = CTORATstpTraderApi.CreateTstpTraderApi();
        String apiVersion = CTORATstpTraderApi.GetApiVersion();
        log.info("apiVersion{} {}",apiVersion, traderApi);
        traderApi.RegisterSpi(this);
        traderApi.RegisterFront(InsertCacheManager.TRADE_ACCOUNT.getServerIp());
        traderApi.SubscribePrivateTopic(TORA_TE_RESUME_TYPE.TORA_TERT_RESTART);
        traderApi.SubscribePublicTopic(TORA_TE_RESUME_TYPE.TORA_TERT_RESTART);
        traderApi.Init();
    }
}
