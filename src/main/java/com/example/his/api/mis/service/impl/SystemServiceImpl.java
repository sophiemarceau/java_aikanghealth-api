package com.example.his.api.mis.service.impl;

import com.example.his.api.db.dao.SystemDao;
import com.example.his.api.mis.service.SystemService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class SystemServiceImpl implements SystemService {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SystemDao systemDao;

    @Override
    public String getItemValue(String item) {
        String value = redisTemplate.opsForValue().get("setting#" + item).toString();
        return value;
    }

    @Override
    @Transactional
    public boolean setItemValue(String item, String value) {
        HashMap param = new HashMap<>() {{
            put("item", item);
            put("value", value);
        }};
        int rows = systemDao.update(param);
        if (rows == 1) {
            redisTemplate.opsForValue().set("setting#" + item, value);
            return true;
        } else {
            return false;
        }
    }


}
