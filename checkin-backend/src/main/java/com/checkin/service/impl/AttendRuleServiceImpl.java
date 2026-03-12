package com.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.checkin.entity.AttendRule;
import com.checkin.mapper.AttendRuleMapper;
import com.checkin.service.AttendRuleService;
import org.springframework.stereotype.Service;

/**
 * 考勤规则服务实现
 */
@Service
public class AttendRuleServiceImpl extends ServiceImpl<AttendRuleMapper, AttendRule> implements AttendRuleService {

    @Override
    public AttendRule getDefaultRule() {
        LambdaQueryWrapper<AttendRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendRule::getStatus, 1)
                .orderByDesc(AttendRule::getCreateTime)
                .last("LIMIT 1");
        return getOne(wrapper);
    }
}
