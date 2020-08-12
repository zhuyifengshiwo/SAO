package com.powernode.workbench.service.impl;

import com.powernode.util.User;
import com.powernode.workbench.mapper.TblActivityMapper;
import com.powernode.workbench.mapper.TblClueMapper;
import com.powernode.workbench.pojo.TblActivity;
import com.powernode.workbench.pojo.TblClue;
import com.powernode.workbench.service.EchartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EchartServiceImpl implements EchartService {
    @Autowired
    TblActivityMapper activityMapper;
    @Autowired
    TblClueMapper clueMapper;

    @Override
    public Map<String, Object> getName() {
        Map<String, Object> map=new HashMap<>();
        List name = activityMapper.ActiveName();
        List cost = activityMapper.getCost();
        map.put("name",name);
        map.put("cost",cost);
        return map;

    }

    @Override
    public Map<String, Object> getCule() {
        List<TblClue> tblClues = clueMapper.selectByExample(null);
        Map<String, Object> map =new HashMap<>();
        int xians=0;
        int fur=0;
        for (TblClue tblClue : tblClues) {
            String appellation = tblClue.getAppellation();
            if ("先生".equals(appellation)){
                xians++;
            }else if ("夫人".equals(appellation)){
                fur++;
            }

        }
        User man = new User(xians, "先生");
        User woman = new User(fur, "夫人");


        map.put("boy",man);
        map.put("girl",woman);
        return map;

    }
    //线索统计图

}
