package com.bazinga.loom.service;

import com.bazinga.loom.model.StockOpenSnapshot;
import com.bazinga.loom.query.StockOpenSnapshotQuery;

import java.util.List;

/**
 * 〈StockOpenSnapshot Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-27
 */
public interface StockOpenSnapshotService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    StockOpenSnapshot save(StockOpenSnapshot record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    StockOpenSnapshot getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(StockOpenSnapshot record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<StockOpenSnapshot> listByCondition(StockOpenSnapshotQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(StockOpenSnapshotQuery query);

    /**
     * 唯一键uniqueKey 查询
     *
     * @param uniqueKey 查询参数
     */
    StockOpenSnapshot getByUniqueKey(String uniqueKey);

    /**
     * 唯一键uniqueKey 更新
     *
     * @param record 更新参数
     */
    int updateByUniqueKey(StockOpenSnapshot record);
}