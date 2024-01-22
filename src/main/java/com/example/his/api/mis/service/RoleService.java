package com.example.his.api.mis.service;

import com.example.his.api.common.PageUtils;
import com.example.his.api.db.pojo.RoleEntity;

import java.util.ArrayList;
import java.util.HashMap;

public interface RoleService {
    public ArrayList<HashMap> searchAllRole();

    public PageUtils searchByPage(HashMap param);

    public int insert(RoleEntity role);

    public HashMap searchById(int id);

    public ArrayList<Integer> searchUserIdByRoleId(int roleId);

    public int update(RoleEntity role);

    public int deleteByIds(Integer[] ids);
}
