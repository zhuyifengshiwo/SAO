package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClue;
import com.powernode.workbench.service.TblClueActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("clueactivity")
public class TblClueActivityRelationController {
    @Autowired
    TblClueActivityRelationService service;
    @RequestMapping("/del")
    public Result del(String id){
        try {
            service.del(id);
            return Result.OK();
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    //获取所有的市场活动关联
    @RequestMapping("/all")
    public Result getAll(String cid){
        try {
            List<Map<String, Object>> list = service.selectAll(cid);
            return Result.OK(list);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    @RequestMapping("/getremark")
    public Result getRemark(){
        try {
            List<TblActivity> tblActivityList = service.selectActivity();
            return Result.OK(tblActivityList);
        } catch (ResultException e) {
            return Result.build(404,e);
        }

    }
    @RequestMapping("/lookup")
    public Result lookup(String name){
        try {
            List<TblActivity> tblActivityList = service.lookupActivity(name);
            return Result.OK(tblActivityList);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    //关联
    @RequestMapping("/connect")
    public Result connect( @RequestParam("its[]") String[] ids, String cid){
        try {
            service.connect(ids,cid);
            return Result.OK();
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    @RequestMapping("/transformation")
    public Result transformation(@RequestParam String id,
                                 @RequestParam(required = false) String aid,
                                 @RequestParam String check,
                                 @RequestParam(required = false) String money,
                                 @RequestParam(required = false) String name,
                                 @RequestParam(required = false) String date,
                                 @RequestParam(required = false) String stage, HttpServletRequest request){
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        String creatBy = user.getName();
        service.add(id,check,money,name,date,aid,creatBy,stage);
       return Result.OK();

    }
}
