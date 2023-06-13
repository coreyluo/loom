package com.bazinga.loom.service;

import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.query.StockCloseSnapshotQuery;

import java.util.List;

/**
 * 〈StockCloseSnapshot Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-13
 */
public interface StockCloseSnapshotService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    StockCloseSnapshot save(StockCloseSnapshot record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    StockCloseSnapshot getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(StockCloseSnapshot record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<StockCloseSnapshot> listByCondition(StockCloseSnapshotQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(StockCloseSnapshotQuery query);

    /**
     * 唯一键uniqueKey 查询
     *
     * @param uniqueKey 查询参数
     */
    StockCloseSnapshot getByUniqueKey(String uniqueKey);

    /**
     * 唯一键uniqueKey 更新
     *
     * @param record 更新参数
     */
    int updateByUniqueKey(StockCloseSnapshot record);
}