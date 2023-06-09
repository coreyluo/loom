package com.bazinga.loom.dao;

import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.query.CirculateInfoQuery;

import java.util.List;

/**
 * 〈CirculateInfo DAO〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
public interface CirculateInfoDAO {

    /**
     * 新增一条记录
     *
     * @param record 保存对象
     */
    int insert(CirculateInfo record);

    /**
     * 根据主键查询
     *
     * @param id 数据库主键
     */
    CirculateInfo selectByPrimaryKey(Long id);

    /**
     * 根据主键更新数据
     *
     * @param record 更新参数
     */
    int updateByPrimaryKeySelective(CirculateInfo record);

    /**
     * 根据查询参数查询数据
     *
     * @param query 查询参数
     */
    List<CirculateInfo> selectByCondition(CirculateInfoQuery query);

    /**
     * 根据查询参数查询数据总量
     *
     * @param query 查询参数
     */
    Integer countByCondition(CirculateInfoQuery query);

}