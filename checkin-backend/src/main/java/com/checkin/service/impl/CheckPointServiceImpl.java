package com.checkin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.checkin.entity.CheckPoint;
import com.checkin.mapper.CheckPointMapper;
import com.checkin.service.CheckPointService;
import org.springframework.stereotype.Service;

/**
 * 打卡点服务实现
 */
@Service
public class CheckPointServiceImpl extends ServiceImpl<CheckPointMapper, CheckPoint> implements CheckPointService {
}
