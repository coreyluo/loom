package com.bazinga.loom.dao;

import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.query.DisableInsertStockPoolQuery;

import java.util.List;

/**
 * 〈DisableInsertStockPool DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-13
 */
public interface DisableInsertStockPoolDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(DisableInsertStockPool record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    DisableInsertStockPool selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(DisableInsertStockPool record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<DisableInsertStockPool> selectByCondition(DisableInsertStockPoolQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(DisableInsertStockPoolQuery query);

    /**
     * 唯一键stockCodeGear 查询
     *
     * @param stockCodeGear 查询参数
     */
    DisableInsertStockPool selectByStockCodeGear(String stockCodeGear);

    /**
     * 唯一键stockCodeGear 更新
     *
     * @param record 更新参数
     */
    int updateByStockCodeGear(DisableInsertStockPool record);

}