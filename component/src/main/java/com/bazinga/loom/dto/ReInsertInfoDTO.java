package com.bazinga.loom.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReInsertInfoDTO {

    private String stockCode;

    private BigDecimal orderPrice;

    private Integer direction;
}
