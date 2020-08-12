package com.powernode.listener;


import com.powernode.workbench.service.CuleService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.*;

public class TypeListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //监听器拿到与spring容器相关的service对象
        ApplicationContext appContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        //加载service对象
        CuleService bean = (CuleService)appContext.getBean("type");
        //运行方法
        Map cule = bean.getCule();
        //储存到全局作用域中
        //将配置文件里面的key和value显示出来放进全局作用域
        sce.getServletContext().setAttribute("map",cule);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("properties/Stage2Possibility");
        Enumeration<String> keys =  resourceBundle.getKeys();
        Map<String,String> map =new TreeMap<String,String>();
        while (keys.hasMoreElements()){
            String s = keys.nextElement();
            String string = resourceBundle.getString(s);
            map.put(s,string);
        }
        sce.getServletContext().setAttribute("map2",map);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
