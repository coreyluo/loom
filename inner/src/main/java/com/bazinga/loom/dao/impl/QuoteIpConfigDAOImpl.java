package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.QuoteIpConfigDAO;
import com.bazinga.loom.model.QuoteIpConfig;
import com.bazinga.loom.query.QuoteIpConfigQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈QuoteIpConfig DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-04
  */
@Repository
public class QuoteIpConfigDAOImpl extends SqlSessionDaoSupport implements QuoteIpConfigDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.QuoteIpConfigDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(QuoteIpConfig record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public QuoteIpConfig selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(QuoteIpConfig record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<QuoteIpConfig> selectByCondition(QuoteIpConfigQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(QuoteIpConfigQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }
}