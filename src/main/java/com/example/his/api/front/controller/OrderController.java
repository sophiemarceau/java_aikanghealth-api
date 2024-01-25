package com.example.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.config.sa_token.StpCustomerUtil;
import com.example.his.api.front.controller.form.CreatePaymentForm;
import com.example.his.api.front.controller.form.RefundForm;
import com.example.his.api.front.controller.form.SearchOrderByPageForm;
import com.example.his.api.front.controller.form.SearchPaymentResultForm;
import com.example.his.api.front.service.OrderService;
import com.example.his.api.socket.WebSocketService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController("FrontOderController")
@RequestMapping("/front/order")
@Slf4j
public class OrderController {
    @Resource
    private OrderService orderService;
    @Resource
    private WechatApiProvider wechatApiProvider;

    @PostMapping("/createPayment")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R createPayment(@RequestBody @Valid CreatePaymentForm form) {
        int customId = StpCustomerUtil.getLoginIdAsInt();
        Map param = BeanUtil.beanToMap(form);
        param.put("customerId", customId);
        HashMap map = orderService.createPayment(param);
        if (map == null) {
            return R.ok().put("illegal", true);
        } else {
            return R.ok().put("illegal", false).put("result", map);
        }
    }

    @SneakyThrows
    @PostMapping("/paymentCallback")
    public Map paymentCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request
    ) {
        String body = request.getReader().lines().collect(Collectors.joining());
        //请求头进行验签， 确保是微信服务器的调用
        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);
        return wechatApiProvider.callback("his-vue").transactionCallback(params, data -> {
            String transactionId = data.getTransactionId();
            String outTradeNo = data.getOutTradeNo();
            log.error("transactionId=====" + transactionId);
            log.error("outTradeNo=====" + outTradeNo);
            boolean bool = orderService.updatePayment(new HashMap() {{
                put("outTradeNo", outTradeNo);
                put("transactionId", transactionId);
            }});
            if (bool) {
                log.debug("订单付款成功，已更新订单状态");
                //查询订单的customId
                Integer customerId = orderService.searchCustomerId(outTradeNo);
                if (customerId == null) {
                    log.error("没有查询到customerId");
                } else {
                    //推送消息给前端页面
                    JSONObject json = new JSONObject();
                    json.set("result", true);
                    WebSocketService.sendInfo(json.toString(), "customer_" + customerId.toString());
                }
            } else {
                log.error("订单付款成功， 但状态更新失败！");
            }
        });
    }

    @PostMapping("/searchPaymentResult")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchPaymentResult(@Valid @RequestBody SearchPaymentResultForm form) {
        boolean bool = orderService.searchPaymentResult(form.getOutTradeNo());
        return R.ok().put("result", bool);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchByPage(@RequestBody @Valid SearchOrderByPageForm form) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        param.put("customerId", customerId);
        PageUtils pageUtils = orderService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/refund")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R refund(@RequestBody @Valid RefundForm form) {
        int customId = StpCustomerUtil.getLoginIdAsInt();
        form.setCustomerId(customId);
        Map param = BeanUtil.beanToMap(form);
        boolean bool = orderService.refund(param);
        return R.ok().put("result", bool);
    }

    @SneakyThrows
    @PostMapping("/refundCallback")
    public Map refundCallback(
            @RequestHeader("Wechatpay-Serial") String serial,
            @RequestHeader("Wechatpay-Signature") String signature,
            @RequestHeader("Wechatpay-Timestamp") String timestamp,
            @RequestHeader("Wechatpay-Nonce") String nonce,
            HttpServletRequest request
    ) {
        String body = request.getReader().lines().collect(Collectors.joining());
        //验证数字签名，确保是wechat server发送的通信消息
        ResponseSignVerifyParams params = new ResponseSignVerifyParams();
        params.setWechatpaySerial(serial);
        params.setWechatpaySignature(signature);
        params.setWechatpayTimestamp(timestamp);
        params.setWechatpayNonce(nonce);
        params.setBody(body);
        return wechatApiProvider.callback("his-vue").refundCallback(params, data -> {
            //退款是否成功
            String status = data.getRefundStatus().toString();
            if ("SUCCESS".equals(status)) {
                String outRefundNo = data.getOutRefundNo();
                //订单状态更新成 已退款
                boolean bool = orderService.updateRefundStatus(outRefundNo);
                if (!bool) {
                    log.error("订单状态更新失败");
                } else {
                    log.debug("退款流水号为" + outRefundNo + "的订单退款成功");
                    log.error("退款流水号为" + outRefundNo + "的订单退款成功");
                }
            } else if ("ABNORMAL".equals(status)) {
                //退款的银行开 不能用， 发送短信给用户手机，让用户联系客服执行手动退款到其他银行卡
            }
        });
    }
}
