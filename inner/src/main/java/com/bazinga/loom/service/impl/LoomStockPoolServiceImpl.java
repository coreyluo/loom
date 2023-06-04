package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.LoomStockPoolDAO;
import com.bazinga.loom.model.LoomStockPool;
import com.bazinga.loom.query.LoomStockPoolQuery;
import com.bazinga.loom.service.LoomStockPoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈LoomStockPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
@Service
public class LoomStockPoolServiceImpl implements LoomStockPoolService {

    @Autowired
    private LoomStockPoolDAO loomStockPoolDAO;

    @Override
    public LoomStockPool save(LoomStockPool record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        loomStockPoolDAO.insert(record);
        return record;
    }

    @Override
    public LoomStockPool getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return loomStockPoolDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(LoomStockPool record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return loomStockPoolDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<LoomStockPool> listByCondition(LoomStockPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return loomStockPoolDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(LoomStockPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return loomStockPoolDAO.countByCondition(query);
    }
}