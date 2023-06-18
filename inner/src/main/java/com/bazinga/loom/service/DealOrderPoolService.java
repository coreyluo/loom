package com.bazinga.loom.service;

import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.query.DealOrderPoolQuery;

import java.util.List;

/**
 * 〈DealOrderPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-18
 */
public interface DealOrderPoolService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    DealOrderPool save(DealOrderPool record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    DealOrderPool getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(DealOrderPool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<DealOrderPool> listByCondition(DealOrderPoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(DealOrderPoolQuery query);

    /**
     * 唯一键stockCodeGearTypeDay 查询
     *
     * @param stockCodeGearTypeDay 查询参数
     */
    DealOrderPool getByStockCodeGearTypeDay(String stockCodeGearTypeDay);

    /**
     * 唯一键stockCodeGearTypeDay 更新
     *
     * @param record 更新参数
     */
    int updateByStockCodeGearTypeDay(DealOrderPool record);
}