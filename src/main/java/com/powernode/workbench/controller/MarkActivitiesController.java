package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.PageResult;
import com.powernode.util.Result;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.service.ActivitiesService;
import com.powernode.workbench.service.impl.ActivitiesServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("act")
public class MarkActivitiesController {
    @Autowired
    private ActivitiesService service;
   @Value("${session}")
    private String SESSION_USER;
    @RequestMapping("/user")
    //查询客户数据
    public Result selectUser(){
        try {
            List<TblUser> user = service.getUser();
            return Result.OK(user);
        }catch (ResultException e){
            return Result.build(400,e);
        }
    }
    //添加数据
    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public Result add(TblActivity tblActivity, HttpServletRequest request){
        //添加id属性
        tblActivity.setId(UUIDUtil.getUUID());
        //添加创建时间使用系统当前时间
        tblActivity.setCreatetime(DateTimeUtil.getSysTime());


        try {
            service.add(tblActivity,request);
            return Result.OK();
        }catch (ResultException e){
            return Result.build(400,e);
        }

    }
    //分页查询数据
    @RequestMapping("/select")
    public Result select(@RequestParam(required = true,defaultValue = "1") String pageNum,
                         @RequestParam(required = true,defaultValue = "1") String pageSize,
                         @RequestParam(required =false) String owner,
                         @RequestParam(required =false) String name,
                         @RequestParam(required =false) String startdate,
                         @RequestParam(required =false) String enddate){
        try {
            PageResult list = service.getList(pageNum, pageSize, owner, name, startdate, enddate);
            return Result.OK(list);
        }catch (ResultException e){
            return Result.build(404,e);
        }
    }
  /*  //自己写的删除
    @RequestMapping("/del")
    public Result del(@RequestParam(required = true) String id){
        try {
            service.delect(id);
            return Result.OK();
        }catch (ResultException e){
            return Result.build(404,e);
        }


    }*/
//删除客户数据
    @RequestMapping("/del")
    public Result del(@RequestParam("its[]") String[] ids){
        try {
            service.delete(ids);
            return Result.OK();
        }catch (ResultException e){
            return Result.build(404,e);
        }
    }
    //获取更新页面信息
    @RequestMapping("/modify")
    public Result modify(String id){
        try {
            HashMap hashMap = new HashMap();
            List<TblUser> user = service.getUser();
            TblActivity activity = service.update(id);
            hashMap.put("user",user);
            hashMap.put("activity",activity);
            return Result.OK(hashMap);
        }catch(ResultException e){
            return Result.build(404,e);
        }
    }
    //修改页面信息
    @RequestMapping("/update")
    public Result update(TblActivity tblActivity,HttpServletRequest request){
        TblUser attribute =(TblUser)request.getSession().getAttribute(SESSION_USER);
        String name = attribute.getName();
        String sysTime = DateTimeUtil.getSysTime();
        tblActivity.setEditby(name);
        tblActivity.setEdittime(sysTime);
        try {
            service.update1(tblActivity);
            return Result.OK();
        }catch (ResultException e){
            return Result.build(404,e);
        }

    }

    //跳转活动页面显示
    @RequestMapping("/activity")
    public Result act (String id){
        try {
            TblActivity activity = service.actSelect(id);
            return Result.OK(activity);
        }catch (ResultException e){
            return Result.build(404,e);
        }
    }


}
