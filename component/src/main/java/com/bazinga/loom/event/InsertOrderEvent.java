package com.bazinga.loom.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

public class InsertOrderEvent extends ApplicationEvent {

    private String stockCode;

    private BigDecimal orderPrice;

    private Long cannonQuantity;

    private String quoteTime;

    private Integer insertOrderType;

    private Integer insertModeType;

    public InsertOrderEvent(Object source, String stockCode, BigDecimal orderPrice, Long cannonQuantity, String quoteTime, Integer insertOrderType, Integer insertModeType) {
        super(source);
        this.stockCode = stockCode;
        this.orderPrice = orderPrice;
        this.cannonQuantity = cannonQuantity;
        this.quoteTime = quoteTime;
        this.insertOrderType = insertOrderType;
        this.insertModeType = insertModeType;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Long getCannonQuantity() {
        return cannonQuantity;
    }

    public void setCannonQuantity(Long cannonQuantity) {
        this.cannonQuantity = cannonQuantity;
    }

    public String getQuoteTime() {
        return quoteTime;
    }

    public void setQuoteTime(String quoteTime) {
        this.quoteTime = quoteTime;
    }

    public Integer getInsertOrderType() {
        return insertOrderType;
    }

    public void setInsertOrderType(Integer insertOrderType) {
        this.insertOrderType = insertOrderType;
    }

    public Integer getInsertModeType() {
        return insertModeType;
    }

    public void setInsertModeType(Integer insertModeType) {
        this.insertModeType = insertModeType;
    }
}
