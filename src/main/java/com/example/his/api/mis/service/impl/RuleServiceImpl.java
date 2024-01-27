package com.example.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.db.dao.RuleDao;
import com.example.his.api.mis.service.RuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class RuleServiceImpl implements RuleService {
    @Resource
    private RuleDao ruleDao;

    public ArrayList<HashMap> searchAllRule() {
        ArrayList<HashMap> list = ruleDao.searchAllRule();
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = ruleDao.searchCount(param);
        if (count > 0) {
            list = ruleDao.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }


}
