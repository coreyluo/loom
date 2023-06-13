package com.bazinga.loom.query;


import com.bazinga.base.PagingQuery;

import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈DisableInsertStockPool 查询参数〉<p>
 *
 * @author
 * @date 2023-06-13
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class DisableInsertStockPoolQuery extends PagingQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 禁用标识
     */
    private String stockCodeGear;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 0 系统操作 1 人工操作
     */
    private Integer operateStatus;

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