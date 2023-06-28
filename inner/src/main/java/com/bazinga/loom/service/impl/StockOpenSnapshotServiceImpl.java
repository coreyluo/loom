package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.StockOpenSnapshotDAO;
import com.bazinga.loom.model.StockOpenSnapshot;
import com.bazinga.loom.query.StockOpenSnapshotQuery;
import com.bazinga.loom.service.StockOpenSnapshotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈StockOpenSnapshot Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-27
 */
@Service
public class StockOpenSnapshotServiceImpl implements StockOpenSnapshotService {

    @Autowired
    private StockOpenSnapshotDAO stockOpenSnapshotDAO;

    @Override
    public StockOpenSnapshot save(StockOpenSnapshot record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        stockOpenSnapshotDAO.insert(record);
        return record;
    }

    @Override
    public StockOpenSnapshot getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return stockOpenSnapshotDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(StockOpenSnapshot record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return stockOpenSnapshotDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<StockOpenSnapshot> listByCondition(StockOpenSnapshotQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return stockOpenSnapshotDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(StockOpenSnapshotQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return stockOpenSnapshotDAO.countByCondition(query);
    }

    @Override
    public StockOpenSnapshot getByUniqueKey(String uniqueKey) {
        Assert.notNull(uniqueKey, "唯一键不能为空");
        return stockOpenSnapshotDAO.selectByUniqueKey(uniqueKey);
    }

    @Override
    public int updateByUniqueKey(StockOpenSnapshot record) {
        Assert.notNull(record, "待更新记录不能为空");
        return stockOpenSnapshotDAO.updateByUniqueKey(record);
    }
}