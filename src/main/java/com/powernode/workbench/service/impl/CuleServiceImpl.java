package com.powernode.workbench.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.settings.pojo.TblUserExample;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.MyComparator;
import com.powernode.util.PageResult;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.CuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("type")
public class CuleServiceImpl implements CuleService {
    @Autowired
    private TblClueMapper clueMapper;
    @Autowired
    private TblDicTypeMapper dicTypeMapper;
    @Autowired
    private TblDicValueMapper valueMapper;
    @Autowired
    private TblUserMapper userMapper;
    @Autowired
    TblClueRemarkMapper clueRemarkMapper;
    @Autowired
    TblClueActivityRelationMapper relationMapper;
    @Autowired
    TblActivityMapper activityMapper;
    //查询所有的下拉选数据
    @Override
    public Map getCule() {
        HashMap map = new HashMap();
        List<TblDicType> tblDicTypes = dicTypeMapper.selectByExample(null);
        if (tblDicTypes!=null &&tblDicTypes.size()!=0) {
            for (TblDicType tblDicType : tblDicTypes) {
                String code = tblDicType.getCode();
                TblDicValueExample valueExample = new TblDicValueExample();
                TblDicValueExample.Criteria valueExampleCriteria = valueExample.createCriteria();
                valueExampleCriteria.andTypecodeEqualTo(code);
                List<TblDicValue> tblDicValues = valueMapper.selectByExample(valueExample);
                if (tblDicValues!=null && tblDicValues.size()!=0){
                    MyComparator comparator = new MyComparator();
                    Set<TblDicValue> tblDicValues1 = new TreeSet<>(comparator);
                    tblDicValues1.addAll(tblDicValues);
                    map.put(code, tblDicValues1);
                }else {
                    throw new ResultException("未找到字典数据");
                }
            }
        }else {
            throw new ResultException("未找到字典类型");
        }
        return map;
    }
    //分页查询
    @Override
    public PageResult pageList(String pageNum, String pageSize,String fullname,String company, String phone, String source, String owner, String mphone, String state) {
        int pn = Integer.parseInt(pageNum);
        int ps = Integer.parseInt(pageSize);

        TblClueExample clueExample = new TblClueExample();
        TblClueExample.Criteria criteria = clueExample.createCriteria();
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
        if (fullname != null && !"".equals(fullname)){
            criteria.andFullnameLike("%"+fullname+"%");
            criteria.andAppellationLike("%"+fullname+"%");
        }
        if (company != null && !"".equals(company)){
            criteria.andCompanyLike("%"+company+"%");
        }
        if (source != null && !"".equals(source)){
            criteria.andSourceLike("%"+source+"%");
        }
        if (phone != null && !"".equals(phone)){
            criteria.andPhoneLike("%"+phone+"%");
        }
        if (mphone != null && !"".equals(mphone)){
            criteria.andMphoneLike("%"+mphone+"%");
        }
        if (state != null && !"".equals(state)){
            criteria.andStateLike("%"+state+"%");
        }
        PageHelper.startPage(pn,ps);
        List<TblClue> tblClues = clueMapper.selectByExample(clueExample);
        if (tblClues ==null || tblClues.size()==0){
            throw new ResultException("查询为空");
        }
        for (TblClue tblClue : tblClues) {
            String owner1 = tblClue.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria1 = tblUserExample.createCriteria();
            criteria1.andIdEqualTo(owner1);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String name1 = tblUsers.get(0).getName();
            tblClue.setOwner(name1);
        }

        PageInfo<TblClue> PageInfo = new PageInfo<>(tblClues);
        List<TblClue> list = PageInfo.getList();
        long total = PageInfo.getTotal();
        PageResult pageResult = new PageResult(total,list);
        return pageResult;
    }

    @Override
    public void addCule(TblClue clue) {
        String uuid = UUIDUtil.getUUID();
        if (clue!=null){
            clue.setId(uuid);
            clue.setCreatetime(DateTimeUtil.getSysTime());
           /* String owner = clue.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria = tblUserExample.createCriteria();
            criteria.andNameEqualTo(owner);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            if (tblUsers!=null && tblUsers.size()!=0){
                String id = tblUsers.get(0).getId();
                clue.setOwner(id);
            }*/
            try {
                clueMapper.insertSelective(clue);
            } catch (Exception e) {
                throw new ResultException("添加失败");
            }

        }
        throw new ResultException("添加失败");
    }


    //删除
    @Override
    public void del(String[] id) {
        if (id.length!=0 && id!=null) {
            for (int i = 0; i < id.length; i++) {
                TblClueRemarkExample tblClueRemarkExample = new TblClueRemarkExample();
                TblClueRemarkExample.Criteria criteria = tblClueRemarkExample.createCriteria();
                criteria.andClueidEqualTo(id[i]);
                clueRemarkMapper.deleteByExample(tblClueRemarkExample);
                clueMapper.deleteByPrimaryKey(id[i]);
            }
        }else {
            throw new ResultException("删除失败");
        }
    }
    //查询线索
    @Override
    public TblClue select(String id) {
        if (id!=null && !"".equals(id)){
            TblClue tblClue = clueMapper.selectByPrimaryKey(id);
            return tblClue;
        }else{
            throw new ResultException("查询为空");
        }
    }

    @Override
    public List<TblActivity> get(String cid,String name) {
        List<TblActivity> list =new ArrayList<>();
        List<TblActivity> list2 =new ArrayList<>();
        if (cid!=null && !"".equals(cid)) {
            TblClueActivityRelationExample example = new TblClueActivityRelationExample();
            TblClueActivityRelationExample.Criteria criteria = example.createCriteria();
            criteria.andClueidEqualTo(cid);
            List<TblClueActivityRelation> relations = relationMapper.selectByExample(example);
            if (relations!=null && relations.size()!=0){
                for (TblClueActivityRelation relation : relations) {
                    String activityid = relation.getActivityid();
                    TblActivity activity = activityMapper.selectByPrimaryKey(activityid);
                    if (activity!=null){
                        String owner = activity.getOwner();
                        TblUser tblUser = userMapper.selectByPrimaryKey(owner);
                        if (tblUser!=null){
                            String name1 = tblUser.getName();
                            activity.setOwner(name1);
                            list.add(activity);
                        }
                    }
                }
            }
        }
        if (name!=null && !"".equals(name)) {
            for (TblActivity activity : list) {
                if (activity.getName().contains(name)) {
                    list2.add(activity);
                }
            }
            return list2;
        }
        return list;

    }
    //修改线索
    @Override
    public void update(TblClue clue,String editBy) {
        if (clue!=null) {
            clue.setEditby(editBy);
            clue.setEdittime(DateTimeUtil.getSysTime());
            String owner = clue.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria = tblUserExample.createCriteria();
            criteria.andNameEqualTo(owner);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            if (tblUsers!=null && tblUsers.size()!=0){
                String id = tblUsers.get(0).getId();
                clue.setOwner(id);
            }
            try {
                clueMapper.updateByPrimaryKeySelective(clue);
            } catch (Exception e) {
                new ResultException("修改失败");
            }
        }

    }
}
