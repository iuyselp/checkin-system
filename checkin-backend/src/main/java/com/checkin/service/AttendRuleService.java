package com.checkin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.checkin.entity.AttendRule;

/**
 * 考勤规则服务接口
 */
public interface AttendRuleService extends IService<AttendRule> {

    /**
     * 获取默认考勤规则
     */
    AttendRule getDefaultRule();
}
