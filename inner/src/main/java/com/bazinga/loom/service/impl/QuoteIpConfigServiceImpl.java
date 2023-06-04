package com.bazinga.loom.service.impl;

import com.bazinga.loom.dao.QuoteIpConfigDAO;
import com.bazinga.loom.model.QuoteIpConfig;
import com.bazinga.loom.query.QuoteIpConfigQuery;
import com.bazinga.loom.service.QuoteIpConfigService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * 〈QuoteIpConfig Service〉<p>
 * 〈功能详细描述〉
 *
 * @author
 * @date 2023-06-04
 */
@Service
public class QuoteIpConfigServiceImpl implements QuoteIpConfigService {

    @Autowired
    private QuoteIpConfigDAO quoteIpConfigDAO;

    @Override
    public QuoteIpConfig save(QuoteIpConfig record) {
        Assert.notNull(record, "待插入记录不能为空");
        record.setCreateTime(new Date());
        quoteIpConfigDAO.insert(record);
        return record;
    }

    @Override
    public QuoteIpConfig getById(Long id) {
        Assert.notNull(id, "主键不能为空");
        return quoteIpConfigDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateById(QuoteIpConfig record) {
        Assert.notNull(record, "待更新记录不能为空");
        return quoteIpConfigDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public List<QuoteIpConfig> listByCondition(QuoteIpConfigQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return quoteIpConfigDAO.selectByCondition(query);
    }

    @Override
    public int countByCondition(QuoteIpConfigQuery query) {
        Assert.notNull(query, "查询条件不能为空");
        return quoteIpConfigDAO.countByCondition(query);
    }
}