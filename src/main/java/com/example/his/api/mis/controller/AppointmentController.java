package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.mis.controller.form.*;
import com.example.his.api.mis.service.AppointmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController("MisAppointmentController")
@RequestMapping("/mis/appointment")
public class AppointmentController {
    @Resource
    private AppointmentService appointmentService;

    @PostMapping("/searchByOrderId")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R searchByOrderId(@RequestBody @Valid SearchAppointmentByOrderIdForm form) {
        ArrayList<HashMap> list = appointmentService.searchByOrderId(form.getOrderId());
        return R.ok().put("result", list);
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchAppointmentByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        param.put("start", start);
        PageUtils pageUtils = appointmentService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteAppointmentByIdsForm form) {
        int rows = appointmentService.deleteByIds(form.getIds());
        return R.ok().put("rows", rows);
    }

    @PostMapping("/hasAppointInToday")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R hasAppointInToday(@RequestBody @Valid HasAppointInTodayForm form) {
        Map param = BeanUtil.beanToMap(form);
        int result = appointmentService.hasAppointInToday(param);
        return R.ok().put("result", result);
    }

    @PostMapping("/checkin")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R checkin(@RequestBody @Valid CheckinAppointmentForm form) {
        Map param = BeanUtil.beanToMap(form);
        boolean result = appointmentService.checkin(param);
        return R.ok().put("result", result);
    }

    @PostMapping("/searchGuidanceInfo")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R searchGuidanceInfo(@RequestBody @Valid SearchGuidanceInfoForm form) {
        HashMap map = appointmentService.searchGuidanceInfo(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/updateStatusByUuid")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:UPDATE"}, mode = SaMode.OR)
    public R updateStatusByUuid(@RequestBody @Valid UpdateAppointmentStatusByUuidForm form) {
        Map<String, Object> param = BeanUtil.beanToMap(form);
        boolean bool = appointmentService.updateStatusByUuid(param);
        return R.ok().put("result", bool);
    }

    @PostMapping("/searchByUuid")
    @SaCheckPermission(value = {"ROOT", "APPOINTMENT:SELECT"}, mode = SaMode.OR)
    public R searchByUuid(@RequestBody @Valid SearchAppointmentByUuidForm form) {
        HashMap map = appointmentService.searchByUuid(form.getUuid());
        return R.ok().put("result", map);
    }
}
