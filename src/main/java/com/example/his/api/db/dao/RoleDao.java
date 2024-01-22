package com.example.his.api.db.dao;

import com.example.his.api.db.pojo.RoleEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【tb_role(角色表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.RoleEntity
 */
public interface RoleDao {
    public ArrayList<HashMap> searchAllRole();

    public ArrayList<HashMap> searchByPage(Map param);

    public long searchCount(Map param);

    public int insert(RoleEntity role);

    public HashMap searchById(int id);

    public ArrayList<Integer> searchUserIdByRoleId(int roleId);

    public int update(RoleEntity role);

    public boolean searchCanDelete(Integer[] ids);

    public int deleteByIds(Integer[] ids);
}




