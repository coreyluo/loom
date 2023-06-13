package com.bazinga.loom.dao;

import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.query.OrderCancelPoolQuery;

import java.util.List;

/**
 * 〈OrderCancelPool DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-12
 */
public interface OrderCancelPoolDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(OrderCancelPool record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    OrderCancelPool selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(OrderCancelPool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<OrderCancelPool> selectByCondition(OrderCancelPoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(OrderCancelPoolQuery query);

}