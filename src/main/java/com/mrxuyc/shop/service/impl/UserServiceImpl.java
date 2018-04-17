package com.mrxuyc.shop.service.impl;

import com.mrxuyc.shop.common.Const;
import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.common.TokenCache;
import com.mrxuyc.shop.dao.UserMapper;
import com.mrxuyc.shop.pojo.User;
import com.mrxuyc.shop.service.IUserService;
import com.mrxuyc.shop.util.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //先查找该名称的用户名
        int resultCount = userMapper.checkUsername(username);
        if(0==resultCount){
            return ServerResponse.createByErrorMsg("用户名不存在");
        }
        //密码md5
        password=Md5Util.MD5EncodeUtf8(password);
        User user=userMapper.selectLogin(username,password);
        if (user==null){
            return ServerResponse.createByErrorMsg("密码错误");
        }
        //将密码置为空字符串  StringUtils.EMPTY
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        //先查找该名称的用户名
        ServerResponse validResponse=this.checkValid(user.getUsername(),"username");
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse=this.checkValid(user.getEmail(),"email");
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //md5加密
        user.setPassword(Md5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount=userMapper.insert(user);
        if (0==resultCount){
            return ServerResponse.createByErrorMsg("注册失败");
        }else{
            return ServerResponse.createBySuccessMsg("注册成功");
        }
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNoneBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){
                int resulrCount =userMapper.checkUsername(str);
                if (resulrCount>0){
                    return ServerResponse.createByErrorMsg("用户名已存在");
                }
            }else if(Const.EMAIL.equals(type)){
                int resulrCount =userMapper.checkEmail(str);
                if (resulrCount>0){
                    return ServerResponse.createByErrorMsg("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMsg("参数错误");
        }
        return ServerResponse.createBySuccess("校验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse=this.checkValid(username,"username");
        if(validResponse.isSuccess()){
           //用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String question=userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMsg("找回密码的问题是空的");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //验证成功
            String forgetToken= UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        }
        return ServerResponse.createByErrorMsg("问题答案验证错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String password, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMsg("参数错误，token需要传递");
        }
        ServerResponse validResponse=this.checkValid(username,"username");
        if(validResponse.isSuccess()){
            //用户不存在
            return ServerResponse.createByErrorMsg("用户不存在");
        }
        String token =TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMsg("token无效或者过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password=Md5Util.MD5EncodeUtf8(password);
            int rowCount=userMapper.updatePasswordByUsername(username,md5Password);
            if (rowCount>0){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMsg("token验证错误");
        }
        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew) {
        //防止横向越权
        Integer userId=user.getId();
        int rowCount=userMapper.checkPassword(Md5Util.MD5EncodeUtf8(passwordOld),userId);
        if (rowCount==0){
            return ServerResponse.createByErrorMsg("旧密码错误");
        }
        //防止更改密码时修改其他项
        user=new User();
        user.setId(userId);
        user.setPassword(Md5Util.MD5EncodeUtf8(passwordNew));
        rowCount=userMapper.updateByPrimaryKeySelective(user);
        if (rowCount>0){
            return ServerResponse.createBySuccess("修改密码成功");
        }
        return ServerResponse.createByErrorMsg("修改密码失败");
    }

    @Override
    public ServerResponse<User> updateInfomation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return ServerResponse.createByErrorMsg("邮箱已经存在，请更改再尝试更新");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateRow = userMapper.updateByPrimaryKeySelective(user);
        if(updateRow>0){
            return ServerResponse.createBySuccess("更新个人信息成功",updateUser);
        }
        return ServerResponse.createByErrorMsg("更新个人信息失败");
    }

    @Override
    public ServerResponse<User> getInfomation(Integer userId) {
        User user =userMapper.selectByPrimaryKey(userId);
        if(user==null){
            return ServerResponse.createByErrorMsg("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    @Override
    public ServerResponse<String> checkAdminRole(User user) {
        if(user!=null&&user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
