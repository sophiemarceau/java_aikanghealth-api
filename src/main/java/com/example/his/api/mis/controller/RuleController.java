package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.db.pojo.RoleEntity;
import com.example.his.api.db.pojo.RuleEntity;
import com.example.his.api.mis.controller.form.*;
import com.example.his.api.mis.service.RuleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mis/rule")
public class RuleController {
    @Resource
    private RuleService ruleService;

    @GetMapping("/searchAllRule")
    @SaCheckLogin
    public R searchAllRule() {
        ArrayList<HashMap> list = ruleService.searchAllRule();
        return R.ok().put("result", list);
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "RULE:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@Valid @RequestBody SearchRuleByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = ruleService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "RULE:INSERT"}, mode = SaMode.OR)
    public R searchByPage(@Valid @RequestBody InsertRuleForm form) {
        RuleEntity entity = BeanUtil.toBean(form, RuleEntity.class);
        int rows = ruleService.insert(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "RULE:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchRoleByIdForm form) {
        HashMap map = ruleService.searchById(form.getId());
        return R.ok().put("result", map);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "RULE:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateRuleForm form) {
        RuleEntity entity = BeanUtil.toBean(form, RuleEntity.class);
        int rows = ruleService.update(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteById")
    @SaCheckPermission(value = {"ROOT", "RULE:DELETE"}, mode = SaMode.OR)
    public R deleteById(@RequestBody @Valid DeleteRuleByIdForm form) {
        int rows = ruleService.deleteById(form.getId());
        return R.ok().put("rows", rows);
    }
}
