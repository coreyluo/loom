package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.CirculateInfoDAO;
import com.bazinga.loom.model.CirculateInfo;
import com.bazinga.loom.query.CirculateInfoQuery;
import com.bazinga.loom.service.CirculateInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈CirculateInfo Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
@Service
public class CirculateInfoServiceImpl implements CirculateInfoService {

    @Autowired
    private CirculateInfoDAO circulateInfoDAO;

    @Override
    public CirculateInfo save(CirculateInfo record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        circulateInfoDAO.insert(record);
        return record;
    }

    @Override
    public CirculateInfo getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return circulateInfoDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(CirculateInfo record) {
        Assert.notNull(record, "待更新记录不能为空");
        return circulateInfoDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<CirculateInfo> listByCondition(CirculateInfoQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return circulateInfoDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(CirculateInfoQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return circulateInfoDAO.countByCondition(query);
    }
}