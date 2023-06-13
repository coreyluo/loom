package com.bazinga.loom.query;


import com.bazinga.base.PagingQuery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈OrderCancelPool 查询参数〉<p>
 *
 * @author
 * @date 2023-06-12
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class OrderCancelPoolQuery extends PagingQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 本地标识
     */
    private String localSign;

    /**
     * 委托编号 订单委托_靶委托
     */
    private String orderNo;

    /**
     * 委托价格
     */
    private BigDecimal orderPrice;

    /**
     * 订单股数
     */
    private Integer orderQuantity;

    /**
     * 位置所在时分秒
     */
    private String orderStamp;

    /**
     * 委托时间毫秒数
     */
    private Long orderTimeMillis;

    /**
     * 真实炮灰量 单位 手
     */
    private Long realCannonQuantity;

    /**
     * 买一,1 买二2 卖1 -1 卖2 -2
     */
    private Integer gearType;

    /**
     * 状态 0 初始化, 1 成功 -1 失败
     */
    private Integer status;

    /**
     * 创建时间 开始
     */
    private Date createTimeFrom;

    /**
     * 创建时间 结束
     */
    private Date createTimeTo;

    /**
     * 更新时间 开始
     */
    private Date updateTimeFrom;

    /**
     * 更新时间 结束
     */
    private Date updateTimeTo;


}