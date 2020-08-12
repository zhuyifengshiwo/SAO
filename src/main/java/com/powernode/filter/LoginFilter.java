package com.powernode.filter;

import com.powernode.settings.pojo.TblUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        TblUser user = (TblUser)request.getSession().getAttribute("USER");
        String requestURI = request.getRequestURI();
        //不管怎么样这些路径都是可以访问的
        if (requestURI.endsWith(".JPG")||requestURI.contains("/login") || requestURI.contains("fonts")||requestURI.endsWith(".js")||requestURI.endsWith(".css")||requestURI.endsWith("/name")){
                chain.doFilter(request,response);
            }else{
            //如果session里面有值，都可以访问
            if (user != null){
                chain.doFilter(request,response);
            }else {
                response.sendRedirect("/login.html");
            }
        }


        }



    public void init(FilterConfig config) throws ServletException {

    }

}
