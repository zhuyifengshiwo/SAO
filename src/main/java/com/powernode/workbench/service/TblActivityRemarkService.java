package com.powernode.workbench.service;

import com.powernode.workbench.pojo.TblActivityRemark;

import java.util.List;

public interface TblActivityRemarkService {
    TblActivityRemark add (String createBy,String aid,String content);
    List<TblActivityRemark> selectAll(String aid);
    void delete(String id);
    TblActivityRemark update(String notecontent,String id);
}
