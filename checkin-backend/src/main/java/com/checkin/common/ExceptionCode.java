package com.checkin.common;

import lombok.Getter;

/**
 * 异常错误码枚举
 */
@Getter
public enum ExceptionCode {

    // 通用错误 1000-1999
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统错误"),
    PARAM_ERROR(1001, "参数错误"),
    NOT_LOGIN(1002, "未登录"),
    TOKEN_EXPIRED(1003, "Token 已过期"),
    TOKEN_INVALID(1004, "Token 无效"),
    NO_PERMISSION(1005, "无权限"),

    // 用户相关 2000-2999
    USER_NOT_FOUND(2001, "用户不存在"),
    USER_ALREADY_EXISTS(2002, "用户已存在"),
    PASSWORD_ERROR(2003, "密码错误"),
    USER_DISABLED(2004, "用户已禁用"),
    PHONE_ALREADY_EXISTS(2005, "手机号已存在"),

    // 打卡相关 3000-3999
    CHECKIN_ALREADY(3001, "今日已打卡"),
    CHECKIN_OUT_OF_RANGE(3002, "不在打卡范围内"),
    CHECKIN_POINT_NOT_FOUND(3003, "打卡点不存在"),
    CHECKIN_POINT_DISABLED(3004, "打卡点已禁用"),
    CHECKIN_TIME_ERROR(3005, "打卡时间错误"),
    LOCATION_GET_FAILED(3006, "获取位置失败"),

    // 考勤规则 4000-4999
    RULE_NOT_FOUND(4001, "考勤规则不存在"),
    RULE_ALREADY_EXISTS(4002, "考勤规则已存在");

    private final Integer code;
    private final String message;

    ExceptionCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
