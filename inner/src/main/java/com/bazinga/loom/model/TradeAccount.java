package com.bazinga.loom.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈TradeAccount〉<p>
 *
 * @author
 * @date 2023-06-04
 */
@lombok.Data
@lombok.ToString
public class TradeAccount implements Serializable {

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
     * 券商账号
     *
     * @最大长度   30
     * @允许为空   NO
     * @是否索引   NO
     */
    private String userId;

    /**
     * 券商交易账号
     *
     * @最大长度   30
     * @允许为空   NO
     * @是否索引   NO
     */
    private String investorId;

    /**
     * 交易密码
     *
     * @最大长度   80
     * @允许为空   NO
     * @是否索引   NO
     */
    private String tradePassword;

    /**
     * 服务器ip
     *
     * @最大长度   512
     * @允许为空   YES
     * @是否索引   NO
     */
    private String serverIp;

    /**
     * 序列号 
     *
     * @最大长度   512
     * @允许为空   NO
     * @是否索引   NO
     */
    private String hdSerial;

    /**
     * orderref 
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Long orderRef;

    /**
     * 主板仓位 
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private BigDecimal position;

    /**
     * 深圳股东代码
     *
     * @最大长度   60
     * @允许为空   NO
     * @是否索引   NO
     */
    private String szGdCode;

    /**
     * 上海股东代码
     *
     * @最大长度   60
     * @允许为空   NO
     * @是否索引   NO
     */
    private String shGdCode;

    /**
     * 账号禁用标识 1 冻结禁用 0 允许
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer accountStatus;

    /**
     * 下单次数
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer insertTimes;

    /**
     * 下单最大手数
     *
     * @允许为空   NO
     * @是否索引   NO
     */
    private Integer maxOrderNo;

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