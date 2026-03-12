package com.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.checkin.common.BusinessException;
import com.checkin.common.ExceptionCode;
import com.checkin.dto.UserLoginDTO;
import com.checkin.dto.UserRegisterDTO;
import com.checkin.entity.User;
import com.checkin.mapper.UserMapper;
import com.checkin.service.UserService;
import com.checkin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User register(UserRegisterDTO dto) {
        // 检查用户名是否存在
        User existUser = getByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException(ExceptionCode.USER_ALREADY_EXISTS);
        }

        // 检查手机号是否存在
        if (StringUtils.hasText(dto.getPhone())) {
            User existPhone = getByPhone(dto.getPhone());
            if (existPhone != null) {
                throw new BusinessException(ExceptionCode.PHONE_ALREADY_EXISTS);
            }
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setStatus(1);

        save(user);
        return user;
    }

    @Override
    public String login(UserLoginDTO dto) {
        // 查询用户
        User user = getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ExceptionCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ExceptionCode.USER_DISABLED);
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ExceptionCode.PASSWORD_ERROR);
        }

        // 生成 Token
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public User getByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        return getOne(wrapper);
    }
}
