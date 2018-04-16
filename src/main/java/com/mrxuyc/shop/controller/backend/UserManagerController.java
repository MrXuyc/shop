package com.mrxuyc.shop.controller.backend;

import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manager/user")
public class UserManagerController {
    @Autowired
    private IUserService userService;
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        ServerResponse<User> response=userService.login(username,password);
        if (response.isSuccess()){
            User user =response.getData();
            if (user.getRole()== Const.Role.ROLE_ADMIN){
                //说明是管理员
                session.setAttribute(Const.CURRENT_USER,user);
            }else{
                return ServerResponse.createByErrorMsg("不是管理员无法登录");
            }
        }
        return response;
    }
}
