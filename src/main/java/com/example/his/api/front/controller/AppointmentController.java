package com.example.his.api.front.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.IdcardUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.config.sa_token.StpCustomerUtil;
import com.example.his.api.db.pojo.AppointmentEntity;
import com.example.his.api.exception.HisException;
import com.example.his.api.front.controller.form.InsertAppointmentForm;
import com.example.his.api.front.controller.form.SearchAppointmentByPageForm;
import com.example.his.api.front.service.AppointmentService;
import com.example.his.api.front.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController("FrontAppointController")
@RequestMapping("/front/appointment")
public class AppointmentController {
    @Resource
    private OrderService orderService;
    @Resource
    private AppointmentService appointmentService;

    @PostMapping("/insert")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R insert(@RequestBody @Valid InsertAppointmentForm form) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        HashMap param = new HashMap() {{
            put("customerId", customerId);
            put("id", form.getOrderId());
        }};
        boolean bool = orderService.hasOwnOrder(param);
        if (!bool) {
            throw new HisException("预约失败，该订单与您无关");
        }
        String pid = form.getPid();
        //验证身份证是否有效
        if (!IdcardUtil.isValidCard18(pid)) {
            throw new HisException("身份证号码无效");
        }
        String birthDay = IdcardUtil.getBirthDate(pid).toDateStr();
        String sex = IdcardUtil.getGenderByIdCard(pid) == 1 ? "男" : "女";
        //验证日期是否为未来60天以内
        DateTime dateTime = DateUtil.parse(form.getDate());
        DateTime tomorrow = DateUtil.tomorrow();//当前时刻的24小时之后
        DateTime startTime = DateUtil.parse(tomorrow.toDateStr());
        DateTime endDate = tomorrow.offset(DateField.DAY_OF_MONTH, 60);
        boolean temp = dateTime.isIn(startTime, endDate);
        if (!temp) {
            throw new HisException("预约日期错误");
        }
        AppointmentEntity entity = BeanUtil.toBean(form, AppointmentEntity.class);
        entity.setUuid(IdUtil.simpleUUID().toUpperCase());
        entity.setBirthday(birthDay);
        entity.setSex(sex);

        String result = appointmentService.insert(entity);
        return R.ok().put("result", result);
    }

    @PostMapping("/searchByPage")
    @SaCheckLogin(type = StpCustomerUtil.TYPE)
    public R searchByPage(@RequestBody @Valid SearchAppointmentByPageForm form) {
        int customerId = StpCustomerUtil.getLoginIdAsInt();
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        param.put("customerId", customerId);
        PageUtils pageUtils = appointmentService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }
}
