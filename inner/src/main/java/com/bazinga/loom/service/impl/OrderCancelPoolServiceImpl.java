package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.OrderCancelPoolDAO;
import com.bazinga.loom.model.OrderCancelPool;
import com.bazinga.loom.query.OrderCancelPoolQuery;
import com.bazinga.loom.service.OrderCancelPoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈OrderCancelPool Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-12
 */
@Service
public class OrderCancelPoolServiceImpl implements OrderCancelPoolService {

    @Autowired
    private OrderCancelPoolDAO orderCancelPoolDAO;

    @Override
    public OrderCancelPool save(OrderCancelPool record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        orderCancelPoolDAO.insert(record);
        return record;
    }

    @Override
    public OrderCancelPool getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return orderCancelPoolDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(OrderCancelPool record) {
        Assert.notNull(record, "待更新记录不能为空");
        record.setUpdateTime(new Date());
        return orderCancelPoolDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<OrderCancelPool> listByCondition(OrderCancelPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return orderCancelPoolDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(OrderCancelPoolQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return orderCancelPoolDAO.countByCondition(query);
    }
}