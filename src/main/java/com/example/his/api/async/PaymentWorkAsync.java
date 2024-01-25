package com.example.his.api.async;

import com.example.his.api.db.dao.OrderDao;
import com.example.his.api.exception.HisException;
import com.example.his.api.front.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Slf4j
public class PaymentWorkAsync {
    @Resource
    private OrderDao orderDao;

    @Resource
    private PaymentService paymentService;

    @Async("AsyncTaskExecutor")//找到线程池，该方法执行会被线程池分配给空闲的线程
    @Transactional
    public void closeTimeoutRefund(int id, String outRefundNo) {
        String result = paymentService.searchPaymentResult(outRefundNo);
        if ("SUCCESS".equals(result)) {
            //查询订单状态已退款
            int rows = orderDao.updateRefundStatusById(id);
            if (rows != 1) {
                throw new HisException("订单更新未已退款状态失败");
            }
        } else if ("ABNORMAL".equals(result)) {
            /**
             * if 给用户发送过退款失败短信
             * else 没有发送过短信 给用户发送退款失败短信
             */
        }

    }
}
