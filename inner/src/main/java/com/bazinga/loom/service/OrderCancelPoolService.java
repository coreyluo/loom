package com.bazinga.loom.service;

import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.query.OrderCancelPoolQuery;

import java.util.List;

/**
 * 〈OrderCancelPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-12
 */
public interface OrderCancelPoolService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    OrderCancelPool save(OrderCancelPool record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    OrderCancelPool getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(OrderCancelPool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<OrderCancelPool> listByCondition(OrderCancelPoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(OrderCancelPoolQuery query);
}