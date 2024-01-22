package com.example.his.api.schedule;

import com.example.his.api.db.dao.OrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Component
@Slf4j
public class OrderSchedule {
    @Resource
    private OrderDao orderDao;
    /**
     * 每小时执行一次 关闭超过30分钟未交钱的订单
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void closeUnpaymentOrder() {
        int rows = orderDao.closeOrder();
        if (rows > 0) {
            log.info("关闭了" + rows + "个未付款的订单");
        }
    }
}
