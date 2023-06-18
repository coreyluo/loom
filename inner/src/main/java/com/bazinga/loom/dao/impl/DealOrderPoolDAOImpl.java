package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.DealOrderPoolDAO;
import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.query.DealOrderPoolQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈DealOrderPool DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-18
  */
@Repository
public class DealOrderPoolDAOImpl extends SqlSessionDaoSupport implements DealOrderPoolDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.DealOrderPoolDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(DealOrderPool record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public DealOrderPool selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(DealOrderPool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<DealOrderPool> selectByCondition(DealOrderPoolQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(DealOrderPoolQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }

    @Override
    public DealOrderPool selectByStockCodeGearTypeDay(String stockCodeGearTypeDay) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByStockCodeGearTypeDay", stockCodeGearTypeDay);
    }

    @Override
    public int updateByStockCodeGearTypeDay(DealOrderPool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByStockCodeGearTypeDay", record);
    }
}