package com.example.his.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Administrator
 * @description 针对表【tb_permission(权限表)】的数据库操作Mapper
 * @createDate 2023-07-06 12:32:56
 * @Entity com.example.his.api.db.pojo.PermissionEntity
 */
public interface PermissionDao {
    public ArrayList<HashMap> searchAllPermission();
}




