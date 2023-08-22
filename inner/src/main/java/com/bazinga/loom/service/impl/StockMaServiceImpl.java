package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.StockMaDAO;
import com.bazinga.loom.model.StockMa;
import com.bazinga.loom.query.StockMaQuery;
import com.bazinga.loom.service.StockMaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈StockMa Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-08-22
 */
@Service
public class StockMaServiceImpl implements StockMaService {

    @Autowired
    private StockMaDAO stockMaDAO;

    @Override
    public StockMa save(StockMa record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        stockMaDAO.insert(record);
        return record;
    }

    @Override
    public StockMa getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return stockMaDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(StockMa record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return stockMaDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<StockMa> listByCondition(StockMaQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return stockMaDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(StockMaQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return stockMaDAO.countByCondition(query);
    }

    @Override
    public StockMa getByUniqueKey(String uniqueKey) {
        Assert.notNull(uniqueKey, "唯一键不能为空");
        return stockMaDAO.selectByUniqueKey(uniqueKey);
    }

    @Override
    public int updateByUniqueKey(StockMa record) {
        Assert.notNull(record, "待更新记录不能为空");
        return stockMaDAO.updateByUniqueKey(record);
    }
}