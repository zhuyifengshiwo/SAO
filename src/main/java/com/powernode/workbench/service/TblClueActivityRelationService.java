package com.powernode.workbench.service;

import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClueActivityRelation;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface TblClueActivityRelationService {
    //删除市场活动和线索关联
    void del(String id);
    //查询所有的市场活动关联信息
    List<Map<String,Object>> selectAll(String cid);
    List<TblActivity> selectActivity();
    List<TblActivity> lookupActivity(String name);
    void connect(String[] its,String cid);
    //创建客户
    void add(String id,String check, String money,String name, String date, String aid, String creatBy, String stage);
}
