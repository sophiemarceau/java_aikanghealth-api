package com.example.his.api.mis.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.db.Page;
import com.example.his.api.common.PageUtils;
import com.example.his.api.db.dao.FlowRegulationDao;
import com.example.his.api.db.pojo.FlowRegulationEntity;
import com.example.his.api.mis.service.FlowRegulationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@Service
public class FlowRegulationServiceImpl implements FlowRegulationService {
    @Resource
    private FlowRegulationDao flowRegulationDao;

    @Override
    public ArrayList<String> searchPlaceList() {
        ArrayList<String> list = flowRegulationDao.searchPlaceList();
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<FlowRegulationEntity> list = new ArrayList<>();
        long count = flowRegulationDao.searchCount(param);
        if (count > 0) {
            list = flowRegulationDao.searchByPage(param);
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    @Transactional
    public int insert(FlowRegulationEntity entity) {
        int rows = flowRegulationDao.insert(entity);
        return rows;
    }

    @Override
    public FlowRegulationEntity searchById(int id) {
        FlowRegulationEntity entity = flowRegulationDao.searchById(id);
        return entity;
    }

    @Override
    @Transactional
    public int update(Map param) {
        int rows = flowRegulationDao.update(param);
        return rows;
    }

    @Override
    @Transactional
    public int deleteByIds(Integer[] ids) {
        int rows = flowRegulationDao.deleteByIds(ids);
        return rows;
    }
}
