package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface AppointmentService {
    public ArrayList<HashMap> searchByOrderId(int orderId);

    public PageUtils searchByPage(Map param);

    public int deleteByIds(Integer[] ids);

    public int hasAppointInToday(Map param);

    public boolean checkin(Map param);

    public HashMap searchGuidanceInfo(int id);

    public boolean updateStatusByUuid(Map param);

    public HashMap searchByUuid(String uuid);
}
