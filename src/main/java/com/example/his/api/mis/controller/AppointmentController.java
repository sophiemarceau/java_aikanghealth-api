package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.example.his.api.common.R;
import com.example.his.api.mis.controller.form.SearchAppointmentByOrderIdForm;
import com.example.his.api.front.service.AppointmentService;
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
}
