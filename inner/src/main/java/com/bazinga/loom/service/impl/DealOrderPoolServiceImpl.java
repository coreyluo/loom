package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.DealOrderPoolDAO;
import com.bazinga.loom.model.DealOrderPool;
import com.bazinga.loom.query.DealOrderPoolQuery;
import com.bazinga.loom.service.DealOrderPoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈DealOrderPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-18
 */
@Service
public class DealOrderPoolServiceImpl implements DealOrderPoolService {

    @Autowired
    private DealOrderPoolDAO dealOrderPoolDAO;

    @Override
    public DealOrderPool save(DealOrderPool record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        dealOrderPoolDAO.insert(record);
        return record;
    }

    @Override
    public DealOrderPool getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return dealOrderPoolDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(DealOrderPool record) {
        Assert.notNull(record, "待更新记录不能为空");
        return dealOrderPoolDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<DealOrderPool> listByCondition(DealOrderPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return dealOrderPoolDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(DealOrderPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return dealOrderPoolDAO.countByCondition(query);
    }

    @Override
    public DealOrderPool getByStockCodeGearTypeDay(String stockCodeGearTypeDay) {
        Assert.notNull(stockCodeGearTypeDay, "唯一键不能为空");
        return dealOrderPoolDAO.selectByStockCodeGearTypeDay(stockCodeGearTypeDay);
    }

    @Override
    public int updateByStockCodeGearTypeDay(DealOrderPool record) {
        Assert.notNull(record, "待更新记录不能为空");
        return dealOrderPoolDAO.updateByStockCodeGearTypeDay(record);
    }
}