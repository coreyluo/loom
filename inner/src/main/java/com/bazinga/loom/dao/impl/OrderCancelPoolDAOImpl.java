package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.OrderCancelPoolDAO;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.query.OrderCancelPoolQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈OrderCancelPool DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-12
  */
@Repository
public class OrderCancelPoolDAOImpl extends SqlSessionDaoSupport implements OrderCancelPoolDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.OrderCancelPoolDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(OrderCancelPool record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public OrderCancelPool selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(OrderCancelPool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<OrderCancelPool> selectByCondition(OrderCancelPoolQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(OrderCancelPoolQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }
}