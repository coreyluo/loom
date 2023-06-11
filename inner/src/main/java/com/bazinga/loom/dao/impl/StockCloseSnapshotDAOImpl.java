package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.StockCloseSnapshotDAO;
import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.query.StockCloseSnapshotQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈StockCloseSnapshot DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-11
  */
@Repository
public class StockCloseSnapshotDAOImpl extends SqlSessionDaoSupport implements StockCloseSnapshotDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.StockCloseSnapshotDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(StockCloseSnapshot record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public StockCloseSnapshot selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(StockCloseSnapshot record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<StockCloseSnapshot> selectByCondition(StockCloseSnapshotQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(StockCloseSnapshotQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }

    @Override
    public StockCloseSnapshot selectByUniqueKey(String uniqueKey) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByUniqueKey", uniqueKey);
    }

    @Override
    public int updateByUniqueKey(StockCloseSnapshot record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByUniqueKey", record);
    }
}