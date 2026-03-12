package com.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.checkin.entity.AttendStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤统计 Mapper 接口
 */
@Mapper
public interface AttendStatisticsMapper extends BaseMapper<AttendStatistics> {
}
