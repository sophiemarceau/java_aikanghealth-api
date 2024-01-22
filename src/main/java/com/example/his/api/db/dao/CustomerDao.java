package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.CustomerEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_customer(客户表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.CustomerEntity
 */
public interface CustomerDao {
    public Integer searchIdByTel(String tel);

    public void insert(CustomerEntity entity);

    public HashMap searchById(int id);

    public int update(Map map);
}




