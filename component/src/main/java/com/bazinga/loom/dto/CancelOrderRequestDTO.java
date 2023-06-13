package com.bazinga.loom.dto;


import lombok.Data;

@Data
public class CancelOrderRequestDTO {

    private char exchangeId;

    private String stockCode;
    /**
     * 系统委托订单号
     */
    private String orderSysId;

    /**
     * 本地编号
     */
    private String orderRef;

    private Integer orderQuantity;
}
