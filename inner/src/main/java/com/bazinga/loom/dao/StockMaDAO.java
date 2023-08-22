package com.bazinga.loom.dao;

import com.bazinga.loom.model.StockMa;
import com.bazinga.loom.query.StockMaQuery;

import java.util.List;

/**
 * 〈StockMa DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-08-22
 */
public interface StockMaDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(StockMa record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    StockMa selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(StockMa record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<StockMa> selectByCondition(StockMaQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(StockMaQuery query);

    /**
     * 唯一键uniqueKey 查询
     *
     * @param uniqueKey 查询参数
     */
    StockMa selectByUniqueKey(String uniqueKey);

    /**
     * 唯一键uniqueKey 更新
     *
     * @param record 更新参数
     */
    int updateByUniqueKey(StockMa record);

}