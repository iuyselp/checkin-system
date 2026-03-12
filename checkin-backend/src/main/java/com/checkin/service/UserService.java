package com.checkin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.checkin.dto.UserLoginDTO;
import com.checkin.dto.UserRegisterDTO;
import com.checkin.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    User register(UserRegisterDTO dto);

    /**
     * 用户登录
     * @return JWT Token
     */
    String login(UserLoginDTO dto);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    User getByPhone(String phone);
}
