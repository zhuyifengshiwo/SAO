package com.powernode.settings.service;

import com.powernode.settings.pojo.TblUser;

public interface UserDateService {
    public TblUser selectUser(String name,String psw,String ip);
}
