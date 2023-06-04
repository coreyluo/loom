package com.bazinga.loom.model;

import java.util.Date;

import java.io.Serializable;

/**
 * 〈QuoteIpConfig〉<p>
 *
 * @author
 * @date 2023-06-04
 */
@lombok.Data
@lombok.ToString
public class QuoteIpConfig implements Serializable {

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
     * 服务地址
     *
     * @最大长度   60
     * @允许为空   NO
     * @是否索引   NO
     */
    private String serverIp;

    /**
     * 行情类型
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer quoteType;

    /**
     * 创建时间
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Date createTime;


}