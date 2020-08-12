package com.powernode.workbench.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.mapper.TblClueMapper;
import com.powernode.workbench.mapper.TblClueRemarkMapper;
import com.powernode.workbench.pojo.TblActivityRemark;
import com.powernode.workbench.pojo.TblClue;
import com.powernode.workbench.pojo.TblClueRemark;
import com.powernode.workbench.pojo.TblClueRemarkExample;
import com.powernode.workbench.service.TblClueRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TblClueRemarkServiceImpl implements TblClueRemarkService {
    @Autowired
    private TblClueRemarkMapper mapper;
    @Autowired
    private TblClueMapper clueMapper;
    //提交备注
    @Override
    public TblClueRemark add(String createBy, String aid, String content) {
        String uuid = UUIDUtil.getUUID();
        TblClueRemark tblClueRemark = new TblClueRemark();
        tblClueRemark.setCreateby(createBy);
        tblClueRemark.setClueid(aid);
        tblClueRemark.setNotecontent(content);
        tblClueRemark.setId(uuid);
        tblClueRemark.setCreatetime(DateTimeUtil.getSysTime());
        tblClueRemark.setEditflag("0");
        try {
            int insert = mapper.insert(tblClueRemark);
        } catch (ResultException e) {
            throw new ResultException("添加失败");
        }
        TblClueRemark tblClueRemark1 = mapper.selectByPrimaryKey(uuid);
        return tblClueRemark1;

    }
//查询所有备注
    @Override
    public Map<String,Object> all(String id) {
        /*List<TblClueRemark> list =new ArrayList<>();*/
        Map<String,Object> map = new HashMap<>();
        if (id!=null && !"".equals(id)) {
            TblClueRemarkExample example = new TblClueRemarkExample();
            TblClueRemarkExample.Criteria criteria = example.createCriteria();
            criteria.andClueidEqualTo(id);
            List<TblClueRemark> tblClueRemarks = mapper.selectByExample(example);
            map.put("remark",tblClueRemarks);
            TblClue tblClue = clueMapper.selectByPrimaryKey(id);
            map.put("cule",tblClue);
            return map;
        }else {
            throw new ResultException("没有备注");
        }
    }
    //
    @Override
    public void delete(String id) {
        try {
            mapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new ResultException("删除失败");
        }
    }
}
