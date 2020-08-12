package com.powernode.settings.controller;

import com.powernode.exception.ResultException;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.service.UserDateService;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.MD5Util;
import com.powernode.util.Result;
import com.powernode.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class UserLoginController {
    @Autowired
    private UserDateService dateService;
    @Value("${session}")
    private String SESSION_USER;
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Result login(@RequestParam("name") String name,@RequestParam("password") String password, HttpServletRequest request){
            //获取ip地址,ip地址需要请求栏不能是localhost
        String remoteAddr = request.getRemoteAddr();

        if (password != ""){
            password=MD5Util.getMD5(password);
        }
            //处理异常
        try {
            //拿到对象并且进行加密
            TblUser tblUser = dateService.selectUser(name, password, remoteAddr);
            //拿到对象放入sassion域当中，用作免登录验证
            request.getSession().setAttribute(SESSION_USER,tblUser);

            //返回参数成功200，已经登陆成功
            return Result.OK();
        }catch (ResultException e){
            return Result.build(400,e);
        }
    }
    //获取存在session域里面的user对象
    @RequestMapping("/name")
    public Result getName(HttpServletRequest request){
        TblUser tblUser = (TblUser) request.getSession().getAttribute(SESSION_USER);
        return Result.OK(tblUser);
    }


}
