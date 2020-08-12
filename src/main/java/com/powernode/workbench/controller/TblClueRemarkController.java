package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.pojo.TblClueRemark;
import com.powernode.workbench.service.TblClueRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("tblclueremark")
public class TblClueRemarkController {
    @Autowired
    private TblClueRemarkService service;
    @Value("${session}")
    private String SESSION_USER;

    @RequestMapping(value = "/notes",method = RequestMethod.POST)
    public Result notes(HttpServletRequest request, String aid, String content){
            TblUser attribute =(TblUser)request.getSession().getAttribute(SESSION_USER);
            String name = attribute.getName();
        TblClueRemark add = service.add(name, aid, content);
        return Result.OK(add);

    }
    @RequestMapping("/all")
    public Result get(String id){
        try {
            Map<String, Object> all = service.all(id);
            return Result.OK(all);
        } catch (ResultException e) {
            return Result.build(404,e);
        }

    }
    //删除备注
    @RequestMapping("/del")
    public Result del(String tid){

            service.delete(tid);
            return Result.OK();

    }

}
