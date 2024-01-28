package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;
import com.example.his.api.db.pojo.RuleEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface RuleService {
    public ArrayList<HashMap> searchAllRule();

    public PageUtils searchByPage(Map param);

    public int insert(RuleEntity entity);

    public HashMap searchById(int id);

    public int update(RuleEntity entity);

    public int deleteById(int id);
}
