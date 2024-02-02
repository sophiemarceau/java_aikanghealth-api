package com.example.his.api.mis.service.impl;

import com.example.his.api.common.PageUtils;
import com.example.his.api.db.dao.AppointmentDao;
import com.example.his.api.mis.service.AppointmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service("MisAppointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService {
    @Resource
    private AppointmentDao appointmentDao;

    @Override
    public ArrayList<HashMap> searchByOrderId(int orderId) {
        ArrayList<HashMap> list = appointmentDao.searchByOrderId(orderId);
        return list;
    }

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = new ArrayList<>();
        long count = appointmentDao.searchCount(param);
        if (count > 0) {
            list = appointmentDao.searchByPage(param);
        }
        int start = (Integer) param.get("start");
        int length = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, count, start, length);
        return pageUtils;
    }

    @Override
    public int deleteByIds(Integer[] ids) {
        int rows = appointmentDao.deleteByIds(ids);
        return rows;
    }


}
