package com.bazinga.loom.model;

import java.math.BigDecimal;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈DealOrderPool〉<p>
 *
 * @author
 * @date 2023-06-18
 */
@lombok.Data
@lombok.ToString
public class DealOrderPool implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     *
     * @允许为空   NO
     * @是否索引   YES
     * @唯一索引   PRIMARY
     */
    private Long id;

    /**
     * 股票代码
     *
     * @最大长度   10
     * @允许为空   NO
     * @是否索引   NO
     */
    private String stockCode;

    /**
     * 
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private BigDecimal tradePrice;

    /**
     * 档位对数
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer gearType;

    /**
     * 唯一键
     *
     * @最大长度   20
     * @允许为空   NO
     * @是否索引   YES
     * @唯一索引   uk_stock_code_gear_type_day
     */
    private String stockCodeGearTypeDay;

    /**
     * 股票名称
     *
     * @最大长度   60
     * @允许为空   YES
     * @是否索引   NO
     */
    private String stockName;

    /**
     * 创建时间
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Date createTime;


}