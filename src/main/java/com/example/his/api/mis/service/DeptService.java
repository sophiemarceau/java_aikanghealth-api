package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;
import com.example.his.api.db.pojo.DeptEntity;

import java.util.ArrayList;
import java.util.HashMap;

public interface DeptService {
    public ArrayList<HashMap> searchAllDept();

    public PageUtils searchByPage(HashMap param);

    public int insert(DeptEntity dept);

    public HashMap searchById(int id);

    public int update(DeptEntity dept);

    public int deleteByIds(Integer[] ids);
}
