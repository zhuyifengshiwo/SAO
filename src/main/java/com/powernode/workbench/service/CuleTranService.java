package com.powernode.workbench.service;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblTran;
import com.powernode.workbench.pojo.TblTranHistory;

import java.util.List;
import java.util.Map;

public interface CuleTranService {
    //查询所有者
    List<String> geyList();
    //查询交易概率
    String get(String stage,Map map);
    //模糊查询客户名称
    Map<String,Object> getQuery(String name);
    //获取所有的市场活动
    List<TblActivity> getAct(String name);
    //获取所有的联系人
    List<TblContacts> getContacts(String name);
    //建立交易
    void add(TblTran tblTran,String creatBy);
    //分页查询
    PageResult getList(String pageNum, String pageSize, String name, String fullname, String stage, String transactionType, String owner, String source, String lname           );
    //修改页面展示
    Map<String,Object> editpage(String id,Map map2);
    //更新交易信息
    void update(TblTran tblTran,String editBy);
    //获得交易信息
    TblTran getTran(String id);
    //删除交易
    void del(String[] its);
    //点击交易页面
    TblTran getById(String id);
    //锚点点击
    Map<String,Object> possibility(Map value,Map possibility,String id);
    public TblTran addTranHistory(TblTranHistory tblTranHistory);
}
