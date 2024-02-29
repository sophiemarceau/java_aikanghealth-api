package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.example.his.api.common.R;
import com.example.his.api.mis.controller.form.AddCheckupResultForm;
import com.example.his.api.mis.controller.form.SearchCheckupByPlaceForm;
import com.example.his.api.mis.service.CheckupService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/mis/checkup")
public class CheckupController {
    @Resource
    private CheckupService checkupService;

    @PostMapping("/searchCheckupByPlace")
    @SaCheckPermission(value = {"ROOT", "CHECKUP:SELECT"}, mode = SaMode.OR)
    public R searchCheckupByPlace(@RequestBody @Valid SearchCheckupByPlaceForm form) {
        HashMap map = checkupService.searchCheckupByPlace(form.getUuid(), form.getPlace());
        return R.ok().put("result", map);
    }

    @PostMapping("/addResult")
    @SaCheckPermission(value = {"ROOT", "CHECKUP:INSERT", "CHECKUP:UPDATE"}, mode = SaMode.OR)
    public R addResult(@RequestBody @Valid AddCheckupResultForm form) {
        int userId = StpUtil.getLoginIdAsInt();
        checkupService.addResult(userId, form.getName(), form.getUuid(), form.getPlace(), form.getTemplate(), form.getItem());
        return R.ok();
    }
}