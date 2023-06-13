package com.bazinga.loom.service;

import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.query.DisableInsertStockPoolQuery;

import java.util.List;

/**
 * 〈DisableInsertStockPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-13
 */
public interface DisableInsertStockPoolService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    DisableInsertStockPool save(DisableInsertStockPool record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    DisableInsertStockPool getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(DisableInsertStockPool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<DisableInsertStockPool> listByCondition(DisableInsertStockPoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(DisableInsertStockPoolQuery query);

    /**
     * 唯一键stockCodeGear 查询
     *
     * @param stockCodeGear 查询参数
     */
    DisableInsertStockPool getByStockCodeGear(String stockCodeGear);

    /**
     * 唯一键stockCodeGear 更新
     *
     * @param record 更新参数
     */
    int updateByStockCodeGear(DisableInsertStockPool record);
}