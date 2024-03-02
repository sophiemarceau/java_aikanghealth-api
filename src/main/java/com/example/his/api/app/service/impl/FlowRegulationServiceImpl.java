package com.example.his.api.app.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.his.api.app.service.FlowRegulationService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service("AppFlowRegulationServiceImpl")
public class FlowRegulationServiceImpl implements FlowRegulationService {
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public ArrayList searchRecommendedPlace() {
        List list = redisTemplate.opsForList().range("flow_regulation", 0, -1);
        ArrayList result = new ArrayList();
        list.forEach(one -> {
            JSONObject json = JSONUtil.parseObj(one.toString());
            result.add(json);
        });
        return result;
    }
}
