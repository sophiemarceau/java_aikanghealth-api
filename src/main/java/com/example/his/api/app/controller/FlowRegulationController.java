package com.example.his.api.app.controller;

import com.example.his.api.app.service.FlowRegulationService;
import com.example.his.api.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController("AppFlowRegulationController")
@RequestMapping("/app/appointment")
public class FlowRegulationController {
    @Resource
    private FlowRegulationService flowRegulationService;

    @GetMapping("/searchRecommendedPlace")
    public R searchRecommendPlace() {
        List list = flowRegulationService.searchRecommendedPlace();
        return R.ok().put("result", list);
    }
}
