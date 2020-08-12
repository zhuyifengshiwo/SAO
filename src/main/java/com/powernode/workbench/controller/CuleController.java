package com.powernode.workbench.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClue;
import com.powernode.workbench.service.CuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("cule")
public class CuleController {
    @Autowired
    CuleService culeService;
    //返回类型
    @RequestMapping("/data")
    public Result culetype(HttpServletRequest request){
        Object map = request.getSession().getServletContext().getAttribute("map");
        return Result.OK(map);
    }
    //分页查询
   @RequestMapping("/massage")
    public Result page(@RequestParam(required = true,defaultValue = "1") String pageNum,
                       @RequestParam(required = true,defaultValue = "1") String pageSize,
                       @RequestParam(required =false)String fullname,
                       @RequestParam(required =false)String company,
                       @RequestParam(required =false)String phone,
                       @RequestParam(required =false)String source,
                       @RequestParam(required =false)String owner,
                       @RequestParam(required =false)String mphone, String state){
       try {
           PageResult pageResult = culeService.pageList(pageNum, pageSize, fullname, company, phone, source, owner, mphone, state);
           return Result.OK(pageResult);
       } catch (ResultException e) {
           return Result.build(404,e);
       }
   }
    //添加
   @RequestMapping("/add")
    public Result add(TblClue clue,HttpServletRequest request){
       TblUser user = (TblUser)request.getSession().getAttribute("USER");
       String name = user.getName();
       clue.setCreateby(name);
       try {
           culeService.addCule(clue);
           return Result.OK();
       } catch (ResultException e) {
           return Result.build(404,e);
       }
   }
   //删除
   @RequestMapping("/del")
    public Result del(@RequestParam("its[]") String[] ids){
       try {
           culeService.del(ids);
           return Result.OK();
       } catch (ResultException e) {
           return Result.build(404,e);
       }
   }
   //查询线索
    @RequestMapping("/select")
    public Result getCule(@RequestParam String id){
        try {
            TblClue select = culeService.select(id);
            return Result.OK(select);
        } catch (ResultException e) {
            return Result.build(404,e);
        }
    }
    //查询线索绑定的活动的信息
    @RequestMapping("/active")
    public Result getActive(@RequestParam String id, @RequestParam(required =false) String name){
        List<TblActivity> list = culeService.get(id,name);
        return Result.OK(list);
    }
    @RequestMapping("/update")
    public Result update(HttpServletRequest request,TblClue clue){
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        String editBy = user.getName();
        culeService.update(clue,editBy);
        return Result.OK();
    }
}
