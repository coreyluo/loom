package com.bazinga.loom.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈OrderCancelPool〉<p>
 *
 * @author
 * @date 2023-06-12
 */
@lombok.Data
@lombok.ToString
public class OrderCancelPool implements Serializable {

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
     * @最大长度   15
     * @允许为空   NO
     * @是否索引   NO
     */
    private String stockCode;

    /**
     * 股票名称
     *
     * @最大长度   20
     * @允许为空   NO
     * @是否索引   NO
     */
    private String stockName;

    /**
     * 本地标识
     *
     * @最大长度   15
     * @允许为空   NO
     * @是否索引   NO
     */
    private String localSign;

    /**
     * 委托编号 订单委托_靶委托
     *
     * @最大长度   60
     * @允许为空   YES
     * @是否索引   NO
     */
    private String orderNo;

    /**
     * 委托价格
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private BigDecimal orderPrice;

    /**
     * 订单股数
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer orderQuantity;

    /**
     * 位置所在时分秒
     *
     * @最大长度   20
     * @允许为空   YES
     * @是否索引   NO
     */
    private String orderStamp;

    /**
     * 委托时间毫秒数
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Long orderTimeMillis;

    /**
     * 真实炮灰量 单位 手
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Long realCannonQuantity;

    /**
     * 买一,1 买二2 卖1 -1 卖2 -2
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer gearType;

    /**
     * 状态 0 初始化, 1 成功 -1 失败
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer status;

    /**
     * 创建时间
     *
     * @允许为空   NO
     * @是否索引   YES
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