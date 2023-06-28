package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.StockOpenSnapshotDAO;
import com.bazinga.loom.model.StockOpenSnapshot;
import com.bazinga.loom.query.StockOpenSnapshotQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈StockOpenSnapshot DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-27
  */
@Repository
public class StockOpenSnapshotDAOImpl extends SqlSessionDaoSupport implements StockOpenSnapshotDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.StockOpenSnapshotDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(StockOpenSnapshot record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public StockOpenSnapshot selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(StockOpenSnapshot record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<StockOpenSnapshot> selectByCondition(StockOpenSnapshotQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(StockOpenSnapshotQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }

    @Override
    public StockOpenSnapshot selectByUniqueKey(String uniqueKey) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByUniqueKey", uniqueKey);
    }

    @Override
    public int updateByUniqueKey(StockOpenSnapshot record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByUniqueKey", record);
    }
}