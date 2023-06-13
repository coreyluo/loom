package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.DisableInsertStockPoolDAO;
import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.query.DisableInsertStockPoolQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈DisableInsertStockPool DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-13
  */
@Repository
public class DisableInsertStockPoolDAOImpl extends SqlSessionDaoSupport implements DisableInsertStockPoolDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.DisableInsertStockPoolDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(DisableInsertStockPool record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public DisableInsertStockPool selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(DisableInsertStockPool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<DisableInsertStockPool> selectByCondition(DisableInsertStockPoolQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(DisableInsertStockPoolQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }

    @Override
    public DisableInsertStockPool selectByStockCodeGear(String stockCodeGear) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByStockCodeGear", stockCodeGear);
    }

    @Override
    public int updateByStockCodeGear(DisableInsertStockPool record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByStockCodeGear", record);
    }
}