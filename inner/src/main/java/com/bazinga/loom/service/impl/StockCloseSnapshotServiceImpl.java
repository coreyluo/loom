package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.StockCloseSnapshotDAO;
import com.bazinga.loom.model.StockCloseSnapshot;
import com.bazinga.loom.query.StockCloseSnapshotQuery;
import com.bazinga.loom.service.StockCloseSnapshotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈StockCloseSnapshot Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-11
 */
@Service
public class StockCloseSnapshotServiceImpl implements StockCloseSnapshotService {

    @Autowired
    private StockCloseSnapshotDAO stockCloseSnapshotDAO;

    @Override
    public StockCloseSnapshot save(StockCloseSnapshot record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        stockCloseSnapshotDAO.insert(record);
        return record;
    }

    @Override
    public StockCloseSnapshot getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return stockCloseSnapshotDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(StockCloseSnapshot record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return stockCloseSnapshotDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<StockCloseSnapshot> listByCondition(StockCloseSnapshotQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return stockCloseSnapshotDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(StockCloseSnapshotQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return stockCloseSnapshotDAO.countByCondition(query);
    }

    @Override
    public StockCloseSnapshot getByUniqueKey(String uniqueKey) {
        Assert.notNull(uniqueKey, "唯一键不能为空");
        return stockCloseSnapshotDAO.selectByUniqueKey(uniqueKey);
    }

    @Override
    public int updateByUniqueKey(StockCloseSnapshot record) {
        Assert.notNull(record, "待更新记录不能为空");
        return stockCloseSnapshotDAO.updateByUniqueKey(record);
    }
}