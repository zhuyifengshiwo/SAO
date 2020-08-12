package com.powernode.workbench.service;

import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.pojo.TblClueRemark;

import java.util.List;
import java.util.Map;

public interface TblClueRemarkService{
    Map<String,Object> all (String id);
    //提交备注
    TblClueRemark add(String createBy, String aid, String content);
    //del
    public void delete(String id);
}
