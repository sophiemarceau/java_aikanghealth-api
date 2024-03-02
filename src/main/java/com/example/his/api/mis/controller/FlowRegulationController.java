package com.example.his.api.mis.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import com.example.his.api.common.PageUtils;
import com.example.his.api.common.R;
import com.example.his.api.db.pojo.FlowRegulationEntity;
import com.example.his.api.mis.controller.form.*;
import com.example.his.api.mis.service.FlowRegulationService;
import com.example.his.api.mis.service.SystemService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/mis/flow_regulation")
public class FlowRegulationController {
    @Resource
    private FlowRegulationService flowRegulationService;
    @Resource
    private SystemService systemService;

    @GetMapping("/searchPlaceList")
    @SaCheckLogin
    public R searchPlaceList() {
        ArrayList<String> list = flowRegulationService.searchPlaceList();
        return R.ok().put("result", list);
    }

    @GetMapping("/searchMode")
    @SaCheckLogin
    public R searchMode() {
        String value = systemService.getItemValue("auto_flow_regulation");
        return R.ok().put("result", Boolean.parseBoolean(value));
    }

    @PostMapping("/searchByPage")
    @SaCheckPermission(value = {"ROOT", "FLOW_REGULATION:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody @Valid SearchFlowRegulationByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        Map param = BeanUtil.beanToMap(form);
        param.put("start", start);
        PageUtils pageUtils = flowRegulationService.searchByPage(param);
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "FLOW_REGULATION:INSERT"}, mode = SaMode.OR)
    public R insert(@RequestBody @Valid InsertFlowRegulationForm form) {
        FlowRegulationEntity entity = BeanUtil.toBean(form, FlowRegulationEntity.class);
        int rows = flowRegulationService.insert(entity);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/searchById")
    @SaCheckPermission(value = {"ROOT", "FLOW_REGULATION:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody @Valid SearchFlowRegulationByIdForm form) {
        FlowRegulationEntity entity = flowRegulationService.searchById(form.getId());
        return R.ok().put("result", entity);
    }

    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "FLOW_REGULATION:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody @Valid UpdateFlowRegulationForm form) {
        Map param = BeanUtil.beanToMap(form);
        int rows = flowRegulationService.update(param);
        return R.ok().put("rows", rows);
    }

    @PostMapping("/deleteByIds")
    @SaCheckPermission(value = {"ROOT", "FLOW_REGULATION:DELETE"}, mode = SaMode.OR)
    public R deleteByIds(@RequestBody @Valid DeleteFlowRegulationByIdsForm form) {
        int rows = flowRegulationService.deleteByIds(form.getIds());
        return R.ok().put("rows", rows);
    }

    @PostMapping("/updateMode")
    @SaCheckPermission(value = {"ROOT", "FLOW_REGULATION:UPDATE"}, mode = SaMode.OR)
    public R updateMode(@RequestBody @Valid UpdateFlowRegulationModeForm form) {
        boolean bool = systemService.setItemValue("auto_flow_regulation", form.getMode().toString());
        return R.ok().put("result", bool);
    }
}
