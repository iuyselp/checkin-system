package com.checkin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.checkin.entity.CheckRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡记录 Mapper 接口
 */
@Mapper
public interface CheckRecordMapper extends BaseMapper<CheckRecord> {
}
