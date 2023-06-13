package com.bazinga.loom.dto;


import lombok.Data;

@Data
public class ReturnOrderDTO {

    private String stockCode;

    private String localSign;

    private String orderNo;

    private char orderStatus;
}
