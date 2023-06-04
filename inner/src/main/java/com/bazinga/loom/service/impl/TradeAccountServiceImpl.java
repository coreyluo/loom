package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.TradeAccountDAO;
import com.bazinga.loom.model.TradeAccount;
import com.bazinga.loom.query.TradeAccountQuery;
import com.bazinga.loom.service.TradeAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈TradeAccount Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
@Service
public class TradeAccountServiceImpl implements TradeAccountService {

    @Autowired
    private TradeAccountDAO tradeAccountDAO;

    @Override
    public TradeAccount save(TradeAccount record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        tradeAccountDAO.insert(record);
        return record;
    }

    @Override
    public TradeAccount getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return tradeAccountDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(TradeAccount record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return tradeAccountDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<TradeAccount> listByCondition(TradeAccountQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return tradeAccountDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(TradeAccountQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return tradeAccountDAO.countByCondition(query);
    }
}