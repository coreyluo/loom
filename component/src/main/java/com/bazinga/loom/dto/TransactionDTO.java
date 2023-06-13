package com.bazinga.loom.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {

    private String stockCode;

    private String tradeTime;

    private BigDecimal tradePrice;

    private char tradeType;

    private Long tradeQuantity;

    private char executeType;

    private Long buyNo;

    private Long sellNo;

    private Integer mainSeq;

}
