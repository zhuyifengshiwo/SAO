package com.powernode.workbench.service.impl;

import com.powernode.exception.ResultException;
import com.powernode.settings.mapper.TblUserMapper;
import com.powernode.settings.pojo.TblUser;
import com.powernode.util.DateTimeUtil;
import com.powernode.util.UUIDUtil;
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.TblClueActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Transactional
@Service
public class TblClueActivityRelationServiceImpl implements TblClueActivityRelationService {
    @Autowired
    TblClueActivityRelationMapper mapper;
    @Autowired
    TblActivityMapper activityMapper;
    @Autowired
    TblUserMapper userMapper;
    @Autowired
    TblClueMapper clueMapper;
    @Autowired
    TblCustomerMapper customerMapper;
    @Autowired
    TblContactsMapper contactsMapper;
    @Autowired
    TblClueRemarkMapper remarkMapper;
    @Autowired
    TblCustomerRemarkMapper customerRemarkMapper;
    @Autowired
    TblContactsRemarkMapper contactsRemarkMapper;
    @Autowired
    TblContactsActivityRelationMapper contactsActivityRelationMapper;
    @Autowired
    TblTranMapper tranMapper;
    @Autowired
    TblTranHistoryMapper historyMapper;





    @Override
    public void del(String id) {
        try {
            mapper.deleteByPrimaryKey(id);
        } catch (Exception e) {
            throw new ResultException("删除失败");
        }


    }

    //查询所有的活动信息
    @Override
    public List<Map<String, Object>> selectAll(String cid) {
        TblClueActivityRelationExample example = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria criteria = example.createCriteria();
        criteria.andClueidEqualTo(cid);
        List<TblClueActivityRelation> tblClueActivityRelations = mapper.selectByExample(example);
        List<Map<String, Object>> list = new ArrayList<>();

        if (tblClueActivityRelations != null && tblClueActivityRelations.size() != 0) {
            for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations) {
                Map<String, Object> map = new HashMap<>();
                String activityid = tblClueActivityRelation.getActivityid();
                TblActivity activity = activityMapper.selectByPrimaryKey(activityid);
                String owner = activity.getOwner();
                TblUser tblUser = userMapper.selectByPrimaryKey(owner);
                String name = tblUser.getName();
                activity.setOwner(name);
                String id = tblClueActivityRelation.getId();
                map.put("id", id);
                map.put("activity", activity);
                list.add(map);
            }

        }
        return list;
    }

    //查询所有的活动
    @Override
    public List<TblActivity> selectActivity() {
        List<TblActivity> list = new ArrayList<>();
        List<TblActivity> tblActivityList = activityMapper.selectByExample(null);
        if (tblActivityList.size() != 0 && tblActivityList != null) {
            for (TblActivity activity : tblActivityList) {
                String owner = activity.getOwner();
                TblUser tblUser = userMapper.selectByPrimaryKey(owner);
                String name = tblUser.getName();
                activity.setOwner(name);
                list.add(activity);
            }
        }else {
            throw new ResultException("无数据");
        }

        return list;

    }

    @Override
    public List<TblActivity> lookupActivity(String name) {
        List<TblActivity> list = new ArrayList<>();
        if (name != null && !"".equals(name)) {
            TblActivityExample activityExample = new TblActivityExample();
            TblActivityExample.Criteria criteria = activityExample.createCriteria();
            criteria.andNameLike("%" + name + "%");
            List<TblActivity> tblActivityList = activityMapper.selectByExample(activityExample);
            if (tblActivityList.size() != 0 && tblActivityList != null) {
                for (TblActivity activity : tblActivityList) {
                    String owner = activity.getOwner();
                    TblUser tblUser = userMapper.selectByPrimaryKey(owner);
                    String name1 = tblUser.getName();
                    activity.setOwner(name1);
                    list.add(activity);
                }
            }

        }else {
            throw new ResultException("查询失败");
        }
        return list;
    }
        //关联
    @Override
    public void connect(String[] its,String cid) {
        TblClueActivityRelation tblClueActivityRelation = new TblClueActivityRelation();

        if (its!=null && its.length != 0 && cid!=null && !"".equals(cid)) {
            for (String it : its) {
                tblClueActivityRelation.setId(UUIDUtil.getUUID());
                tblClueActivityRelation.setActivityid(it);
                tblClueActivityRelation.setClueid(cid);
                mapper.insert(tblClueActivityRelation);
            }
        }else {
            throw new ResultException("关联失败");
        }
    }
    //转换客户
    @Override
    public void add(String id,String check, String money,String name, String date, String aid, String creatBy, String stage) {
        if (id==null || "".equals(id)){
            throw new ResultException("转换失败");
        }
        String sysTime = DateTimeUtil.getSysTime();
        TblClue tblClue = clueMapper.selectByPrimaryKey(id);
        String company = tblClue.getCompany();
        TblCustomerExample customerExample = new TblCustomerExample();
        TblCustomerExample.Criteria criteria = customerExample.createCriteria();
        criteria.andNameEqualTo(company);
        List<TblCustomer> tblCustomers = customerMapper.selectByExample(customerExample);
        TblCustomer tblCustomer = new TblCustomer();
        //判断是否存在客户，如果存在直接取出来，不存在新建一个客户
        if (tblCustomers ==null || tblCustomers.size() == 0){
            tblCustomer.setId(UUIDUtil.getUUID());
            tblCustomer.setOwner(tblClue.getOwner());
            tblCustomer.setName(tblClue.getCompany());
            tblCustomer.setWebsite(tblClue.getWebsite());
            tblCustomer.setPhone(tblClue.getPhone());
            tblCustomer.setCreateby(creatBy);
            tblCustomer.setCreatetime(sysTime);
            tblCustomer.setContactsummary(tblClue.getContactsummary());
            tblCustomer.setNextcontacttime(tblClue.getNextcontacttime());
            tblCustomer.setDescription(tblClue.getDescription());
            tblCustomer.setAddress(tblClue.getAddress());
            customerMapper.insertSelective(tblCustomer);
        }else {
             tblCustomer = tblCustomers.get(0);
        }
        //创建联系人信息
        TblContacts tblContacts = new TblContacts();
        tblContacts.setId(UUIDUtil.getUUID());
        tblContacts.setOwner(tblCustomer.getOwner());
        tblContacts.setSource(tblClue.getSource());
        tblContacts.setCustomerid(tblCustomer.getId());
        tblContacts.setFullname(tblClue.getFullname());
        tblContacts.setAppellation(tblClue.getAppellation());
        tblContacts.setEmail(tblClue.getEmail());
        tblContacts.setMphone(tblClue.getMphone());
        tblContacts.setJob(tblClue.getJob());
        tblContacts.setCreateby(creatBy);
        tblContacts.setCreatetime(sysTime);
        tblContacts.setDescription(tblClue.getDescription());
        tblContacts.setContactsummary(tblClue.getContactsummary());
        tblContacts.setNextcontacttime(tblClue.getNextcontacttime());
        tblContacts.setAddress(tblClue.getAddress());
        contactsMapper.insertSelective(tblContacts);

        TblClueRemarkExample tblClueRemarkExample = new TblClueRemarkExample();
        TblClueRemarkExample.Criteria clueRemarkExampleCriteria = tblClueRemarkExample.createCriteria();
        clueRemarkExampleCriteria.andClueidEqualTo(id);
        List<TblClueRemark> tblClueRemarks = remarkMapper.selectByExample(tblClueRemarkExample);
        //转换备注
        if (tblClueRemarks!=null ){
            for (TblClueRemark tblClueRemark : tblClueRemarks) {
                TblCustomerRemark tblCustomerRemark = new TblCustomerRemark();
                tblCustomerRemark.setCreateby(creatBy);
                tblCustomerRemark.setCreatetime(sysTime);
                tblCustomerRemark.setCustomerid(tblCustomer.getId());
                tblCustomerRemark.setId(UUIDUtil.getUUID());
                tblCustomerRemark.setNotecontent(tblClueRemark.getNotecontent());
                //0新建 1修改过
                tblCustomerRemark.setEditflag("0");
                customerRemarkMapper.insertSelective(tblCustomerRemark);


                TblContactsRemark contactsRemark = new TblContactsRemark();
                contactsRemark.setContactsid(tblContacts.getId());
                contactsRemark.setCreateby(creatBy);
                contactsRemark.setCreatetime(sysTime);
                //0新建 1修改过
                contactsRemark.setEditflag("0");
                contactsRemark.setId(UUIDUtil.getUUID());
                contactsRemark.setNotecontent(tblClueRemark.getNotecontent());
                contactsRemarkMapper.insertSelective(contactsRemark);
            }
        }
        //联系人市场活动
        TblClueActivityRelationExample tblClueActivityRelationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria criteria1 = tblClueActivityRelationExample.createCriteria();
        criteria1.andClueidEqualTo(id);
        List<TblClueActivityRelation> tblClueActivityRelations = mapper.selectByExample(tblClueActivityRelationExample);
        if (tblClueActivityRelations!=null && tblClueActivityRelations.size()!=0){
            for (TblClueActivityRelation tblClueActivityRelation : tblClueActivityRelations) {
                TblContactsActivityRelation relation = new TblContactsActivityRelation();
                relation.setId(UUIDUtil.getUUID());
                relation.setActivityid(tblClueActivityRelation.getActivityid());
                relation.setContactsid(tblContacts.getId());
                contactsActivityRelationMapper.insertSelective(relation);
            }
        }

        boolean flag = Boolean.parseBoolean(check);
        TblTran tblTran = new TblTran();
        if (flag){
            if ( money!=null && name!=null&& date!=null&& stage!=null){
                tblTran.setActivityid(aid);
                tblTran.setContactsid(tblContacts.getId());
                tblTran.setCustomerid(tblCustomer.getId());
                tblTran.setId(UUIDUtil.getUUID());
                tblTran.setContactsummary(tblContacts.getContactsummary());
                tblTran.setCreateby(creatBy);
                tblTran.setCreatetime(sysTime);
                tblTran.setMoney(money);
                tblTran.setStage(stage);
                tblTran.setSource(tblClue.getSource());
                tblTran.setNextcontacttime(tblClue.getNextcontacttime());
                tblTran.setExpecteddate(date);
                tblTran.setOwner(tblClue.getOwner());
                tblTran.setName(name);
                tblTran.setDescription(tblClue.getDescription());
                tranMapper.insertSelective(tblTran);
            }
        }else {
            throw new ResultException("未提交交易");
        }
        TblTranHistory tblTranHistory = new TblTranHistory();
        tblTranHistory.setId(UUIDUtil.getUUID());
        tblTranHistory.setTranid(tblTran.getId());
        tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
        tblTranHistory.setMoney(money);
        tblTranHistory.setCreateby(creatBy);
        tblTranHistory.setStage(tblTran.getStage());
        tblTranHistory.setCreatetime(sysTime);
        historyMapper.insertSelective(tblTranHistory);


        TblClueRemarkExample remarkExample = new TblClueRemarkExample();
        TblClueRemarkExample.Criteria remarkExampleCriteria = remarkExample.createCriteria();
        remarkExampleCriteria.andClueidEqualTo(id);
        remarkMapper.deleteByExample(remarkExample);

        TblClueActivityRelationExample relationExample = new TblClueActivityRelationExample();
        TblClueActivityRelationExample.Criteria relationExampleCriteria = relationExample.createCriteria();
        relationExampleCriteria.andClueidEqualTo(id);
        mapper.deleteByExample(relationExample);

        clueMapper.deleteByPrimaryKey(id);


    }

}
