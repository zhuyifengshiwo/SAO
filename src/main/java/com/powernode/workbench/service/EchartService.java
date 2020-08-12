package com.powernode.workbench.service;

import java.util.Map;

public interface EchartService {
    //获取所有的市场活动名字
    Map<String,Object> getName();
    //线索统计图
    Map<String,Object> getCule();
}
