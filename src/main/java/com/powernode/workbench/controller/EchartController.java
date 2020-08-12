package com.powernode.workbench.controller;

import com.powernode.util.Result;
import com.powernode.workbench.service.EchartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("echart")
public class EchartController {
    @Autowired
    private EchartService service;
    //市场活动统计图表
    @RequestMapping("/active")
    public Result active(){
        Map<String, Object> name = service.getName();
        return Result.OK(name);

    }
    //线索统计表
    @RequestMapping("/cule")
    public Result cule(){
        Map<String, Object> cule = service.getCule();
        return Result.OK(cule);
    }
}
