package com.bazinga.loom.dao;

import com.bazinga.loom.model.QuoteIpConfig;
import com.bazinga.loom.query.QuoteIpConfigQuery;

import java.util.List;

/**
 * 〈QuoteIpConfig DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
public interface QuoteIpConfigDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(QuoteIpConfig record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    QuoteIpConfig selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(QuoteIpConfig record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<QuoteIpConfig> selectByCondition(QuoteIpConfigQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(QuoteIpConfigQuery query);

}