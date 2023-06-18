package com.bazinga.loom.dao;

import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.query.DealOrderPoolQuery;

import java.util.List;

/**
 * 〈DealOrderPool DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-18
 */
public interface DealOrderPoolDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(DealOrderPool record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    DealOrderPool selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(DealOrderPool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<DealOrderPool> selectByCondition(DealOrderPoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(DealOrderPoolQuery query);

    /**
     * 唯一键stockCodeGearTypeDay 查询
     *
     * @param stockCodeGearTypeDay 查询参数
     */
    DealOrderPool selectByStockCodeGearTypeDay(String stockCodeGearTypeDay);

    /**
     * 唯一键stockCodeGearTypeDay 更新
     *
     * @param record 更新参数
     */
    int updateByStockCodeGearTypeDay(DealOrderPool record);

}