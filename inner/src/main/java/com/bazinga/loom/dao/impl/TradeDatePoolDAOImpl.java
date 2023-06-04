package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.TradeDatePoolDAO;
import com.bazinga.loom.model.TradeDatePool;
import com.bazinga.loom.query.TradeDatePoolQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈TradeDatePool DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-04
  */
@Repository
public class TradeDatePoolDAOImpl extends SqlSessionDaoSupport implements TradeDatePoolDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.TradeDatePoolDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(TradeDatePool record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public TradeDatePool selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(TradeDatePool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<TradeDatePool> selectByCondition(TradeDatePoolQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(TradeDatePoolQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }
}