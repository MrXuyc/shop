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
 * Time: 11:31
 */
public class PortalHandlerInterceptor implements HandlerInterceptor {

    private IUserService userService;

    private static Logger logger= LoggerFactory.getLogger(PortalHandlerInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String requestURI = request.getRequestURI();
        String ip = request.getRemoteAddr();//返回发出请求的IP地址
        String params = request.getQueryString();//返回请求行中的参数部分
        Map<String, String[]> parameterMap = request.getParameterMap();
        String host=request.getRemoteHost();//返回发出请求的客户机的主机名
        int port =request.getRemotePort();//返回发出请求的客户机的端口号。
        logger.info("请求URL:{}，请求URI:{}，请求URL参数:{},请求报文参数:{}，来访者ip:{}/:{}，端口号:{}，主机名:{}"
                ,request.getRequestURL(),request.getRequestURI(),params,parameterMap,ip, CusAccessObjectUtil.getIpAddress(request),port,host);
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if(user==null){
            ResponseWriteUtil.sendJsonMessage(response, ServerResponse.createByErrorCodeMsg(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc()));
            logger.warn("用户未登录访问需要登录的的权限");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }
}
