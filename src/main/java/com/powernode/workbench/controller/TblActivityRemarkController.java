package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.service.TblActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("remark")
public class TblActivityRemarkController {
    @Autowired
    private TblActivityRemarkService service;
    @Value("${session}")
    private String SESSION_USER;

    @RequestMapping(value = "/notes",method = RequestMethod.POST)
    public Result notes(HttpServletRequest request, String aid, String content){
        TblUser attribute =(TblUser)request.getSession().getAttribute(SESSION_USER);
        String name = attribute.getName();
        try {
            TblActivityRemark remark = service.add(name, aid, content);
            return Result.OK(remark);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    //查询所有的备注
    @RequestMapping("/all")
    public Result all(String aid){
        try {
            List<TblActivityRemark> tblActivityRemarks = service.selectAll(aid);
            return Result.OK(tblActivityRemarks);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    //删除备注
    @RequestMapping("/del")
    public Result del(String tid){
        try {
            service.delete(tid);
            return Result.OK();
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    //修改备注
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public Result byupdate(String notecontent,String id){
        try {
            TblActivityRemark update = service.update(notecontent,id);
            return Result.OK(update);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
}
