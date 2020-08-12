package com.powernode.workbench.service;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClue;

import java.util.List;
import java.util.Map;

public interface CuleService {
    Map getCule();
    //分页查询
    PageResult pageList(String pageNum, String pageSize,String fullname, String company, String phone, String source, String owner, String mphone, String state);
    //添加线索
    void addCule(TblClue clue);
    void del(String [] id);
    TblClue select(String id);
    //查询线索对应活动信息
    List<TblActivity> get(String cid,String name);
    //修改线索
     void update(TblClue clue,String editBy);
}
