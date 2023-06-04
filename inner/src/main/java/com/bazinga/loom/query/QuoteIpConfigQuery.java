package com.bazinga.loom.query;


import com.bazinga.base.PagingQuery;

import java.util.Date;

import java.io.Serializable;

/**
 * 〈QuoteIpConfig 查询参数〉<p>
 *
 * @author
 * @date 2023-06-04
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class QuoteIpConfigQuery extends PagingQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务地址
     */
    private String serverIp;

    /**
     * 行情类型
     */
    private Integer quoteType;

    /**
     * 创建时间 开始
     */
    private Date createTimeFrom;

    /**
     * 创建时间 结束
     */
    private Date createTimeTo;


}