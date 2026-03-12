package com.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.checkin.entity.AttendRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤规则 Mapper 接口
 */
@Mapper
public interface AttendRuleMapper extends BaseMapper<AttendRule> {
}
