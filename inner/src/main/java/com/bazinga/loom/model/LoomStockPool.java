package com.bazinga.loom.model;

import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈LoomStockPool〉<p>
 *
 * @author
 * @date 2023-06-04
 */
@lombok.Data
@lombok.ToString
public class LoomStockPool implements Serializable {

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
     * @是否索引   NO
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