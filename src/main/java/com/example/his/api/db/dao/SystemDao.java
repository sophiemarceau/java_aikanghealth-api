package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.SystemEntity;

import java.util.ArrayList;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_system(系统表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.SystemEntity
 */
public interface SystemDao {
    public ArrayList<SystemEntity> searchAll();

    public int update(Map param);
}




