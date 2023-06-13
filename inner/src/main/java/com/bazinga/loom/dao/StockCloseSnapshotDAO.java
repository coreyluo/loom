package com.bazinga.loom.dao;

import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.query.StockCloseSnapshotQuery;

import java.util.List;

/**
 * 〈StockCloseSnapshot DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-13
 */
public interface StockCloseSnapshotDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(StockCloseSnapshot record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    StockCloseSnapshot selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(StockCloseSnapshot record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<StockCloseSnapshot> selectByCondition(StockCloseSnapshotQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(StockCloseSnapshotQuery query);

    /**
     * 唯一键uniqueKey 查询
     *
     * @param uniqueKey 查询参数
     */
    StockCloseSnapshot selectByUniqueKey(String uniqueKey);

    /**
     * 唯一键uniqueKey 更新
     *
     * @param record 更新参数
     */
    int updateByUniqueKey(StockCloseSnapshot record);

}