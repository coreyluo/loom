package com.bazinga.loom.model;

import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈DisableInsertStockPool〉<p>
 *
 * @author
 * @date 2023-06-13
 */
@lombok.Data
@lombok.ToString
public class DisableInsertStockPool implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     *
     * @允许为空   NO
     * @是否索引   YES
     * @唯一索引   PRIMARY
     */
    private Long id;

    /**
     * 禁用标识
     *
     * @最大长度   10
     * @允许为空   NO
     * @是否索引   YES
     * @唯一索引   uk_stock_code_gear
     */
    private String stockCodeGear;

    /**
     * 股票名称
     *
     * @最大长度   30
     * @允许为空   YES
     * @是否索引   NO
     */
    private String stockName;

    /**
     * 0 系统操作 1 人工操作
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer operateStatus;

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