package com.example.his.api.controller;

import com.example.his.api.common.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class Test {
    @GetMapping("/demo")
    public R demo() {
        return R.ok("执行成功");
    }
}
