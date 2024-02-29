package com.example.his.api.mis.service.impl;

import cn.hutool.core.date.DateUtil;
import com.example.his.api.db.dao.AppointmentDao;
import com.example.his.api.db.dao.CheckupResultDao;
import com.example.his.api.db.dao.GoodsSnapshotDao;
import com.example.his.api.db.dao.UserDao;
import com.example.his.api.mis.service.CheckupService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CheckupServiceImpl implements CheckupService {
    @Resource
    private GoodsSnapshotDao goodsSnapshotDao;
    @Resource
    private CheckupResultDao checkupResultDao;
    @Resource
    private AppointmentDao appointmentDao;
    @Resource
    private UserDao userDao;

    @Override
    public HashMap searchCheckupByPlace(String uuid, String place) {
        //取出体检项
        List<Map> checkupList = checkupResultDao.searchCheckupByPlace(uuid, place);
        //当前科室是否录入过体检结果
        boolean bool = checkupResultDao.hasAlreadyCheckup(uuid, place);
        HashMap<Object, Object> map = new HashMap<>() {{
            put("checkupList", checkupList);
            put("hasAlreadyCheckup", bool);
        }};
        return map;
    }

    @Override
    public void addResult(int userId, String name, String uuid, String place, String template, ArrayList item) {
        HashMap map = new HashMap() {{
            put("doctorId", userId);
            put("doctorName", name);
            put("date", DateUtil.today());
            put("place", place);
            put("template", template);
            put("item", item);
        }};
        checkupResultDao.addResult(uuid, place, map);
    }
}
