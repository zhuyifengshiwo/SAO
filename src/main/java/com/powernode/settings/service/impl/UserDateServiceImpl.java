package com.powernode.settings.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.settings.service.UserDateService;
import com.powernode.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserDateServiceImpl implements UserDateService {
    @Autowired
    private TblUserMapper tblUserMapper;
    @Override
    public TblUser selectUser(String name,String psw,String ip) {
        //判断账号密码是否为空
        if ((name == null ||"".equals(name)) ||( "".equals(psw) || psw ==null)){
            throw new ResultException("账号或者密码不能为空");
        }else {
        //查询数据
        TblUserExample userExample = new TblUserExample();
        TblUserExample.Criteria criteria = userExample.createCriteria();
        criteria.andLoginactEqualTo(name);
        criteria.andLoginpwdEqualTo(psw);
        List<TblUser> tblUsers = tblUserMapper.selectByExample(userExample);
        //判断是否查询到数据
        if (tblUsers == null||tblUsers.size()==0){
            throw new ResultException("登陆失败,账号或者密码错误");
        }
        //拿到当前查询到的对象
        TblUser tblUser = tblUsers.get(0);
        //判断ip地址是否受到限制
        if (!tblUser.getAllowips().contains(ip)){
            throw new ResultException("抱歉！您的ip受限");
        }
        //判断账号状态是否正常          0:正常 1:受限制
        if ("1".equals(tblUser.getLockstate())){
            throw new ResultException("当前账号已经被锁定");
        }
        //判断账号是否已经失效
        //获取系统当前时间
        String time = DateTimeUtil.getSysTime();
        //比较时间等于=0 小于是负数 大于是正数
        if(time.compareTo(tblUser.getExpiretime())> 0){
            throw new ResultException("抱歉！您的账号已到期");
        }
        //返回数据
        return tblUser;
    }
    }
}
