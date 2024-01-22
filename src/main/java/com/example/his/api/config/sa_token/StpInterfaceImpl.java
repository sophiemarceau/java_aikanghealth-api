package com.example.his.api.config.sa_token;

import cn.dev33.satoken.stp.StpInterface;
import com.example.his.api.db.dao.UserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private UserDao userDao;

    /**
     * 返回一个用户所拥有的权限集合
     */
    public  List<String> getPermissionList(Object loginId, String loginType) {
        List <String> list = new ArrayList<>();
        int userId = Integer.parseInt(loginId.toString());
        Set<String> set = userDao.searchUserPermissions(userId);
        list.addAll(set);
        return list;
    }

    /**
     * 返回一个用户所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object o, String s) {
        ArrayList<String> list = new ArrayList<>();
        return list;
    }
}
