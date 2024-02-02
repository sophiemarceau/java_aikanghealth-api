package com.example.his.api.front.service;

import com.example.his.api.common.PageUtils;
import com.example.his.api.db.pojo.AppointmentEntity;

import java.util.Map;

public interface AppointmentService {
    public String insert(AppointmentEntity entity);

    public PageUtils searchByPage(Map param);


}
