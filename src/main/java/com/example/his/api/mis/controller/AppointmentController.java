package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.json.JSONUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.mis.controller.form.DeleteAppointmentByIdsForm;
import com.example.his.api.mis.controller.form.SearchAppointmentByOrderIdForm;
import com.example.his.api.mis.controller.form.SearchAppointmentByPageForm;
import com.example.his.api.mis.service.AppointmentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

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
}
