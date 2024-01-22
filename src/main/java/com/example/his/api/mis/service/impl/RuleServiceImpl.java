package com.example.his.api.mis.service.impl;

import com.example.his.api.db.dao.RuleDao;
import com.example.his.api.mis.service.RuleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class RuleServiceImpl implements RuleService {
    @Resource
    private RuleDao ruleDao;

    public ArrayList<HashMap> searchAllRule() {
        ArrayList<HashMap> list = ruleDao.searchAllRule();
        return list;
    }
}
