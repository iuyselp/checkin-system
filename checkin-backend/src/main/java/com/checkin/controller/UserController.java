package com.checkin.controller;

import com.checkin.common.Result;
import com.checkin.dto.UserLoginDTO;
import com.checkin.dto.UserRegisterDTO;
import com.checkin.entity.User;
import com.checkin.service.UserService;
import com.checkin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody @Validated UserRegisterDTO dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated UserLoginDTO dto) {
        String token = userService.login(dto);
        User user = userService.getByUsername(dto.getUsername());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success(data);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        User user = userService.getById(userId);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<User> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        user.setId(userId);
        userService.updateById(user);
        return Result.success(user);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        User user = userService.getById(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        // 加密新密码并更新
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        
        return Result.success();
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
