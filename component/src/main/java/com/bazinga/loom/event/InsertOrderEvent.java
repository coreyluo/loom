package com.bazinga.loom.event;

import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

public class InsertOrderEvent extends ApplicationEvent {

    private String stockCode;

    private BigDecimal orderPrice;

    private Long cannonQuantity;

    private String quoteTime;

    private Integer gearLevel;


    public InsertOrderEvent(Object source, String stockCode, BigDecimal orderPrice, Long cannonQuantity, String quoteTime,Integer gearLevel) {
        super(source);
        this.stockCode = stockCode;
        this.orderPrice = orderPrice;
        this.cannonQuantity = cannonQuantity;
        this.quoteTime = quoteTime;
        this.gearLevel = gearLevel;
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

    public Integer getGearLevel() {
        return gearLevel;
    }

    public void setGearLevel(Integer gearLevel) {
        this.gearLevel = gearLevel;
    }
}
