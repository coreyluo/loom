package com.bazinga.loom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CommonQuoteDTO {

    private String stockCode;

    private String stockCodeName;

    private BigDecimal currentPrice;
    private BigDecimal buyOnePrice;
    private BigDecimal buyTwoPrice;
    private BigDecimal buyThreePrice;

    private BigDecimal sellOnePrice;
    private BigDecimal sellTwoPrice;
    private BigDecimal sellThreePrice;

    private Long buyOneQuantity;
    private Long buyTwoQuantity;
    private Long sellOneQuantity;
    private Long sellTwoQuantity;

    private String quoteTime;

    private BigDecimal upperLimitPrice;

    private BigDecimal yesterdayPrice;

    private Long totalSellQuantity;

    private Long totalTradeQuantity;

    private BigDecimal totalTradeMoney;


    private BigDecimal openPrice;

    private BigDecimal highestPrice;
    private BigDecimal lowestPrice;
}
