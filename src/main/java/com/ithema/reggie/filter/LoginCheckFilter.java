package com.ithema.reggie.filter;


import com.alibaba.fastjson.JSON;
import com.ithema.reggie.common.BaseContext;
import com.ithema.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public  static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        System.out.println("拦截过滤--------------");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        1.	获取本次请求的url
        String requestURI = request.getRequestURI();
//        log.info("拦截到请求：{}", request.getRequestURI());

//        2.	定义不需要拦截的请求路径，判断本次请求是否拦截
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

//        3.	如果不需拦截直接放行
        boolean check = check(urls, requestURI);
        if(check){
            log.info("本次请求无需拦截：{}", request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

//        4.	判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("已登录无需拦截：{}", request.getRequestURI());

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            filterChain.doFilter(request, response);
            return;
        }

        if(request.getSession().getAttribute("user") != null){
            log.info("已登录无需拦截：{}", request.getRequestURI());

            Long empId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(empId);

            long id = Thread.currentThread().getId();
            log.info("线程id为：{}",id);

            filterChain.doFilter(request, response);
            return;
        }

//        5.	如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("未登录：{}", request.getRequestURI());
        return;
    }

    private boolean check(String[] urls, String requestURI) {
        for (String s: urls
             ) {
            boolean match = PATH_MATCHER.match(s, requestURI);
            if(match)
                return true;
        }
        return false;
    }
}
