package com.bazinga.loom.service;

import com.bazinga.loom.model.StockMa;
import com.bazinga.loom.query.StockMaQuery;

import java.util.List;

/**
 * 〈StockMa Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-08-22
 */
public interface StockMaService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    StockMa save(StockMa record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    StockMa getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(StockMa record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<StockMa> listByCondition(StockMaQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(StockMaQuery query);

    /**
     * 唯一键uniqueKey 查询
     *
     * @param uniqueKey 查询参数
     */
    StockMa getByUniqueKey(String uniqueKey);

    /**
     * 唯一键uniqueKey 更新
     *
     * @param record 更新参数
     */
    int updateByUniqueKey(StockMa record);
}