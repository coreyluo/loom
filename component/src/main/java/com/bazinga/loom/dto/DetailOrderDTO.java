package com.bazinga.loom.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class DetailOrderDTO {

    private String stockCode;

    private String orderTime;

    private BigDecimal orderPrice;

    private Long orderQuantity;

    private String side;

    private String orderStatus;

    private char orderType;

    private Integer mainSeq;

    private Integer subSeq;
}
