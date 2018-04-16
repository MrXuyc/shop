package com.mrxuyc.shop.service;

import com.mrxuyc.shop.common.ServerResponse;
import com.mrxuyc.shop.pojo.User;

public interface IUserService {
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str,String type);

    ServerResponse<String> selectQuestion(String username);


    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String password, String token);

    ServerResponse<String> resetPassword(User user, String passwordOld, String passwordNew);

    ServerResponse<User> updateInfomation(User user);

    ServerResponse<User> getInfomation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);
}
