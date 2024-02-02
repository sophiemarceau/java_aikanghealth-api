package com.example.his.api.async;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.example.his.api.db.dao.AppointmentRestrictionDao;
import com.example.his.api.db.dao.SystemDao;
import com.example.his.api.db.pojo.SystemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    @Async("AsyncTaskExecutor")
    public void init() {
        //加载全局设置
        this.loadSystemSetting();
        //生成未来60天的体检日程缓存
        this.createAppointmentCache();
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

    private void loadSystemSetting() {
        ArrayList<SystemEntity> list = systemDao.searchAll();
        list.forEach(one -> {
            redisTemplate.opsForValue().set("setting#" + one.getItem(), one.getValue());
        });
        log.debug("系统设置缓存成功");
    }

}
