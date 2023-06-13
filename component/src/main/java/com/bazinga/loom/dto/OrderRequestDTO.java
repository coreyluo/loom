package com.bazinga.loom.dto;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequestDTO {


    private char exchangeId;

    private String stockCode;

    private String shareholderId;

    private int volume;

    private String localSign;

    private BigDecimal orderPrice;
}
