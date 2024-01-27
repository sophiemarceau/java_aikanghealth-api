package com.example.his.api.front.service.impl;

import com.example.his.api.db.dao.AppointmentDao;
import com.example.his.api.front.service.AppointmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service("MisAppointmentServiceImpl")
public class AppointmentServiceImpl implements AppointmentService {
    @Resource
    private AppointmentDao appointmentDao;

    @Override
    public ArrayList<HashMap> searchByOrderId(int orderId) {
        ArrayList<HashMap> list = appointmentDao.searchByOrderId(orderId);
        return list;
    }
}
