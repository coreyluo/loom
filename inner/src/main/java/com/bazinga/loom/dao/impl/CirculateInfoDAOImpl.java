package com.bazinga.loom.dao.impl;

import com.bazinga.loom.dao.CirculateInfoDAO;
import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.query.CirculateInfoQuery;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import org.springframework.util.Assert;

 /**
  * 〈CirculateInfo DAO〉<p>
  * 〈功能详细描述〉
  *
  * @author
  * @date 2023-06-04
  */
@Repository
public class CirculateInfoDAOImpl extends SqlSessionDaoSupport implements CirculateInfoDAO {

    private final String MAPPER_NAME = "com.bazinga.loom.dao.CirculateInfoDAO";

    @Resource
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    @Override
    public int insert(CirculateInfo record) {
        return this.getSqlSession().insert( MAPPER_NAME + ".insert", record);
    }

    @Override
    public CirculateInfo selectByPrimaryKey(Long id) {
        return this.getSqlSession().selectOne( MAPPER_NAME + ".selectByPrimaryKey", id);
    }

    @Override
    public int updateByPrimaryKeySelective(CirculateInfo record) {
        return this.getSqlSession().update( MAPPER_NAME + ".updateByPrimaryKeySelective", record);
    }

    @Override
    public List<CirculateInfo> selectByCondition(CirculateInfoQuery query) {

        return this.getSqlSession().selectList( MAPPER_NAME + ".selectByCondition", query);
    }

    @Override
    public Integer countByCondition(CirculateInfoQuery query) {

        return (Integer)this.getSqlSession().selectOne( MAPPER_NAME + ".countByCondition", query);
    }
}