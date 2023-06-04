package com.bazinga.loom.dao;

import com.bazinga.loom.model.TradeAccount;
import com.bazinga.loom.query.TradeAccountQuery;

import java.util.List;

/**
 * 〈TradeAccount DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
public interface TradeAccountDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(TradeAccount record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    TradeAccount selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(TradeAccount record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<TradeAccount> selectByCondition(TradeAccountQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(TradeAccountQuery query);

}