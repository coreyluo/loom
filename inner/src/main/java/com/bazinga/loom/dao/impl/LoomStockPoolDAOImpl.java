package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.LoomStockPoolDAO;
import com.bazinga.loom.model.LoomStockPool;
import com.bazinga.loom.query.LoomStockPoolQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈LoomStockPool DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-04
  */
@Repository
public class LoomStockPoolDAOImpl extends SqlSessionDaoSupport implements LoomStockPoolDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.LoomStockPoolDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(LoomStockPool record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public LoomStockPool selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(LoomStockPool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<LoomStockPool> selectByCondition(LoomStockPoolQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(LoomStockPoolQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }
}