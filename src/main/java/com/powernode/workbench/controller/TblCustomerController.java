package com.powernode.workbench.controller;

import com.powernode.settings.pojo.TblUser;
import com.powernode.util.PageResult;
import com.powernode.util.Result;
import com.powernode.workbench.pojo.TblCustomer;
import com.powernode.workbench.service.TblCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("customer")
public class TblCustomerController {
    //分页查询
    @Autowired
    private TblCustomerService service;
    @RequestMapping("/get")
    public Result get(@RequestParam(value = "pageNum",required = true,defaultValue = "1") int pageNum,
                      @RequestParam(value = "pageSize",required = true) int pageSize,
                      @RequestParam(required =false) String owner,
                      @RequestParam(required =false) String name,
                      @RequestParam(required =false) String phone,
                      @RequestParam(required =false) String web

    ){
        PageResult list = service.getList(pageNum, pageSize, owner, name, phone, web);
        return Result.OK(list);
    }
    //添加客户
    @RequestMapping("/add")
    public Result addCustom(TblCustomer customer,HttpServletRequest request){
        TblUser user = (TblUser) request.getSession().getAttribute("USER");
        String name = user.getName();
        customer.setCreateby(name);
        service.add(customer);
        return Result.OK();

    }

}
