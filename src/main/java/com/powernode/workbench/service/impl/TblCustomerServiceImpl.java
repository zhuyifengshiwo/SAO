package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.PageResult;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.mapper.TblCustomerMapper;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblActivityExample;
import com.powernode.workbench.pojo.TblCustomer;
import com.powernode.workbench.pojo.TblCustomerExample;
import com.powernode.workbench.service.TblCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblCustomerServiceImpl implements TblCustomerService {
    @Autowired
    private TblUserMapper userMapper;
    @Autowired
    private TblCustomerMapper mapper;
    //分页查询
    @Override
    public PageResult getList(int pageNum, int pageSize, String owner, String name, String phone, String web) {
        TblCustomerExample tblCustomerExample = new TblCustomerExample();
        TblCustomerExample.Criteria criteria = tblCustomerExample.createCriteria();
        if (owner !=null && !"".equals(owner)){
            TblUserExample userExample = new TblUserExample();
            TblUserExample.Criteria criteria2 = userExample.createCriteria();
            criteria2.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers1 = userMapper.selectByExample(userExample);
            for (TblUser tblUser : tblUsers1) {
                String id = tblUser.getId();
                criteria.andOwnerLike("%"+id+"%");

            }
        }


        if (name != null && !"".equals(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if (phone != null && !"".equals(phone)){
            criteria.andPhoneLike("%"+phone+"%");
        }
        if (web != null && !"".equals(web)){
            criteria.andWebsiteLike("%"+web+"%");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<TblCustomer> tblCustomers = mapper.selectByExample(tblCustomerExample);
        if (tblCustomers ==null || tblCustomers.size()==0){
            throw new ResultException("查询为空");
        }

        for (TblCustomer tblCustomer : tblCustomers) {
            String owner1 = tblCustomer.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria1 = tblUserExample.createCriteria();
            criteria1.andIdEqualTo(owner1);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String name1 = tblUsers.get(0).getName();
            tblCustomer.setOwner(name1);
        }
        PageInfo<TblCustomer> PageInfo = new PageInfo<>(tblCustomers);
        List<TblCustomer> list = PageInfo.getList();
        long total = PageInfo.getTotal();
        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }
    //添加客户
    @Override
    public void add(TblCustomer customer) {
        if (customer!=null){
            String uuid = UUIDUtil.getUUID();
            String sysTime = DateTimeUtil.getSysTime();
            customer.setId(uuid);
            customer.setCreatetime(sysTime);
            String owner = customer.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            tblUserExample.createCriteria().andNameEqualTo(owner);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            if (tblUsers!=null && tblUsers.size()!=0){
                String id = tblUsers.get(0).getId();
                customer.setOwner(id);
            }
            try {
                mapper.insertSelective(customer);
            } catch (Exception e) {
                throw new ResultException("创建失败");
            }
        }


    }
}
