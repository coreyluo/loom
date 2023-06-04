package com.bazinga.loom.service;

import com.bazinga.loom.model.TradeDatePool;
import com.bazinga.loom.query.TradeDatePoolQuery;

import java.util.List;

/**
 * 〈TradeDatePool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
public interface TradeDatePoolService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    TradeDatePool save(TradeDatePool record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    TradeDatePool getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(TradeDatePool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<TradeDatePool> listByCondition(TradeDatePoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(TradeDatePoolQuery query);
}