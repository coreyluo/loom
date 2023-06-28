package com.bazinga.loom.dao;

import com.bazinga.loom.model.StockOpenSnapshot;
import com.bazinga.loom.query.StockOpenSnapshotQuery;

import java.util.List;

/**
 * 〈StockOpenSnapshot DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-27
 */
public interface StockOpenSnapshotDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(StockOpenSnapshot record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    StockOpenSnapshot selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(StockOpenSnapshot record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<StockOpenSnapshot> selectByCondition(StockOpenSnapshotQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(StockOpenSnapshotQuery query);

    /**
     * 唯一键uniqueKey 查询
     *
     * @param uniqueKey 查询参数
     */
    StockOpenSnapshot selectByUniqueKey(String uniqueKey);

    /**
     * 唯一键uniqueKey 更新
     *
     * @param record 更新参数
     */
    int updateByUniqueKey(StockOpenSnapshot record);

}