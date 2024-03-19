package com.example.his.api.async;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.his.api.db.dao.AppointmentRestrictionDao;
import com.example.his.api.db.dao.FlowRegulationDao;
import com.example.his.api.db.dao.SystemDao;
import com.example.his.api.db.pojo.SystemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@Slf4j
public class InitializeWorkAsync {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SystemDao systemDao;
    @Resource
    private AppointmentRestrictionDao appointmentRestrictionDao;
    @Resource
    private FlowRegulationDao flowRegulationDao;

    @Async("AsyncTaskExecutor")
    @Transactional
    public void init() {
        this.loadSystemSetting();//加载全局设置
        this.createAppointmentCache();//生成未来60天的体检日程缓存
        this.creatFlowRegulationCache();//缓存体检调流数据
    }

    private void loadSystemSetting() {
        ArrayList<SystemEntity> list = systemDao.searchAll();
        list.forEach(one -> {
            redisTemplate.opsForValue().set("setting#" + one.getItem(), one.getValue());
        });
        log.debug("系统设置缓存成功");
    }

    private void createAppointmentCache() {
        DateTime startDate = DateUtil.tomorrow();
        DateTime endDate = startDate.offsetNew(DateField.DAY_OF_MONTH, 60);
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        HashMap param = new HashMap() {{
            put("startDate", startDate.toDateStr());
            put("endDate", endDate.toDateStr());
        }};
        ArrayList<HashMap> list = appointmentRestrictionDao.searchScheduleInRange(param);
        range.forEach(one -> {
            String date = one.toDateStr();
            int maxNum = Integer.parseInt(redisTemplate.opsForValue().get("setting#appointment_number").toString());
            int realNum = 0;
            for (HashMap map : list) {
                String temp = MapUtil.getStr(map, "date");
                if (date.equals(temp)) {
                    maxNum = MapUtil.getInt(map, "num_1");
                    realNum = MapUtil.getInt(map, "num_3");
                    break;
                }
            }
            //设置缓存
            HashMap cache = new HashMap();
            cache.put("maxNum", maxNum);
            cache.put("realNum", realNum);
            String key = "appointment#" + date;
            redisTemplate.opsForHash().putAll(key, cache);
            DateTime dateTime = new DateTime(date).offsetNew(DateField.DAY_OF_MONTH, 1);
            redisTemplate.expireAt(key, dateTime);
        });
        log.debug("未来60day体检人数缓存设置成功！");
    }

    private void creatFlowRegulationCache() {
        //把所有调流科室排队人数更新成为0人
        flowRegulationDao.updateRealNum(new HashMap() {{
            put("realNum", 0);
        }});
        //查询当前的调流模式
        String value = redisTemplate.opsForValue().get("setting#auto_flow_regulation").toString();
        boolean mode = Boolean.parseBoolean(value);
        ArrayList<HashMap> list = null;

        if (mode) {//自动限流模式推荐的科室排名
            list = flowRegulationDao.searchRecommendWithWeight();
        } else {//手动限流模式推荐的科室排名
            list = flowRegulationDao.searchRecommendWithPriority();
        }
        ArrayList result = new ArrayList();
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
