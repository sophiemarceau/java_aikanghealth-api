package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.AppointmentEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_appointment(体检预约表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.AppointmentEntity
 */
public interface AppointmentDao {
    public ArrayList<HashMap> searchByOrderId(int orderId);

    public int insert(AppointmentEntity entity);

    public ArrayList<HashMap> searchFrontAppointmentByPage(Map param);

    public long searchFrontAppointmentCount(Map param);

    public ArrayList<HashMap> searchByPage(Map param);

    public long searchCount(Map param);

    public int deleteByIds(Integer[] ids);
}




