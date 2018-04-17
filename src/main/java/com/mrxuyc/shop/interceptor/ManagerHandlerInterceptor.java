package com.mrxuyc.shop.interceptor;

import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ResponseCode;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IUserService;
import com.mrxuyc.shop.util.CusAccessObjectUtil;
import com.mrxuyc.shop.util.ResponseWriteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: mrxuyc
 * Date: 2018-04-17
 * Time: 9:24
 */
public class ManagerHandlerInterceptor implements HandlerInterceptor {


    private IUserService userService;

    private static Logger logger= LoggerFactory.getLogger(ManagerHandlerInterceptor.class);


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
//        String getServerName()：获取服务器名，localhost；
//        String getServerPort()：获取服务器端口号，8080；
//        String getContextPath()：获取项目名，/Example；
//        String getServletPath()：获取Servlet路径，/AServlet；
//        String getQueryString()：获取参数部分，即问号后面的部分：username=zhangsan
//        String getRequestURI()：获取请求URI，等于项目名+Servlet路径：/Example/AServlet
//        String getRequestURL()：获取请求URL，等于不包含参数的整个请求路径：http://localhost:8080/Example/AServlet
        String requestURI = request.getRequestURI();
        String ip = request.getRemoteAddr();//返回发出请求的IP地址
        String params = request.getQueryString();//返回请求行中的参数部分
        Map<String, String[]> parameterMap = request.getParameterMap();
        String host=request.getRemoteHost();//返回发出请求的客户机的主机名
        int port =request.getRemotePort();//返回发出请求的客户机的端口号。
        logger.info("请求URL:{}，请求URI:{}，请求URL参数:{},请求报文参数:{}，来访者ip:{}/:{}，端口号:{}，主机名:{}"
                ,request.getRequestURL(),request.getRequestURI(),params,parameterMap,ip, CusAccessObjectUtil.getIpAddress(request),port,host);
        if (requestURI.indexOf("/manager/")>0){
            User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
            if(user==null){
                ResponseWriteUtil.sendJsonMessage(response,ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录"));
                logger.warn("用户未登录访问管理系统");
                return false;
            }
            if(userService.checkAdminRole(user).isSuccess()){
                ResponseWriteUtil.sendJsonMessage(response,ServerResponse.createByErrorMsg("无权限操作，需要管理员操作！"));
                logger.warn("用户无权限访问管理系统");
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }



}
