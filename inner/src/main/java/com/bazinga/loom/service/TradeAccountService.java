package com.bazinga.loom.service;

import com.bazinga.loom.model.TradeAccount;
import com.bazinga.loom.query.TradeAccountQuery;

import java.util.List;

/**
 * 〈TradeAccount Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
public interface TradeAccountService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    TradeAccount save(TradeAccount record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    TradeAccount getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(TradeAccount record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<TradeAccount> listByCondition(TradeAccountQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(TradeAccountQuery query);
}