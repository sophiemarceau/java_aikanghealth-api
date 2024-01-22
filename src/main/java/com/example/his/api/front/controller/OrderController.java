package com.example.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.felord.payment.wechat.v3.WechatApiProvider;
import cn.felord.payment.wechat.v3.model.ResponseSignVerifyParams;
import cn.hutool.core.bean.BeanUtil;
import com.example.his.api.common.R;
import com.example.his.api.config.sa_token.StpCustomerUtil;
import com.example.his.api.front.controller.form.CreatePaymentForm;
import com.example.his.api.front.service.OrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            boolean bool = orderService.updatePayment(new HashMap() {{
                put("outTradeNo", outTradeNo);
                put("transactionId", transactionId);
            }});
        });
    }
}
