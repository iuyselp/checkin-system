package com.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.checkin.entity.CheckPoint;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡点 Mapper 接口
 */
@Mapper
public interface CheckPointMapper extends BaseMapper<CheckPoint> {
}
