package com.bazinga.loom.listener;


import com.alibaba.fastjson.JSONObject;
import com.bazinga.bull.cache.CacheManager;
import com.bazinga.bull.cache.CommonCacheManager;
import com.bazinga.bull.cache.DragonHeadCacheManager;
import com.bazinga.bull.cache.InsertCacheManager;
import com.bazinga.bull.component.*;
import com.bazinga.bull.constant.CommonConstant;
import com.bazinga.bull.constant.DateConstant;
import com.bazinga.bull.dto.*;
import com.bazinga.bull.dto.CommonQuoteDTO;
import com.bazinga.bull.enums.*;
import com.bazinga.bull.event.InsertOrderEvent;
import com.bazinga.bull.model.*;
import com.bazinga.bull.service.DisableInsertStockPoolService;
import com.bazinga.bull.service.OrderCancelPoolService;
import com.bazinga.bull.util.*;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InsertOrderListener implements ApplicationListener<InsertOrderEvent> {

    private ExecutorService SINGLE_THREAD_POOL = ThreadPoolUtils.createSingle(4);
    public static Map<String, Integer> INSERT_ORDER_TIMES_MAP = new HashMap<>(256);


    @Autowired
    private TradeApiComponent tradeApiComponent;

    @Autowired
    private OrderCancelPoolService orderCancelPoolService;

    @Override
    public void onApplicationEvent(InsertOrderEvent insertOrderEvent) {
        String stockCode = insertOrderEvent.getStockCode();
        Long orderTimeStamp;
        if(new Date().after(DateConstant.PM_14_56_00)){
            log.info("接近下午收盘不下单 stockCode={}", insertOrderEvent.getStockCode());
            return;
        }
        String openTimeStr = InsertCacheManager.OPEN_PLANK_TIME_MAP.get(stockCode);
        if(!StringUtils.isEmpty(openTimeStr)){
            log.info("stockCode{} 上次开板时间{} 本次下单行情时间{}",stockCode,openTimeStr,DateTimeUtils.trans2CommonFormat(insertOrderEvent.getQuoteTime()));
            Date openDate = DateUtil.parseDate(DateConstant.TODAY_STRING + openTimeStr, DateUtil.yyyyMMddHHmmssSSS);
            Date currentQuoteDate = DateUtil.parseDate(DateConstant.TODAY_STRING + DateTimeUtils.trans2CommonFormat(insertOrderEvent.getQuoteTime()), DateUtil.yyyyMMddHHmmssSSS);
            if(openDate != null && currentQuoteDate != null &&currentQuoteDate.getTime() - openDate.getTime() < TimeUnit.MILLISECONDS.toMillis(2500) ){
                log.info("未通过开板回封下单条件 stockCode{}",stockCode);
                return;
            }
        }
        synchronized (insertOrderEvent.getStockCode()) {
            orderTimeStamp = InsertCacheManager.DUPLICATION_ORDER_MAP.get(insertOrderEvent.getStockCode());
            InsertCacheManager.DUPLICATION_ORDER_MAP.put(insertOrderEvent.getStockCode(), System.currentTimeMillis());
        }
        addStock2System(stockCode);
        if (orderTimeStamp != null && System.currentTimeMillis() - orderTimeStamp < TimeUnit.SECONDS.toMillis(6)) {
            log.info("触碰重发下单 stockCode = {}", insertOrderEvent.getStockCode());
            return;
        }
        List<OrderCancelPool> orderCancelPoolList = buildOrder(insertOrderEvent);
        if(CollectionUtils.isEmpty(orderCancelPoolList)){
            log.info("未构建出订单 stockCode{}", stockCode);
            return;
        }
        if(isShotHeadSign(insertOrderEvent.getStockCode(),insertOrderEvent.getOrderPrice())){
            log.info("满足涨速爆头信号不下单stockCode{}",stockCode);
            return;
        }
        addToDisableInsertPool(insertOrderEvent.getStockCode());

        String shareHolderId = stockCode.startsWith("6")?InsertCacheManager.TRADE_ACCOUNT.getShGdCode():InsertCacheManager.TRADE_ACCOUNT.getSzGdCode();
        if(InsertCacheManager.DELAY_300_MILLION>0 && stockCode.startsWith("3")){
            log.info("创业板下单延迟stockCode{} delay{}",stockCode,InsertCacheManager.DELAY_300_MILLION);
            try {
                TimeUnit.MILLISECONDS.sleep(InsertCacheManager.DELAY_300_MILLION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        synToCacheAndDb(stockCode,orderCancelPoolList);
        for (int i = 0; i < orderCancelPoolList.size(); i++) {
            OrderCancelPool orderCancelPool = orderCancelPoolList.get(i);
            OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
            orderRequestDTO.setExchangeId(ExchangeIdUtil.getExchangeId(stockCode));
            orderRequestDTO.setOrderPrice(insertOrderEvent.getOrderPrice());
            orderRequestDTO.setStockCode(stockCode);
            orderRequestDTO.setShareholderId(shareHolderId);
            orderRequestDTO.setLocalSign(orderCancelPool.getLocalSign());
            orderRequestDTO.setVolume(orderCancelPool.getOrderQuantity());
            try {
                Long orderNo = tradeApiComponent.insertOrder(orderRequestDTO);
                orderCancelPool.setOrderNo(orderNo.toString());
            } catch (Exception e) {
                log.error(e.getMessage()+"stockCode{} 下单异常",stockCode,e);
            }
        }
        SINGLE_THREAD_POOL.execute(()->{
            try {
                for (OrderCancelPool orderCancelPool : orderCancelPoolList) {
                    orderCancelPoolService.save(orderCancelPool);
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        });


    }

    private boolean isShotHeadSign(String stockCode ,BigDecimal upperPrice) {
        if(stockCode.startsWith("3")){
            return false;
        }
        Boolean buttonFlag = CacheManager.BUTTON_CONFIG_MAP.get(ButtonConfigEnum.SHOT_HEAD_BUTTON.getCode());
        if(!buttonFlag){
            return false;
        }
        CommonQuoteDTO lastQuoteDTO = CacheManager.STOCK_LAST_PRICE_MAP.get(stockCode);
        if(lastQuoteDTO!=null){
            String stockCodeDay = CommonUtil.getStockCodeDay(stockCode);
            StockSealPlank stockSealPlank = CacheManager.SEAL_PLANK_MAP.get(stockCodeDay);
            if(stockSealPlank != null ){
                log.info("该票已上过板 无需判断爆头涨速");
                return false;
            }
            BigDecimal rate = PriceUtil.getPricePercentRate(upperPrice.subtract(lastQuoteDTO.getCurrentPrice()),lastQuoteDTO.getYesterdayPrice());
            if(rate.compareTo(new BigDecimal("5"))>=0){
                log.info("满足L2行情爆头信号stockCode{}, rate{}",stockCode,rate);
                disableInsertStockPoolComponent.addShotHeadDisable(stockCode);
                return true;
            }
        }
        return false;
    }

    private void synToCacheAndDb(String stockCode, List<OrderCancelPool> orderCancelPoolList) {
        for (OrderCancelPool orderCancelPool : orderCancelPoolList) {
            orderCancelPool.setLocalSign(InsertCacheManager.getOrderRef());
        }
        InsertCacheManager.ORDER_CANCEL_POOL_MAP.put(stockCode,orderCancelPoolList);
        InsertCacheManager.ORDER_CANCEL_POOL_TO_LOG_MAP.put(stockCode,orderCancelPoolList);
        insertCacheManager.putOrderCancelPoolTimesMap(orderCancelPoolList.get(0));


    }


    private List<OrderCancelPool> buildOrder(InsertOrderEvent insertOrderEvent) {
        List<OrderCancelPool> orderCancelPoolList = null;
        try {
            int quantity;
            BigDecimal position;
            orderCancelPoolList = Lists.newArrayList();
            if(!isStatusOrder(insertOrderEvent.getStockCode())){
                log.info("按钮拦截下单 stockCode{} 账号信息{}", insertOrderEvent.getStockCode(), JSONObject.toJSONString(InsertCacheManager.TRADE_ACCOUNT));
                return null;
            }
            INSERT_ORDER_TIMES_MAP.merge(insertOrderEvent.getStockCode(), 1, Integer::sum);
            StockParamConfigDTO configDTO = CacheManager.STOCK_PARAM_CONFIG_MAP.get(insertOrderEvent.getStockCode());
            log.info("stockCode{} 涨停时流通市值z{}",configDTO.getStockCode(),configDTO.getUpperCirculateAmount());
          //  boolean iceFlag = MiddlePlankComponent.IS_ICE_DAY || InsertCacheManager.YESTERDAY_PLANK_CLOSE_RATE.compareTo(BigDecimal.ZERO)<0;
           // log.info("stockCode{} iceFlag{}",insertOrderEvent.getStockCode(),iceFlag);
            if(insertOrderEvent.getStockCode().startsWith("3")){
                log.info("使用300仓位{}",InsertCacheManager.TRADE_ACCOUNT.getPosition300());
                if(configDTO.getUpperCirculateAmount().longValue()> 10 * CommonConstant.ONE_BILLION){
                    position =  InsertCacheManager.TRADE_ACCOUNT.getPosition300().multiply(InsertCacheManager.TRADE_ACCOUNT.getPositionBigAmount());
                }else {
                    position = InsertCacheManager.TRADE_ACCOUNT.getPosition300();
                }
               /* BigDecimal blockPositionRatio = MainBlockPosition(insertOrderEvent.getStockCode());
                if(blockPositionRatio.compareTo(CommonConstant.EXACTLY_ONE)!=0){
                    log.info("板块仓位因子生效 stockCode{} blockPositionRatio{}",insertOrderEvent.getStockCode(),blockPositionRatio);
                    position = position .multiply(blockPositionRatio).setScale(0,BigDecimal.ROUND_HALF_UP);
                }else {*/
                if(CacheManager.FIRST_PLANK_LIST.contains(insertOrderEvent.getStockCode())){
                    position = position .multiply(InsertCacheManager.TRADE_ACCOUNT.getTwoPlankRatio());
                }else if(CacheManager.HIGH_PLANK_LIST.contains(insertOrderEvent.getStockCode())){
                    position = position .multiply(InsertCacheManager.TRADE_ACCOUNT.getHighPlankRatio());
                }
               /* }*/

            }else if(insertOrderEvent.getStockCode().startsWith("688")){
                position = InsertCacheManager.TRADE_ACCOUNT.getPosition688();
            }else {
                log.info("使用主板仓位{}",InsertCacheManager.TRADE_ACCOUNT.getPosition());
                if(configDTO.getUpperCirculateAmount().longValue()> 10 * CommonConstant.ONE_BILLION){
                    log.info("命中大于10亿流通市值使用二挡仓位{}",configDTO.getStockCode());
                    position = InsertCacheManager.TRADE_ACCOUNT.getPosition().multiply(InsertCacheManager.TRADE_ACCOUNT.getPositionBigAmount());
                }else {
                    position = InsertCacheManager.TRADE_ACCOUNT.getPosition();
                }
                BigDecimal blockPositionRatio = MainBlockPosition(insertOrderEvent.getStockCode());
                if(blockPositionRatio.compareTo(CommonConstant.EXACTLY_ONE)!=0){
                    log.info("板块仓位因子生效 stockCode{} blockPositionRatio{}",insertOrderEvent.getStockCode(),blockPositionRatio);
                    position = position .multiply(blockPositionRatio).setScale(0,BigDecimal.ROUND_HALF_UP);
                }else {
                    if(CacheManager.FIRST_PLANK_LIST.contains(insertOrderEvent.getStockCode())){
                        position = position .multiply(InsertCacheManager.TRADE_ACCOUNT.getTwoPlankRatio());
                        if(CacheManager.MAX100_STRONG_PLANK.contains(insertOrderEvent.getStockCode())){
                            log.info("命中大赚2板 stockCode{}",insertOrderEvent.getStockCode());
                            position = position.multiply(new BigDecimal("1.3")).setScale(0,BigDecimal.ROUND_HALF_UP);
                        }
                        if(  configDTO.getUpperCirculateAmount().intValue() < 45 * CommonConstant.ONE_BILLION && new Date().before(DateConstant.AM_09_35_00)){
                            if(CacheManager.MAX100_WEAK_PLANK.contains(insertOrderEvent.getStockCode())){
                                log.info("命中大亏2板 stockCode{}",insertOrderEvent.getStockCode());
                                position = position.multiply(new BigDecimal("0.3")).setScale(0,BigDecimal.ROUND_HALF_UP);
                            }
                            if(CacheManager.MIN15_WEAK_PLANK.contains(insertOrderEvent.getStockCode())){
                                log.info("命中MIN15_WEAK_PLANK大亏2板 stockCode{}",insertOrderEvent.getStockCode());
                                position = position.multiply(new BigDecimal("0.3")).setScale(0,BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }else if(CacheManager.HIGH_PLANK_LIST.contains(insertOrderEvent.getStockCode())){
                        position = position .multiply(InsertCacheManager.TRADE_ACCOUNT.getHighPlankRatio());
                    }else {
                        Date currentDate = new Date();
                        if( configDTO.getUpperCirculateAmount().intValue() < 45 * CommonConstant.ONE_BILLION){
                            if(currentDate.before(DateConstant.AM_10_00_00) && CacheManager.FIRST_WEAK_PLANK_END_RAISE_RATE255.contains(insertOrderEvent.getStockCode())){
                                log.info("命中首板降仓FIRST_WEAK_PLANK_END_RAISE_RATE255 stockCode{}",insertOrderEvent.getStockCode());
                                position = position.multiply(new BigDecimal("0.1")).setScale(0,BigDecimal.ROUND_HALF_UP);
                            }
                            if(currentDate.before(DateConstant.AM_09_33_00) && CacheManager.FIRST_WEAK_PLANK_TEN_DAY_AVG_EXCHANGE.contains(insertOrderEvent.getStockCode())){
                                log.info("命中首板降仓FIRST_WEAK_PLANK_TEN_DAY_AVG_EXCHANGE stockCode{}",insertOrderEvent.getStockCode());
                                position = position.multiply(new BigDecimal("0.1")).setScale(0,BigDecimal.ROUND_HALF_UP);
                            }
                            if(currentDate.before(DateConstant.AM_10_30_00) && CacheManager.FIRST_WEAK_PLANK_HIGH_RATE_DAY5.contains(insertOrderEvent.getStockCode())){
                                log.info("命中首板降仓FIRST_WEAK_PLANK_HIGH_RATE_DAY5 stockCode{}",insertOrderEvent.getStockCode());
                                position = position.multiply(new BigDecimal("0.1")).setScale(0,BigDecimal.ROUND_HALF_UP);
                            }
                            if(CacheManager.FIRST_WEAK_PLANK_PLANKS_DAY10.contains(insertOrderEvent.getStockCode())){
                                log.info("命中首板降仓FIRST_WEAK_PLANK_PLANKS_DAY10 stockCode{}",insertOrderEvent.getStockCode());
                                position = position.multiply(new BigDecimal("0.1")).setScale(0,BigDecimal.ROUND_HALF_UP);
                            }
                        }
                    }
                }

            }
            Boolean commonFlag = CacheManager.BUTTON_CONFIG_MAP.get(ButtonConfigEnum.COMMON_BUTTON.getCode());
            if(!insertOrderEvent.getStockCode().startsWith("3") && commonFlag!=null && commonFlag ){
                Date currentDate = new Date();
                if(configDTO.getUpperCirculateAmount().longValue()< 10 * CommonConstant.ONE_BILLION ){
                    if(currentDate.before(DateConstant.AM_09_34_00) || currentDate.after(DateConstant.AM_11_20_00)){
                        log.info("命中庄股小盘降仓stockCode{}",insertOrderEvent.getStockCode());
                        position = position.multiply(new BigDecimal("0.5")).setScale(0,BigDecimal.ROUND_HALF_UP);
                    }
                }
                if(BankerComponent.BANKER_LIST.contains(insertOrderEvent.getStockCode())){
                    log.info("命中turf庄股降仓stockCode{}", insertOrderEvent.getStockCode());
                    position = position.multiply(new BigDecimal("0.15")).setScale(0,BigDecimal.ROUND_HALF_UP);
                }
            }

            Boolean periodTwoFlag = CacheManager.BUTTON_CONFIG_MAP.get(ButtonConfigEnum.PE_PERIOD_TWO_BUTTON.getCode());
            if(!insertOrderEvent.getStockCode().startsWith("3") && periodTwoFlag != null && periodTwoFlag){
                long upperAmount = configDTO.getUpperCirculateAmount().longValue();
                if(upperAmount > 15 * CommonConstant.ONE_BILLION){
                    position = position.multiply(new BigDecimal("1")).setScale(0,BigDecimal.ROUND_HALF_UP);
                }else {
                    position = position.multiply(new BigDecimal("0.50")).setScale(0,BigDecimal.ROUND_HALF_UP);
                }
                log.info("二期日志stockCode{} 仓位{} 市值{}",insertOrderEvent.getStockCode(),position, upperAmount);
            }


            if(DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().contains(insertOrderEvent.getStockCode())){
                RadicalDragonPool radicalDragonPool = DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.get(insertOrderEvent.getStockCode());
                position = InsertCacheManager.TRADE_ACCOUNT.getPosition().multiply(new BigDecimal(radicalDragonPool.getPositionRatio().toString()));
                log.info("命中小池子 使用小池子仓位系数 stockCode{}, 仓位系数{}", insertOrderEvent.getStockCode(), radicalDragonPool.getPositionRatio());
            }

            if(CacheManager.BUTTON_CONFIG_MAP.get(ButtonConfigEnum.DISABLE_POOL_AUTO.getCode())){
                if(CommonCacheManager.RISK_CONTROL_POSITION_BUTTON  && position.compareTo(CommonConstant.DECIMAL_298W)>0){
                    log.info("监管日志stockCode{}流通市值{} 原仓位{}", insertOrderEvent.getStockCode(),configDTO.getUpperCirculateAmount(),position);
                    position = CommonConstant.DECIMAL_298W;
                    log.info("监管日志stockCode{}流通市值{} 保护仓位{}", insertOrderEvent.getStockCode(),configDTO.getUpperCirculateAmount(),position);
                }
            }

            OrderCancelPool orderCancelPool = new OrderCancelPool();
            orderCancelPool.setStockName("");
            orderCancelPool.setStockCode(insertOrderEvent.getStockCode());
            orderCancelPool.setOrderPrice(insertOrderEvent.getOrderPrice());
            orderCancelPool.setStatus(OrderCancelPoolStatusEnum.INIT.getCode());
            orderCancelPool.setInsertModeType(insertOrderEvent.getInsertModeType());
            orderCancelPool.setInsertOrderType(insertOrderEvent.getInsertOrderType());
            orderCancelPool.setOrderTimeMillis(System.currentTimeMillis());
            orderCancelPool.setEntrustStatus(1);
            orderCancelPool.setOrderStamp(DateTimeUtils.trans2CommonFormat(insertOrderEvent.getQuoteTime(), InsertOrderTypeEnum.L1_INSERT.getCode().equals(insertOrderEvent.getInsertOrderType())));
            orderCancelPool.setRealCannonQuantity(insertOrderEvent.getCannonQuantity());
            quantity = position.divide(CommonConstant.DECIMAL_HUNDRED,0,BigDecimal.ROUND_DOWN).divide(insertOrderEvent.getOrderPrice(),0, BigDecimal.ROUND_DOWN).intValue() * 100;

            if (quantity >= CommonConstant.HUNDRED) {
                //超过万手处理
                //单笔最大手数
                Integer maxOrderNo = InsertCacheManager.TRADE_ACCOUNT.getMaxOrderNo();
                int breakingCount = 6;
                if(insertOrderEvent.getStockCode().startsWith("3")){
                    maxOrderNo = InsertCacheManager.TRADE_ACCOUNT.getMaxOrderNo() - 700000;
                    breakingCount = 10;
                }
                int orderTimes = quantity / maxOrderNo;
                for (int i = 1; i <= orderTimes +1 && i <= breakingCount; i++) {
                    if (i == orderTimes + 1) {
                        orderCancelPool.setOrderQuantity(quantity - (orderTimes * maxOrderNo));
                        orderCancelPoolList.add(orderCancelPool);
                    } else {
                        OrderCancelPool copyOrder = new OrderCancelPool();
                        BeanUtils.copyProperties(orderCancelPool, copyOrder);
                        copyOrder.setOrderQuantity(maxOrderNo);
                        orderCancelPoolList.add(copyOrder);
                    }
                }
            } else {
                log.info("当前股票价格过高 stockCode ={}", insertOrderEvent.getStockCode());
                orderCancelPool.setOrderQuantity(100);
                orderCancelPoolList.add(orderCancelPool);
            }
        } catch (Exception e) {
            log.error("下单逻辑异常{}" + e.getMessage(),insertOrderEvent.getStockCode(),e);
        }
        return orderCancelPoolList;

    }

    private BigDecimal MainBlockPosition(String stockCode) {
        Boolean buttonFlag = CacheManager.BUTTON_CONFIG_MAP.get(ButtonConfigEnum.BLOCK_COMPETE_BUTTON.getCode());
        if(!buttonFlag){
            log.info("排块排名仓位控制开关关闭 使用正常仓位");
            return CommonConstant.EXACTLY_ONE;
        }
        StockBlockInfoDTO stockBlockInfoDTO = InsertCacheManager.STOCK_BLOCK_INFO_MAP.get(stockCode);
        if(stockBlockInfoDTO == null ){
            log.info("没有从缓存中获取到板块信息 stockCode{}",stockCode);
            return CommonConstant.EXACTLY_ONE;
        }
        log.info("板块缓存信息 stockCode{} 板块代码{} 板块名称{} 板块涨幅{} 板块排名{}",stockCode,stockBlockInfoDTO.getBlockCode()
                ,stockBlockInfoDTO.getBlockName(),stockBlockInfoDTO.getRate(),stockBlockInfoDTO.getCompeteNum());
        Date date = new Date();
        if(stockBlockInfoDTO.getRate().compareTo(new BigDecimal("4"))> 0 && date.before(DateConstant.AM_09_35_00)){
            log.info("板块涨幅大于4且在前5min涨停 主流板块降仓 stockCode{}",stockCode);
            return new BigDecimal("0.03");
        }
        Date currentDate = new Date();
        if(currentDate.after(DateConstant.AM_09_33_00) && currentDate.before(DateConstant.PM_14_00_00)){
            if(stockCode.startsWith("3")){
                Integer bullNum = bullLevelComponent.getStockBlockLevel(stockCode, stockBlockInfoDTO.getBlockCode());
                if(!CacheManager.FIRST_PLANK_LIST.contains(stockCode)){
                    BigDecimal blockOpenRate = BlockRateComponent.BLOCK_OPEN_RATE_MAP.get(stockBlockInfoDTO.getBlockCode());
                    BigDecimal blockRate = stockBlockInfoDTO.getRate();
                    Integer competeNum = stockBlockInfoDTO.getCompeteNum();
                    if(bullNum ==1 || bullNum==2){
                        if(blockOpenRate!=null && blockRate.subtract(blockOpenRate).compareTo(new BigDecimal("3"))<0 &&
                                blockRate.compareTo(new BigDecimal("2")) > 0 && competeNum >0 && competeNum <=7 ){
                            if(currentDate.after(DateConstant.AM_10_15_00)){
                                log.info("命中主流板块减仓 stockCode{} competeNum{} blockRate{}",stockCode,competeNum,stockBlockInfoDTO.getRate());
                                return new BigDecimal("0.8");
                            }
                            log.info("命中主流板块加仓 stockCode{} competeNum{} blockRate{}",stockCode,competeNum,stockBlockInfoDTO.getRate());
                            return new BigDecimal("1.1");
                        }
                    }
                }

            }else {
                Integer competeNum = stockBlockInfoDTO.getCompeteNum();
                if(competeNum>=1  && competeNum <=3 && stockBlockInfoDTO.getRate().compareTo(new BigDecimal("2.5"))>0){
                    if(currentDate.after(DateConstant.AM_09_50_00)){
                        log.info("命中主流板块减仓 stockCode{} competeNum{} blockRate{}",stockCode,competeNum,stockBlockInfoDTO.getRate());
                        return new BigDecimal("0.8");
                    }
                    log.info("命中主流板块加仓 stockCode{} competeNum{} blockRate{}",stockCode,competeNum,stockBlockInfoDTO.getRate());
                    return new BigDecimal("1.1");
                }
            }
        }
        log.info("没有命中任何条件 正常仓位 stockCode{}",stockCode);
        return CommonConstant.EXACTLY_ONE;
    }

    public boolean isStatusOrder(String stockCode){
        Integer insertOrderTimes = INSERT_ORDER_TIMES_MAP.get(stockCode);
        if(DragonRadicalStatusEnum.ENABLE.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getDragonRadicalStatus())
                && DragonHeadCacheManager.PLANK_DRAGON_STOCK_MAP.keySet().contains(stockCode)){
            log.info("超龙头下单按钮命中且开关打开 可以下单 stockCode ={}",stockCode);
            return true;
        }
        if(insertOrderTimes!=null && insertOrderTimes >= InsertCacheManager.TRADE_ACCOUNT.getInsertTimes()){
            log.info("超过后台设置下单次数不下单当前次数{} 后台次数{} stockCode{}",insertOrderTimes,InsertCacheManager.TRADE_ACCOUNT.getInsertTimes(),stockCode);
            return false;
        }else {
            if(stockCode.startsWith("3")){
                if(CacheManager.FIRST_PLANK_LIST.contains(stockCode)){
                    return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getTwoPlankStatus());
                }else if(CacheManager.HIGH_PLANK_LIST.contains(stockCode)){
                    return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getHighPlankStatus());
                }else {
                    return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getAccountStatus300());
                }
            }else if(stockCode.startsWith("688")){
                return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getAccountStatus688());
            }else {
                if(CacheManager.FIRST_PLANK_LIST.contains(stockCode)){
                    return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getTwoPlankStatus());
                }else if(CacheManager.HIGH_PLANK_LIST.contains(stockCode)){
                    return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getHighPlankStatus());
                }else {
                    return AccountStatusEnum.NORMAL.getCode().equals(InsertCacheManager.TRADE_ACCOUNT.getAccountStatus());
                }
            }
        }

    }

    private void addToDisableInsertPool(String stockCode) {
        DisableInsertStockDTO disableInsertStockDTO = new DisableInsertStockDTO();
        disableInsertStockDTO.setStockCode(stockCode);
        disableInsertStockDTO.setOperateStatus(OperateStatusEnum.INSERT_ORDER.getCode());
        log.info("设置 stockCode ={} 为已下单状态", stockCode);
        CacheManager.DISABLE_INSERT_STOCK_POOL.put(stockCode, disableInsertStockDTO);
        DisableInsertStockPool selectByStockCode = disableInsertStockPoolService.selectByStockCode(stockCode);
        if (selectByStockCode == null) {
            DisableInsertStockPool disableInsertStock = new DisableInsertStockPool();
            disableInsertStock.setStockCode(stockCode);
            disableInsertStock.setOperateStatus(OperateStatusEnum.INSERT_ORDER.getCode());
            SINGLE_THREAD_POOL.execute(() -> disableInsertStockPoolService.save(disableInsertStock));
        } else {
            if (OperateStatusEnum.MANUAL.getCode().intValue() != selectByStockCode.getOperateStatus()) {
                selectByStockCode.setOperateStatus(OperateStatusEnum.INSERT_ORDER.getCode());
                SINGLE_THREAD_POOL.execute(() -> disableInsertStockPoolService.updateById(selectByStockCode));
            }
        }

    }


    private void addStock2System(String stockCode) {
        if(!CacheManager.DISABLE_INSERT_STOCK_POOL.keySet().contains(stockCode)){
            log.info("设置为系统禁止状态 stockCode{}", stockCode);
            DisableInsertStockDTO disableInsertStockDTO = new DisableInsertStockDTO();
            disableInsertStockDTO.setStockCode(stockCode);
            disableInsertStockDTO.setOperateStatus(OperateStatusEnum.SYSTEM.getCode());
            CacheManager.DISABLE_INSERT_STOCK_POOL.put(stockCode, disableInsertStockDTO);
        }
    }
}
