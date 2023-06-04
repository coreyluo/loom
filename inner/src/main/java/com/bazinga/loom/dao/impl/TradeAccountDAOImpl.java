package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.TradeAccountDAO;
import com.bazinga.loom.model.TradeAccount;
import com.bazinga.loom.query.TradeAccountQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈TradeAccount DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-04
  */
@Repository
public class TradeAccountDAOImpl extends SqlSessionDaoSupport implements TradeAccountDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.TradeAccountDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(TradeAccount record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public TradeAccount selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(TradeAccount record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<TradeAccount> selectByCondition(TradeAccountQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(TradeAccountQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }
}