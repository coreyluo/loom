package com.bazinga.loom.service;

import com.bazinga.loom.model.QuoteIpConfig;
import com.bazinga.loom.query.QuoteIpConfigQuery;

import java.util.List;

/**
 * 〈QuoteIpConfig Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
public interface QuoteIpConfigService {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    QuoteIpConfig save(QuoteIpConfig record);

    /**
     * 根据ID查询
     *
     * @param id 数据库ID
     */
    QuoteIpConfig getById(Long id);

    /**
     * 根据id更新一条数据
     *
     * @param record 更新参数
     */
    int updateById(QuoteIpConfig record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<QuoteIpConfig> listByCondition(QuoteIpConfigQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    int countByCondition(QuoteIpConfigQuery query);
}