package com.bazinga.loom.query;


import com.bazinga.base.PagingQuery;

import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈StockCloseSnapshot 查询参数〉<p>
 *
 * @author
 * @date 2023-06-11
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class StockCloseSnapshotQuery extends PagingQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 交易日期
     */
    private String kbarDate;

    /**
     * 唯一键
     */
    private String uniqueKey;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 买一价格
     */
    private BigDecimal bid1Price;

    /**
     * 买二价格
     */
    private BigDecimal bid2Price;

    /**
     * 买一量手数
     */
    private Long bid1Volume;

    /**
     * 买二量
     */
    private Long bid2Volume;

    /**
     * 卖一价格
     */
    private BigDecimal ask1Price;

    /**
     * 卖二价格
     */
    private BigDecimal ask2Price;

    /**
     * 卖一量
     */
    private Long ask1Volume;

    /**
     * 卖二量
     */
    private Long ask2Volume;

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