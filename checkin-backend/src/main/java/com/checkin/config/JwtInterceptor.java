package com.checkin.config;

import com.checkin.common.ExceptionCode;
import com.checkin.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证拦截器
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    
    // 放行路径（不需要认证）
    private static final List<String> EXCLUDE_PATHS = Arrays.asList(
            "/user/login",
            "/user/register",
            "/error",
            "/actuator/**"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ServletException, IOException {
        
        String uri = request.getRequestURI();
        
        // 放行路径直接通过
        for (String path : EXCLUDE_PATHS) {
            if (pathMatcher.match(path, uri)) {
                return true;
            }
        }
        
        // 获取 Token
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":1002,\"message\":\"未登录\"}");
            return false;
        }
        
        String token = bearerToken.substring(7);
        
        // 验证 Token
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":1004,\"message\":\"Token 无效\"}");
            return false;
        }
        
        // Token 有效，继续处理
        return true;
    }
}
