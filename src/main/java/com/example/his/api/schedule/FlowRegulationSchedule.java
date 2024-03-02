package com.example.his.api.schedule;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.his.api.db.dao.FlowRegulationDao;
import com.example.his.api.exception.HisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Component
@Slf4j
public class FlowRegulationSchedule {
    @Resource
    private FlowRegulationDao flowRegulationDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 每天7-16点之间，每分钟计算1次推荐的科室列表
     */
//    @Scheduled(cron = "0,20,40 * * * * ?")
    @Scheduled(cron = "0 * 7-16 * * ?")
    @Transactional
    public void refreshFlowRegulation() {
        //查询所有调流科室
        ArrayList<HashMap> placeList = flowRegulationDao.searchAllByPlace();
        //遍历所有体检人的排队缓存
        placeList.forEach((one) -> {
            int id = Integer.parseInt(one.get("id").toString());
            String place = one.get("place").toString();
            //所有体检人的排队缓存
            Set<String> keys = redisTemplate.keys("flow_regulation_customer#*");
            int sum = 0;
            //遍历所有体检人缓存， 如果是在当前科室排队，就累加sum值
            for (String key : keys) {
                ValueOperations ops = redisTemplate.opsForValue();
                int placeId = Integer.parseInt(ops.get(key).toString());
                if (id == placeId) {
                    sum++;
                }
            }
            //把该调流科室排队人数更新到数据库
            HashMap param = new HashMap();
            param.put("id", id);
            param.put("realNum", sum);

            int rows = flowRegulationDao.updateRealNum(param);
            if (rows != 1) {
                throw new HisException("更新体检调流排队人数失败");
            }
        });
        //查询当前的模式
        String value = redisTemplate.opsForValue().get("setting#auto_flow_regulation").toString();
        boolean mode = Boolean.parseBoolean(value);
        ArrayList<HashMap> list = null;

        if (mode) {//自动限流模式推荐的科室排名
            list = flowRegulationDao.searchRecommendWithWeight();
        } else {//手动限流模式推荐的科室排名
            list = flowRegulationDao.searchRecommendWithPriority();
        }
        ArrayList result = new ArrayList<>();
        list.forEach((one) -> {
            JSONObject json = JSONUtil.parseObj(one);
            result.add(json.toString());
        });

        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.delete("flow_regulation");
                operations.opsForList().rightPushAll("flow_regulation", result);
                return null;
            }
        });
        log.debug("更新了体检调流缓存");
    }
}
