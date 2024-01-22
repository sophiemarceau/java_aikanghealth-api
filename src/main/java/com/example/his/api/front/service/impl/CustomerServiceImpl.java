package com.example.his.api.front.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.example.his.api.db.dao.CustomerDao;
import com.example.his.api.db.dao.OrderDao;
import com.example.his.api.db.pojo.CustomerEntity;
import com.example.his.api.front.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service("FrontCustmerSericeImpl")
@Slf4j
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomerDao customerDao;

    @Resource
    private OrderDao orderDao;

    @Override
    public boolean sendSmsCode(String tel) {
        //随机6未数字
        String code = RandomUtil.randomNumbers(6);
        System.out.println(code);
        String key = "sms_code_refresh_" + tel;
        //判断现在是否禁止向某个电话号码发送短信验证码
        if (redisTemplate.hasKey(key)) {
            return false;
        }
        //创建禁止重新发送短信验证码的缓存
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key, 2, TimeUnit.MINUTES);

        //数字和电话号码缓存Redis， 并设置过期时间
        key = "sms_code_" + tel;
        redisTemplate.opsForValue().set(key, code);
        redisTemplate.expire(key
                , 5, TimeUnit.MINUTES);
        //调用接口，发送验证码短信
        return true;
    }

    @Override
    @Transactional
    public HashMap login(String tel, String code) {
        HashMap map = new HashMap();

        String key = "sms_code_" + tel;
        if (!redisTemplate.hasKey(key)) {
            map.put("result", false);
            map.put("msg", "短信验证码已过期");
            return map;
        }
        String cacheCode = redisTemplate.opsForValue().get(key).toString();
        if (!cacheCode.equals(code)) {
            map.put("result", false);
            map.put("msg", "短信验证码不正确");
            return map;
        }
        redisTemplate.delete(key);
        key = "sms_code_refresh_" + tel;
        redisTemplate.delete(key);

        Integer id = customerDao.searchIdByTel(tel);
        if (id == null) {
            CustomerEntity entity = new CustomerEntity();
            entity.setTel(tel);
            customerDao.insert(entity);
            id = entity.getId();
        }
        map.put("id", id);
        map.put("result", true);
        map.put("msg", "登录成功");
        return map;
    }

    @Override
    public HashMap searchSummary(int id) {
        HashMap map = customerDao.searchById(id);
        map.putAll(orderDao.searchFrontStatistic(id));
        return map;
    }

    public boolean update(Map param) {
        int rows = customerDao.update(param);
        return rows == 1;
    }
}
