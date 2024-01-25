package com.example.his.api.schedule;

import cn.hutool.core.map.MapUtil;
import com.example.his.api.async.PaymentWorkAsync;
import com.example.his.api.db.dao.OrderDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class OrderSchedule {
    @Resource
    private OrderDao orderDao;
    @Resource
    PaymentWorkAsync paymentWorkAsync;

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


    /**
     * 每小时执行一次 处理未收到退款通知消息的订单
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void closeTimeoutRefundOrder() {
        //查询所有未收到退款订单通知消息的订单
        ArrayList<HashMap> list = orderDao.searchTimeoutRefund();
        list.forEach(map -> {
            int id = MapUtil.getInt(map, "id");
            String outRefundNo = MapUtil.getStr(map, "ourRefundNo");
            //异步线程查询退款结果，避免阻塞for循环
            paymentWorkAsync.closeTimeoutRefund(id, outRefundNo);
        });
    }
}
