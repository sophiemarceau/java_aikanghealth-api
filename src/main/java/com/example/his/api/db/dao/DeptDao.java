package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.DeptEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_dept(部门表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.DeptEntity
 */
public interface DeptDao {
    public ArrayList<HashMap> searchAllDept();

    public ArrayList<HashMap> searchByPage(Map param);

    public long searchCount(Map param);

    public int insert(DeptEntity dept);

    public HashMap searchById(int id);

    public int update(DeptEntity dept);

    public boolean searchCanDelete(Integer[] ids);

    public int deleteByIds(Integer[] ids);
}




