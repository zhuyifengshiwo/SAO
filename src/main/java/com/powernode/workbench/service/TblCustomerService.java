package com.powernode.workbench.service;

import com.powernode.util.PageResult;
import com.powernode.workbench.pojo.TblCustomer;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface TblCustomerService {
    //分页查询
    PageResult getList(int pageNum, int pageSize, String owner, String name, String phone, String web);
    //c创建客户
    void add(TblCustomer customer);
}
