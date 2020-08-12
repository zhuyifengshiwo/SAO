package com.powernode.workbench.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.mapper.TblActivityRemarkMapper;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.pojo.TblActivityRemarkExample;
import com.powernode.workbench.service.TblActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblActivityRemarkServiceImpl implements TblActivityRemarkService {
    @Autowired
    private TblActivityRemarkMapper mapper;
    //提交备注
    @Override
    public TblActivityRemark add(String createBy,String aid,String content) {
        String uuid = UUIDUtil.getUUID();
        TblActivityRemark tblActivityRemark = new TblActivityRemark();
        tblActivityRemark.setCreateby(createBy);
        tblActivityRemark.setActivityid(aid);
        tblActivityRemark.setNotecontent(content);
        tblActivityRemark.setId(uuid);
        tblActivityRemark.setCreatetime(DateTimeUtil.getSysTime());
        tblActivityRemark.setEditflag("0");
        try {
            int insert = mapper.insert(tblActivityRemark);
        } catch (ResultException e) {
            throw new ResultException("添加失败");
        }
        TblActivityRemark remark = mapper.selectByPrimaryKey(uuid);
        return remark;

    }
    //查询所有
    @Override
    public List<TblActivityRemark> selectAll(String aid) {
        TblActivityRemarkExample remarkExample = new TblActivityRemarkExample();
        TblActivityRemarkExample.Criteria criteria = remarkExample.createCriteria();
        criteria.andActivityidEqualTo(aid);
        try {
            List<TblActivityRemark> tblActivityRemarks = mapper.selectByExample(remarkExample);
            return tblActivityRemarks;
        } catch (Exception e) {
            throw new ResultException("查询为空");
        }
    }
    //删除备注
    @Override
    public void delete(String id) {
        try {
            mapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new ResultException("删除失败");
        }
    }
      //修改数据
    @Override
    public TblActivityRemark update(String notecontent, String id) {
        TblActivityRemark remark = new TblActivityRemark();
        remark.setId(id);
        remark.setNotecontent(notecontent);
        try {
            int i = mapper.updateByPrimaryKeySelective(remark);
            TblActivityRemark remark1 = mapper.selectByPrimaryKey(id);
            return remark1;
        } catch (Exception e) {
            throw new ResultException("修改失败");
        }
    }
}
