package com.checkin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.checkin.dto.CheckinDTO;
import com.checkin.entity.CheckRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡服务接口
 */
public interface CheckinService extends IService<CheckRecord> {

    /**
     * 上班打卡
     */
    CheckRecord workCheckin(Long userId, CheckinDTO dto);

    /**
     * 下班打卡
     */
    CheckRecord offCheckin(Long userId, CheckinDTO dto);

    /**
     * 查询打卡记录
     */
    Page<CheckRecord> getRecords(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size);

    /**
     * 获取今日打卡状态
     */
    CheckRecord getTodayRecord(Long userId, Integer checkType);

    /**
     * 计算两点距离（米）
     */
    double calculateDistance(double lat1, double lng1, double lat2, double lng2);
}
