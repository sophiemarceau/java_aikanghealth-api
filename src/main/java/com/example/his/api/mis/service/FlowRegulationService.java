package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;
import com.example.his.api.db.pojo.FlowRegulationEntity;

import java.util.ArrayList;
import java.util.Map;

public interface FlowRegulationService {
    public ArrayList<String> searchPlaceList();

    public PageUtils searchByPage(Map param);

    public int insert(FlowRegulationEntity entity);

    public FlowRegulationEntity searchById(int id);

    public int update(Map param);

    public int deleteByIds(Integer[] ids);
}
