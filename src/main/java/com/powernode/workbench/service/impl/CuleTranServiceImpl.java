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
import com.powernode.workbench.mapper.*;
import com.powernode.workbench.pojo.*;
import com.powernode.workbench.service.CuleTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class CuleTranServiceImpl implements CuleTranService {
    @Autowired
    TblUserMapper userMapper;
    @Autowired
    TblCustomerMapper customerMapper;
    @Autowired
    TblActivityMapper activityMapper;
    @Autowired
    TblContactsMapper contactsMapper;
    @Autowired
    TblTranMapper tranMapper;
    @Autowired
    TblTranHistoryMapper historyMapper;

    @Override

    //获取所有者
    public List<String> geyList() {
        List<TblUser> tblUsers = userMapper.selectByExample(null);
        List<String> list = new ArrayList<>();
        if (tblUsers!=null && tblUsers.size()!=0){
            for (TblUser tblUser : tblUsers) {
                String name = tblUser.getName();
                list.add(name);
            }
            return list;
        }else {
            throw new ResultException("所有者数据为空");
        }
    }
//查询阶段可能性
    @Override
    public String get(String stage, Map map) {
        boolean b = map.containsKey(stage);
        if (b){
            String o = (String) map.get(stage);
            return o;
        }else {
            throw new ResultException("当前阶段不存在");
        }

    }


    //获取自动补全模糊搜索
    @Override
    public Map<String, Object> getQuery(String name) {
        Map<String, Object> map = new HashMap<>();
        List<String> list =new ArrayList<>();
        Map<String, Object> map2 = new HashMap<>();
        if (name!=null && !"".equals(name)) {
            TblCustomerExample tblCustomerExample = new TblCustomerExample();
            TblCustomerExample.Criteria criteria = tblCustomerExample.createCriteria();
            criteria.andNameLike("%" + name + "%");
            List<TblCustomer> tblCustomers = customerMapper.selectByExample(tblCustomerExample);
            if (tblCustomers != null && tblCustomers.size() != 0) {
                for (TblCustomer tblCustomer : tblCustomers) {
                    String id = tblCustomer.getId();
                    String name1 = tblCustomer.getName();
                    map2.put(name1,id);
                    list.add(name1);
                    map.put("name",list);
                    map.put("id",map2);
                }

            }
        }else {
            throw new ResultException("名称为空");
        }
        return map;
    }
    //货的活动列表
    @Override
    public List<TblActivity> getAct(String name) {
        List<TblActivity> list =new ArrayList<>();
        if (name==null || "".equals(name)) {
            try {
                List<TblActivity> list1 = activityMapper.selectByExample(null);
                for (TblActivity activity : list1) {
                    String owner = activity.getOwner();
                    String name1 = userMapper.selectByPrimaryKey(owner).getName();
                    activity.setOwner(name1);
                    list.add(activity);
                }
                return list;
            } catch (Exception e) {
                throw new ResultException("市场活动为空");
            }
        }else{
            TblActivityExample tblActivityExample = new TblActivityExample();
            TblActivityExample.Criteria criteria = tblActivityExample.createCriteria();
            criteria.andNameLike("%"+name+"%");
            List<TblActivity> list1 = activityMapper.selectByExample(tblActivityExample);
            for (TblActivity activity : list1) {
                String owner = activity.getOwner();
                String name1 = userMapper.selectByPrimaryKey(owner).getName();
                activity.setOwner(name1);
                list.add(activity);
            }
            return list;
        }

    }
    //货的所有的联系人
    @Override
    public List<TblContacts> getContacts(String name) {
        if (name==null || "".equals(name)){
            List<TblContacts> tblContacts = contactsMapper.selectByExample(null);
            if (tblContacts==null || tblContacts.size()==0){
                throw new ResultException("查询为空");
            }else {
                return tblContacts;
            }

        }else {
            TblContactsExample tblContactsExample = new TblContactsExample();
            TblContactsExample.Criteria criteria = tblContactsExample.createCriteria();
            criteria.andFullnameLike("%"+name+"%");

            List<TblContacts> tblContacts = contactsMapper.selectByExample(tblContactsExample);
            if (tblContacts==null || tblContacts.size()==0){
                throw new ResultException("查询为空");
            }else {
                return tblContacts;
            }
        }
    }
    //添加交易
    @Override
    public void add(TblTran tblTran,String creatBy) {
        String sysTime = DateTimeUtil.getSysTime();
        tblTran.setId(UUIDUtil.getUUID());
        tblTran.setCreatetime(sysTime);
        tblTran.setCreateby(creatBy);
        if (tblTran.getOwner()==null){
            throw new ResultException("所有者不能为空");
        }else {
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria = tblUserExample.createCriteria();
            criteria.andNameEqualTo(tblTran.getOwner());
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String id = tblUsers.get(0).getId();
            tblTran.setOwner(id);
        }
        if (tblTran.getName()==null) {
            throw new ResultException("交易名称不能为空");
        }
        if (tblTran.getExpecteddate()==null) {
            throw new ResultException("日期不能为空");
        }
        if (tblTran.getCustomerid()==null) {
            throw new ResultException("客户不能为空");
        }
        if (tblTran.getStage()==null) {
            throw new ResultException("阶段不能为空");
        }
        try {
            tranMapper.insertSelective(tblTran);
            TblTranHistory tblTranHistory = new TblTranHistory();
            tblTranHistory.setCreatetime(sysTime);
            tblTranHistory.setStage(tblTran.getStage());
            tblTranHistory.setCreateby(creatBy);
            tblTranHistory.setMoney(tblTran.getMoney());
            tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
            tblTranHistory.setTranid(tblTran.getId());
            tblTranHistory.setId(UUIDUtil.getUUID());
            historyMapper.insertSelective(tblTranHistory);
        } catch (Exception e) {
            throw new ResultException("创建失败");
        }


    }
    //分页查询
    public PageResult getList(String pageNum,
                                 String pageSize,
                                 String name,
                                 String fullname,
                                 String stage,
                                 String transactionType,
                                 String owner,
                                 String source,
                                 String lname               ){
        int pn = Integer.parseInt(pageNum);
        int ps = Integer.parseInt(pageSize);
        PageHelper.startPage(pn,ps);
        TblTranExample tranExample = new TblTranExample();
        TblTranExample.Criteria criteria = tranExample.createCriteria();
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
            List list =new ArrayList();
            TblCustomerExample customerExample = new TblCustomerExample();
            TblCustomerExample.Criteria exampleCriteria = customerExample.createCriteria();
            exampleCriteria.andNameLike("%"+fullname+"%");
            List<TblCustomer> tblCustomers = customerMapper.selectByExample(customerExample);
            if (tblCustomers.size()!=0 && tblCustomers!=null){
                for (TblCustomer tblCustomer : tblCustomers) {
                    String id = tblCustomer.getId();
                    list.add(id);
                }
                criteria.andCustomeridIn(list);
            }
           /* criteria.andFullnameLike("%"+fullname+"%");
            criteria.andAppellationLike("%"+fullname+"%");*/
        }
        if (name != null && !"".equals(name)){
            criteria.andNameLike("%"+name+"%");
        }
        if (source != null && !"".equals(source)){
            criteria.andSourceLike("%"+source+"%");
        }
        if (stage != null && !"".equals(stage)){
            criteria.andStageLike("%"+stage+"%");
        }
        if (transactionType != null && !"".equals(transactionType)){
            criteria.andTypeLike("%"+transactionType+"%");
        }
        if (lname != null && !"".equals(lname)){
            List list =new ArrayList();
            TblContactsExample Example = new TblContactsExample();
            TblContactsExample.Criteria criteria1 = Example.createCriteria();
            criteria1.andFullnameLike("%"+lname+"%");
            List<TblContacts> tblContacts = contactsMapper.selectByExample(Example);
            if (tblContacts.size()!=0 && tblContacts!=null){
                for (TblContacts tblContact : tblContacts) {
                    String id = tblContact.getId();
                    list.add(id);
                }
                criteria.andContactsidIn(list);
            }
        }
        List<TblTran> tblTrans = tranMapper.selectByExample(tranExample);
        if (tblTrans ==null || tblTrans.size()==0){
            throw new ResultException("查询为空");
        }
        for (TblTran tblTran : tblTrans) {
            String owner1 = tblTran.getOwner();
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria1 = tblUserExample.createCriteria();
            criteria1.andIdEqualTo(owner1);
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String name1 = tblUsers.get(0).getName();
            tblTran.setOwner(name1);
            String customerid = tblTran.getCustomerid();
            TblCustomer tblCustomer = customerMapper.selectByPrimaryKey(customerid);
            String name2 = tblCustomer.getName();
            tblTran.setCustomerid(name2);
            String contactsid = tblTran.getContactsid();
            TblContacts tblContacts = contactsMapper.selectByPrimaryKey(contactsid);
            String name3 = tblContacts.getFullname()+tblContacts.getAppellation();
            tblTran.setContactsid(name3);
        }

        PageInfo<TblTran> PageInfo = new PageInfo<>(tblTrans);
        List<TblTran> list = PageInfo.getList();
        long total = PageInfo.getTotal();
        PageResult pageResult = new PageResult(total,list);
        return pageResult;

    }
//修改页面展示信息
    @Override
    public Map<String, Object> editpage(String id,Map map2) {
        Map<String, Object> map =new HashMap<>();
        List list =new ArrayList();
        List<TblUser> tblUsers = userMapper.selectByExample(null);
        if (tblUsers!=null && tblUsers.size()!=0) {
            for (TblUser tblUser : tblUsers) {
                String name = tblUser.getName();
                list.add(name);
                map.put("owner",list);
            }
        }
        if (id==null ||"".equals(id)){
            throw new ResultException("id为空");
        }
        try {
            TblTran tblTran = tranMapper.selectByPrimaryKey(id);
            String activityid = tblTran.getActivityid();
            TblActivity activity = activityMapper.selectByPrimaryKey(activityid);
            String name = activity.getName();
            tblTran.setActivityid(name);
            map.put("tblTran",tblTran);
            String stage = tblTran.getStage();
            String o = (String)map2.get(stage);
            map.put("stage",o);
            return map;
        } catch (Exception e) {
            throw new ResultException("查询出错");
        }
    }
//更新页面
    @Override
    public void update(TblTran tblTran,String editBy) {
        String editTime = DateTimeUtil.getSysTime();
        tblTran.setEditby(editBy);
        tblTran.setEdittime(editTime);
        if (tblTran.getOwner()==null){
            throw new ResultException("所有者不能为空");
        }else {
            TblUserExample tblUserExample = new TblUserExample();
            TblUserExample.Criteria criteria = tblUserExample.createCriteria();
            criteria.andNameEqualTo(tblTran.getOwner());
            List<TblUser> tblUsers = userMapper.selectByExample(tblUserExample);
            String id = tblUsers.get(0).getId();
            tblTran.setOwner(id);
        }
        if (tblTran.getName()==null) {
            throw new ResultException("交易名称不能为空");
        }
        if (tblTran.getExpecteddate()==null) {
            throw new ResultException("日期不能为空");
        }
        if (tblTran.getCustomerid()==null) {
            throw new ResultException("客户不能为空");
        }
        if (tblTran.getStage()==null) {
            throw new ResultException("阶段不能为空");
        }
        try {
            tranMapper.updateByPrimaryKeySelective(tblTran);
            String id = tblTran.getId();
            TblTranHistoryExample tblTranHistoryExample = new TblTranHistoryExample();
            TblTranHistoryExample.Criteria criteria = tblTranHistoryExample.createCriteria();
            criteria.andTranidEqualTo(id);
            List<TblTranHistory> tblTranHistories = historyMapper.selectByExample(tblTranHistoryExample);
            for (TblTranHistory tblTranHistory : tblTranHistories) {
                if (tblTranHistory.getStage()==tblTran.getStage()){
                    tblTranHistory.setMoney(tblTran.getMoney());
                    tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
                    historyMapper.updateByPrimaryKeySelective(tblTranHistory);
                }
            }

            TblTranHistory tblTranHistory = new TblTranHistory();
            tblTranHistory.setCreatetime(editTime);
            tblTranHistory.setStage(tblTran.getStage());
            tblTranHistory.setCreateby(editBy);
            tblTranHistory.setMoney(tblTran.getMoney());
            tblTranHistory.setExpecteddate(tblTran.getExpecteddate());
            tblTranHistory.setTranid(tblTran.getId());
            tblTranHistory.setId(UUIDUtil.getUUID());
            historyMapper.insertSelective(tblTranHistory);
        } catch (Exception e) {
            throw new ResultException("修改失败");
        }

    }
    //获得交易信息
   public TblTran getTran(String id){
        if (id==null || "".equals(id)){
            throw new ResultException("查询为空");
        }
       TblTran tblTran = tranMapper.selectByPrimaryKey(id);
       return tblTran;
   }
   //删除交易
    public void del(String[] its){
        if (its!=null && its.length!=0){
            for (String it : its) {
                TblTran tblTran = tranMapper.selectByPrimaryKey(it);
                if (tblTran!=null){
                    TblTranHistoryExample tblTranHistoryExample = new TblTranHistoryExample();
                    TblTranHistoryExample.Criteria criteria = tblTranHistoryExample.createCriteria();
                    criteria.andTranidEqualTo(it);
                    List<TblTranHistory> tblTranHistories = historyMapper.selectByExample(tblTranHistoryExample);
                    if (tblTranHistories.size()!=0 && tblTranHistories!=null){
                        for (TblTranHistory tblTranHistory : tblTranHistories) {
                            String id = tblTranHistory.getId();
                            historyMapper.deleteByPrimaryKey(id);
                        }
                    }
                }else {
                    throw new ResultException("交易为空");
                }
                try {
                    tranMapper.deleteByPrimaryKey(it);
                } catch (Exception e) {
                    throw new ResultException("删除失败");
                }
            }
        }
    }
    //点击交易页面
    @Override
    public TblTran getById(String id) {
        TblTran tblTran = tranMapper.selectByPrimaryKey(id);
        if (tblTran==null){
            throw new ResultException("未找到详情信息");
        }
        TblCustomer customer = customerMapper.selectByPrimaryKey(tblTran.getCustomerid());
        tblTran.setCustomerid(customer.getName());
        TblActivity tblActivity = activityMapper.selectByPrimaryKey(tblTran.getActivityid());
        tblTran.setActivityid(tblActivity.getName());
        TblContacts contacts = contactsMapper.selectByPrimaryKey(tblTran.getContactsid());
        tblTran.setContactsid(contacts.getFullname());
        TblUser tblUser = userMapper.selectByPrimaryKey(tblTran.getOwner());
        tblTran.setOwner(tblUser.getName());
        return tblTran;
    }
    //锚点点击
    @Override
    public Map<String, Object> possibility(Map value,Map possibility,String id) {
        Map<String, Object> map = new HashMap<>();
        TblTran tblTran = tranMapper.selectByPrimaryKey(id);
        List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();

        //获取所有的阶段
        Set stage1 = (Set)value.get("stage");
        List list = new ArrayList(stage1);

        //获取圈圈和叉叉的分界点用作判断
        int point =0;
        for (int i = 0; i <list.size() ; i++) {
            TblDicValue dicValue = (TblDicValue) list.get(i);
            String value1 = dicValue.getValue();
            String stageVal1 = (String) possibility.get(value1);
            if ("0".equals(stageVal1)) {
                //在按照顺序排列的情况下  如果阶段的可能性为0 那么这个下标就是分界点
                point = i;
                break;
            }
        }
        //---------------------------------------------------------------------------------------
            //拿到阶段
            String stage = tblTran.getStage();
            //当前阶段的可能性
            String stageVal = (String) possibility.get(stage);
            //如果当前阶段的可能性是0，那么前面7个选项都是黑色的圈圈，后面的两个都是叉叉，不过需要判断是红叉还是黑叉
            if ("0".equals(stageVal)){
                //遍历所有的阶段
                for (int j = 0; j <list.size() ; j++) {
                    TblDicValue dicValue = (TblDicValue) list.get(j);
                    String value1 = dicValue.getValue();
                    String stageVal1 = (String) possibility.get(value1);
                    //找到等于0的阶段
                    if ("0".equals(stageVal1)) {
                        //如果等于0的阶段和我们当前阶段的名字是一样的，那么这个阶段就是红叉
                        if (value1.equals(stage)) {
                            Map<String, String> stageMap = new HashMap<String, String>();
                            //阶段名称用于前端显示
                            stageMap.put("text", value1);
                            //红叉的下标
                            stageMap.put("location", j + "");
                            //用于前端接受的类型4代表是红叉
                            stageMap.put("type", "4");
                            resultList.add(stageMap);
                        } else {
                            //黑叉
                            Map<String, String> stageMap = new HashMap<String, String>();
                            stageMap.put("text", value1);
                            stageMap.put("location", j + "");
                            stageMap.put("type", "5");
                            resultList.add(stageMap);
                        }
                    }else {
                        Map<String,String> stageMap = new HashMap<String,String>();
                        stageMap.put("text",value1);
                        stageMap.put("location",j+"");
                        stageMap.put("type","3");
                        resultList.add(stageMap);
                    }
                }
            }else{
                //不等于0等情况就说明前面的到锚点位置都是绿圈最后两个黑叉剩下的黑圈
                int index=0;
                for (int i = 0; i <list.size() ; i++) {
                    TblDicValue dicValue = (TblDicValue) list.get(i);
                    String value1 = dicValue.getValue();
                    String stageVal1 = (String) possibility.get(value1);
                    if (value1.equals(stage)){
                        index=i;
                        break;
                    }

                }
                for (int i = 0; i <list.size() ; i++) {
                    TblDicValue dicValue = (TblDicValue) list.get(i);
                    String value1 = dicValue.getValue();
                    String stageVal1 = (String) possibility.get(value1);
                    if (i<index){
                        //绿圈
                        Map<String,String> stageMap = new HashMap<String,String>();
                        stageMap.put("text",value1);
                        stageMap.put("location",i+"");
                        stageMap.put("type","1");
                        resultList.add(stageMap);
                    }else if (i==index){
                        //锚点
                        Map<String,String> stageMap = new HashMap<String,String>();
                        stageMap.put("text",value1);
                        stageMap.put("location",i+"");
                        stageMap.put("type","2");
                        resultList.add(stageMap);
                    }else if (i>index && i<point){
                        //黑圈
                        Map<String,String> stageMap = new HashMap<String,String>();
                        stageMap.put("text",value1);
                        stageMap.put("location",i+"");
                        stageMap.put("type","3");
                        resultList.add(stageMap);
                    }else{
                        Map<String,String> stageMap = new HashMap<String,String>();
                        stageMap.put("text",value1);
                        stageMap.put("location",i+"");
                        stageMap.put("type","5");
                        resultList.add(stageMap);
                    }
                }

            }
        map.put("dvList",list);// stage   value
        map.put("point",point);//按照顺序走自然到叉叉的分界点
        map.put("result",resultList);//所有的流程走完所有圈圈叉叉的排列顺序

        return  map;

    }
    @Override
    public TblTran addTranHistory(TblTranHistory tblTranHistory) {
        //补全属性
        String sysTime = DateTimeUtil.getSysTime();
        tblTranHistory.setId(UUIDUtil.getUUID());
        tblTranHistory.setCreatetime(sysTime);
        //修改交易历史
        TblTran tblTran = new TblTran();
        tblTran.setId(tblTranHistory.getTranid());
        tblTran.setStage(tblTranHistory.getStage());
        tblTran.setEditby(tblTranHistory.getCreateby());
        tblTran.setEdittime(sysTime);
        try {
            tranMapper.updateByPrimaryKeySelective(tblTran);
            historyMapper.insert(tblTranHistory);
        } catch (Exception e) {
            throw new ResultException("新增失败");
        }
        TblTran tblTran2 = tranMapper.selectByPrimaryKey(tblTranHistory.getTranid());
        return tblTran2;
    }





}
