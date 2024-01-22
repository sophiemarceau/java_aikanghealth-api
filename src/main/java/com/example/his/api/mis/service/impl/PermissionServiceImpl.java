package com.example.his.api.mis.service.impl;

import com.example.his.api.db.dao.PermissionDao;
import com.example.his.api.mis.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Resource
    private PermissionDao permissionDao;

    @Override
    public ArrayList<HashMap> searchAllPermission() {
        ArrayList<HashMap> list = permissionDao.searchAllPermission();
        return list;
    }
}
