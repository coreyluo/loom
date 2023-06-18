package com.bazinga.loom.query;


import com.bazinga.base.PagingQuery;

import java.util.Date;

import java.io.Serializable;

/**
 * 〈DealOrderPool 查询参数〉<p>
 *
 * @author
 * @date 2023-06-18
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class DealOrderPoolQuery extends PagingQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 档位对数
     */
    private Integer gearType;

    /**
     * 唯一键
     */
    private String stockCodeGearTypeDay;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 创建时间 开始
     */
    private Date createTimeFrom;

    /**
     * 创建时间 结束
     */
    private Date createTimeTo;


}