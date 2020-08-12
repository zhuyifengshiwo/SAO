package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblContacts;
import com.powernode.workbench.pojo.TblTran;
import com.powernode.workbench.pojo.TblTranHistory;
import com.powernode.workbench.service.CuleTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("culetran")
public class CuleTranController {
    @Autowired
    CuleTranService service;
    //获取下拉选框的值
    @RequestMapping("/owner")
    public Result getList(HttpServletRequest request){
       Map map = (Map) request.getSession().getServletContext().getAttribute("map");
       List<String> list = service.geyList();
        map.put("owner",list);
        return Result.OK(map);
    }
    //获取可能性
    @RequestMapping("/possibility")
    public Result getPossibility(HttpServletRequest request,String stage){
        Map map2 = (Map) request.getSession().getServletContext().getAttribute("map2");
        String s = service.get(stage, map2);
        return Result.OK(s);
    }
    //客户名称自动补全模糊搜索
    @RequestMapping("/query")
    public Result getQyery(String name){
        Map<String, Object> query = service.getQuery(name);
        return Result.OK(query);
    }
    //获取所有的市场活动
    @RequestMapping("/active")
    public Result getActive(String name){
        List<TblActivity> act = service.getAct(name);
        return Result.OK(act);
    }
    //获取所有的联系人
    @RequestMapping("/contacts")
    public Result getContacts(String name){
        List<TblContacts> contacts = service.getContacts(name);
        return Result.OK(contacts);
    }
    //保存新建交易
    @RequestMapping(value = "/save",method = RequestMethod.POST)
    public Result save(TblTran tblTran,HttpServletRequest request){
        TblUser user = (TblUser) request.getSession().getAttribute("USER");
        String name = user.getName();
        service.add(tblTran,name);
        return Result.OK();
    }
    //分页查询
    @RequestMapping("/page")
    public Result getList(@RequestParam(required = true,defaultValue ="1") String pageNum,
                          @RequestParam(required = true,defaultValue ="1") String pageSize,
                          @RequestParam(required = false) String name,
                          @RequestParam(required = false) String fullname,
                          @RequestParam(required = false) String stage,
                          @RequestParam(required = false) String transactionType,
                          @RequestParam(required = false) String owner,
                          @RequestParam(required = false) String source,
                          @RequestParam(required = false) String lname
    ){

        PageResult list = service.getList(pageNum, pageSize, name, fullname, stage, transactionType, owner, source, lname);
        return Result.OK(list);

    }
    //修改页面
    @RequestMapping("/edit")
    public Result edit(HttpServletRequest request,String id){
        Map map = (Map)request.getSession().getServletContext().getAttribute("map");
        Map map2 = (Map)request.getSession().getServletContext().getAttribute("map2");
        Map<String, Object> editpage = service.editpage(id,map2);
        editpage.put("type",map);
        return Result.OK(editpage);
    }
    //修改交易信息
    @RequestMapping("/update")
    public Result update(TblTran tblTran,HttpServletRequest request){
        TblUser user = (TblUser) request.getSession().getAttribute("USER");
        String editBy = user.getName();
        service.update(tblTran,editBy);
        return Result.OK();
    }
    //获取交易信息
    @RequestMapping("/tran")
    public Result getTran(String id){
        TblTran tran = service.getTran(id);
        return Result.OK(tran);
    }
    //删除交易信息
    @RequestMapping("/del")
    public Result del(@RequestParam("its[]") String[] its){
        service.del(its);
        return Result.OK();
    }
    //点击交易页面
    @RequestMapping("/id")
    public Result getTranById(String id,HttpServletRequest request){
        ServletContext servletContext = request.getSession().getServletContext();
        Map map = (Map) servletContext.getAttribute("map2");
        TblTran byId = service.getById(id);
        byId.setPoss((String) map.get(byId.getStage()));
        return Result.OK(byId);
    }
    //锚点点击
    @RequestMapping("/stagelogo")
    public Result point(HttpServletRequest request,String id){
        Map value = (Map) request.getSession().getServletContext().getAttribute("map");
        Map possibility = (Map)request.getSession().getServletContext().getAttribute("map2");
        Map<String, Object> possibility1 = service.possibility(value, possibility, id);
        return Result.OK(possibility1);
    }
    @RequestMapping("/addhistory")
    public Result addTranHistory(TblTranHistory tblTranHistory, HttpServletRequest request){
        String createby = ((TblUser)request.getSession().getAttribute("USER")).getName();
        tblTranHistory.setCreateby(createby);
        TblTran t = service.addTranHistory(tblTranHistory);
        Map map = (Map) request.getSession().getServletContext().getAttribute("map2");
        t.setPoss((String) map.get(t.getStage()));
        return  Result.OK(t);
    }

}
