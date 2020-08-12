package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.util.PageResult;
import com.powernode.workbench.mapper.TblActivityMapper;
import com.powernode.workbench.mapper.TblActivityRemarkMapper;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblActivityExample;
import com.powernode.workbench.pojo.TblActivityRemarkExample;
import com.powernode.workbench.service.ActivitiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class ActivitiesServiceImpl implements ActivitiesService {
    @Autowired
    TblActivityMapper mapper;
    @Autowired
    TblUserMapper userMapper;
    @Autowired
    TblActivityRemarkMapper remarkMapper;
    @Override
    //添加客户
    public int add(TblActivity tblActivity, HttpServletRequest request) {
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        tblActivity.setCreateby(user.getName());
        
        if (tblActivity ==null){
            throw new ResultException("创建失败");
        }
        int i = mapper.insertSelective(tblActivity);
        if (i<=0){
            throw new ResultException("创建失败");
        }
        return i;
    }

    //查询所有的客户
    @Override
    public List<TblUser> getUser() {
        List<TblUser> tblUsers = userMapper.selectByExample(null);
        if (tblUsers==null || tblUsers.size() == 0){
            throw new ResultException("没有任何客户");
        }
        return tblUsers;
    }

    @Override
    public PageResult getList(String pageNum, String pageSize, String owner, String name, String startdate, String enddate) {
        int pn = Integer.parseInt(pageNum);
        int ps = Integer.parseInt(pageSize);

        TblActivityExample activityExample = new TblActivityExample();
        TblActivityExample.Criteria criteria = activityExample.createCriteria();
        if (owner !=null && !"".equals(owner)){
            TblUserExample userExample = new TblUserExample();
            TblUserExample.Criteria criteria2 = userExample.createCriteria();
            criteria2.andNameLike("%"+owner+"%");
            List<TblUser> tblUsers1 = userMapper.selectByExample(userExample);
            List<String> ids = new ArrayList<>();
            for (TblUser tblUser : tblUsers1) {
                ids.add(tblUser.getId());
            }
            criteria.andOwnerIn(ids);
        }


        if (name != null && !"".equals(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if (startdate != null && !"".equals(startdate)){
            criteria.andStartdateLike("%"+startdate+"%");
        }
        if (enddate != null && !"".equals(enddate)){
            criteria.andEnddateLike("%"+enddate+"%");
        }
        PageHelper.startPage(pn,ps);
        List<TblActivity> tblActivities = mapper.selectByExample(activityExample);
        if (tblActivities ==null || tblActivities.size()==0){
            throw new ResultException("查询为空");
        }

       for (TblActivity tblActivity : tblActivities) {
            String owner1 = tblActivity.getOwner();
            TblUser tblUser = userMapper.selectByPrimaryKey(owner1);
            String name1 = tblUser.getName();
            tblActivity.setOwner(name1);
        }
        PageInfo<TblActivity> PageInfo = new PageInfo<>(tblActivities);

        List<TblActivity> list = PageInfo.getList();
        long total = PageInfo.getTotal();
        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }
//删除数据
    @Override

    public void delete(String[] id) {
        if (id.length!=0 && id!=null) {
            for (int i = 0; i < id.length; i++) {
                TblActivityRemarkExample remarkExample = new TblActivityRemarkExample();
                TblActivityRemarkExample.Criteria criteria = remarkExample.createCriteria();
                criteria.andActivityidEqualTo(id[i]);
                remarkMapper.deleteByExample(remarkExample);
                mapper.deleteByPrimaryKey(id[i]);
            }
        }else {
            throw new ResultException("删除失败");
        }
    }
    //获得更新框里面的信息
    @Override
    public TblActivity update(String id) {
        if (id!=null && !"".equals(id)){
            TblActivity tblActivity = mapper.selectByPrimaryKey(id);
            return tblActivity;
        }else {
            throw new ResultException("查询失败");
        }

    }

    @Override
    public void update1(TblActivity tblActivity) {
        if (tblActivity!=null){
            mapper.updateByPrimaryKeySelective(tblActivity);
        }else {
            throw new ResultException("修改失败");
        }
    }

    @Override
    public TblActivity actSelect(String id) {
        if (id != null && !"".equals(id)){
            TblActivity activity = mapper.selectByPrimaryKey(id);
            return activity;
        }else {
            throw new ResultException("查询失败");
        }
    }


    //更新信息



    /*//自己写的删除方法
        @Override
        public void delect (String id) {
            if (id!=null && !"".equals(id)){
                int i = mapper.deleteByPrimaryKey(id);
                if (i<=0){
                    throw new ResultException("删除失败");
                }
            }else {
                throw new ResultException("删除失败");
            }
        }*/
   //显示需要的修改数据和所有者

}
