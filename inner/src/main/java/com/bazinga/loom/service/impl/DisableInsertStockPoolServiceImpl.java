package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.DisableInsertStockPoolDAO;
import com.bazinga.loom.model.DisableInsertStockPool;
import com.bazinga.loom.query.DisableInsertStockPoolQuery;
import com.bazinga.loom.service.DisableInsertStockPoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈DisableInsertStockPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-13
 */
@Service
public class DisableInsertStockPoolServiceImpl implements DisableInsertStockPoolService {

    @Autowired
    private DisableInsertStockPoolDAO disableInsertStockPoolDAO;

    @Override
    public DisableInsertStockPool save(DisableInsertStockPool record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        disableInsertStockPoolDAO.insert(record);
        return record;
    }

    @Override
    public DisableInsertStockPool getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return disableInsertStockPoolDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(DisableInsertStockPool record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return disableInsertStockPoolDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<DisableInsertStockPool> listByCondition(DisableInsertStockPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return disableInsertStockPoolDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(DisableInsertStockPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return disableInsertStockPoolDAO.countByCondition(query);
    }

    @Override
    public DisableInsertStockPool getByStockCodeGear(String stockCodeGear) {
        Assert.notNull(stockCodeGear, "唯一键不能为空");
        return disableInsertStockPoolDAO.selectByStockCodeGear(stockCodeGear);
    }

    @Override
    public int updateByStockCodeGear(DisableInsertStockPool record) {
        Assert.notNull(record, "待更新记录不能为空");
        return disableInsertStockPoolDAO.updateByStockCodeGear(record);
    }
}