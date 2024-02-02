package com.example.his.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_appointment_restriction(体检预约限流表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.AppointmentRestrictionEntity
 */
public interface AppointmentRestrictionDao {
    public ArrayList<HashMap> searchScheduleInRange(Map param);

    public int saveOrUpdateRealNum(Map param);
}




