package com.bazinga.loom.model;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈StockOpenSnapshot〉<p>
 *
 * @author
 * @date 2023-06-27
 */
@lombok.Data
@lombok.ToString
public class StockOpenSnapshot implements Serializable {

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
     * 交易日期
     *
     * @最大长度   10
     * @允许为空   NO
     * @是否索引   NO
     */
    private String kbarDate;

    /**
     * 唯一键
     *
     * @最大长度   20
     * @允许为空   NO
     * @是否索引   YES
     * @唯一索引   uk_unique_key
     */
    private String uniqueKey;

    /**
     * 股票名称
     *
     * @最大长度   60
     * @允许为空   NO
     * @是否索引   NO
     */
    private String stockName;

    /**
     * 买一价格
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private BigDecimal bid1Price;

    /**
     * 买二价格
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private BigDecimal bid2Price;

    /**
     * 
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private BigDecimal bid3Price;

    /**
     * 买一量手数
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long bid1Volume;

    /**
     * 买二量
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long bid2Volume;

    /**
     * 
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long bid3Volume;

    /**
     * 卖一价格
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private BigDecimal ask1Price;

    /**
     * 卖二价格
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private BigDecimal ask2Price;

    /**
     * 
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private BigDecimal ask3Price;

    /**
     * 卖一量
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long ask1Volume;

    /**
     * 卖二量
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long ask2Volume;

    /**
     * 
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long ask3Volume;

    /**
     * 成交量
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Long tradeQuantity;

    /**
     * 创建时间
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Date createTime;

    /**
     * 更新时间
     *
     * @允许为空   YES
     * @是否索引   NO
     */
    private Date updateTime;


}