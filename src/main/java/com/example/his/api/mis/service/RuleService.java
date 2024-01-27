package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface RuleService {
    public ArrayList<HashMap> searchAllRule();

    public PageUtils searchByPage(Map param);
}
