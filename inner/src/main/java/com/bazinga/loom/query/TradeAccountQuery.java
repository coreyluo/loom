package com.bazinga.loom.query;


import com.bazinga.base.PagingQuery;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Date;

import java.io.Serializable;

/**
 * 〈TradeAccount 查询参数〉<p>
 *
 * @author
 * @date 2023-06-04
 */
@lombok.Data
@lombok.EqualsAndHashCode(callSuper = true)
@lombok.ToString(callSuper = true)
public class TradeAccountQuery extends PagingQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 券商账号
     */
    private String userId;

    /**
     * 券商交易账号
     */
    private String investorId;

    /**
     * 交易密码
     */
    private String tradePassword;

    /**
     * 服务器ip
     */
    private String serverIp;

    /**
     * 序列号 
     */
    private String hdSerial;

    /**
     * orderref 
     */
    private Long orderRef;

    /**
     * 主板仓位 
     */
    private BigDecimal position;

    /**
     * 深圳股东代码
     */
    private String szGdCode;

    /**
     * 上海股东代码
     */
    private String shGdCode;

    /**
     * 账号禁用标识 1 冻结禁用 0 允许
     */
    private Integer accountStatus;

    /**
     * 下单次数
     */
    private Integer insertTimes;

    /**
     * 下单最大手数
     */
    private Integer maxOrderNo;

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