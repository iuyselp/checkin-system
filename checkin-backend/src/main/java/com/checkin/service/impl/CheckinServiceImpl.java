package com.checkin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.checkin.common.BusinessException;
import com.checkin.common.ExceptionCode;
import com.checkin.dto.CheckinDTO;
import com.checkin.entity.CheckPoint;
import com.checkin.entity.CheckRecord;
import com.checkin.entity.AttendRule;
import com.checkin.mapper.CheckRecordMapper;
import com.checkin.service.CheckinService;
import com.checkin.service.CheckPointService;
import com.checkin.service.AttendRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 打卡服务实现
 */
@Service
@RequiredArgsConstructor
public class CheckinServiceImpl extends ServiceImpl<CheckRecordMapper, CheckRecord> implements CheckinService {

    private final CheckPointService checkPointService;
    private final AttendRuleService attendRuleService;

    @Value("${checkin.default-radius:100}")
    private Integer defaultRadius;

    @Value("${checkin.late-grace-minutes:10}")
    private Integer lateGraceMinutes;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckRecord workCheckin(Long userId, CheckinDTO dto) {
        // 获取打卡点
        CheckPoint point = checkPointService.getById(dto.getPointId());
        if (point == null) {
            throw new BusinessException(ExceptionCode.CHECKIN_POINT_NOT_FOUND);
        }
        if (point.getStatus() == 0) {
            throw new BusinessException(ExceptionCode.CHECKIN_POINT_DISABLED);
        }

        // 检查今日是否已上班打卡
        CheckRecord todayRecord = getTodayRecord(userId, 1);
        if (todayRecord != null) {
            throw new BusinessException(ExceptionCode.CHECKIN_ALREADY, "今日已上班打卡");
        }

        // 计算距离
        double distance = calculateDistance(
                dto.getLatitude().doubleValue(),
                dto.getLongitude().doubleValue(),
                point.getLatitude().doubleValue(),
                point.getLongitude().doubleValue()
        );

        // 判断是否在有效范围内
        int radius = point.getRadius() != null ? point.getRadius() : defaultRadius;
        if (distance > radius) {
            throw new BusinessException(ExceptionCode.CHECKIN_OUT_OF_RANGE, 
                    String.format("距离打卡点%.0f米，超出有效范围%d米", distance, radius));
        }

        // 获取考勤规则
        AttendRule rule = attendRuleService.getDefaultRule();
        LocalTime now = LocalTime.now();
        int status = determineWorkStatus(now, rule);

        // 创建打卡记录
        CheckRecord record = new CheckRecord();
        record.setUserId(userId);
        record.setPointId(point.getId());
        record.setCheckType(1); // 上班打卡
        record.setCheckTime(LocalDateTime.now());
        record.setLatitude(dto.getLatitude());
        record.setLongitude(dto.getLongitude());
        record.setDistance((int) distance);
        record.setStatus(status);
        record.setRemark(status == 2 ? "迟到" : "正常");

        save(record);
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckRecord offCheckin(Long userId, CheckinDTO dto) {
        // 获取打卡点
        CheckPoint point = checkPointService.getById(dto.getPointId());
        if (point == null) {
            throw new BusinessException(ExceptionCode.CHECKIN_POINT_NOT_FOUND);
        }
        if (point.getStatus() == 0) {
            throw new BusinessException(ExceptionCode.CHECKIN_POINT_DISABLED);
        }

        // 检查今日是否已下班打卡
        CheckRecord todayRecord = getTodayRecord(userId, 2);
        if (todayRecord != null) {
            throw new BusinessException(ExceptionCode.CHECKIN_ALREADY, "今日已下班打卡");
        }

        // 检查是否有上班打卡记录
        CheckRecord workRecord = getTodayRecord(userId, 1);
        if (workRecord == null) {
            throw new BusinessException(ExceptionCode.CHECKIN_TIME_ERROR, "请先进行上班打卡");
        }

        // 计算距离
        double distance = calculateDistance(
                dto.getLatitude().doubleValue(),
                dto.getLongitude().doubleValue(),
                point.getLatitude().doubleValue(),
                point.getLongitude().doubleValue()
        );

        // 判断是否在有效范围内
        int radius = point.getRadius() != null ? point.getRadius() : defaultRadius;
        if (distance > radius) {
            throw new BusinessException(ExceptionCode.CHECKIN_OUT_OF_RANGE,
                    String.format("距离打卡点%.0f米，超出有效范围%d米", distance, radius));
        }

        // 获取考勤规则
        AttendRule rule = attendRuleService.getDefaultRule();
        LocalTime now = LocalTime.now();
        int status = determineOffStatus(now, rule);

        // 创建打卡记录
        CheckRecord record = new CheckRecord();
        record.setUserId(userId);
        record.setPointId(point.getId());
        record.setCheckType(2); // 下班打卡
        record.setCheckTime(LocalDateTime.now());
        record.setLatitude(dto.getLatitude());
        record.setLongitude(dto.getLongitude());
        record.setDistance((int) distance);
        record.setStatus(status);
        record.setRemark(status == 3 ? "早退" : "正常");

        save(record);
        return record;
    }

    @Override
    public Page<CheckRecord> getRecords(Long userId, LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        LambdaQueryWrapper<CheckRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckRecord::getUserId, userId);
        
        if (startDate != null) {
            wrapper.ge(CheckRecord::getCheckTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            wrapper.le(CheckRecord::getCheckTime, endDate.atTime(LocalTime.MAX));
        }
        
        wrapper.orderByDesc(CheckRecord::getCheckTime);
        
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public CheckRecord getTodayRecord(Long userId, Integer checkType) {
        LambdaQueryWrapper<CheckRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckRecord::getUserId, userId)
                .eq(CheckRecord::getCheckType, checkType)
                .ge(CheckRecord::getCheckTime, LocalDate.now().atStartOfDay())
                .lt(CheckRecord::getCheckTime, LocalDate.now().plusDays(1).atStartOfDay());
        return getOne(wrapper);
    }

    @Override
    public double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; // 地球半径（米）
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    /**
     * 判断上班打卡状态
     */
    private int determineWorkStatus(LocalTime checkTime, AttendRule rule) {
        if (rule == null) {
            return 1; // 正常
        }
        
        LocalTime workStart = rule.getWorkStartTime();
        int lateGrace = rule.getLateGraceMinutes() != null ? rule.getLateGraceMinutes() : lateGraceMinutes;
        
        if (checkTime.isBefore(workStart.plusMinutes(lateGrace))) {
            return 1; // 正常
        } else {
            return 2; // 迟到
        }
    }

    /**
     * 判断下班打卡状态
     */
    private int determineOffStatus(LocalTime checkTime, AttendRule rule) {
        if (rule == null) {
            return 1; // 正常
        }
        
        LocalTime workEnd = rule.getWorkEndTime();
        int earlyGrace = rule.getEarlyGraceMinutes() != null ? rule.getEarlyGraceMinutes() : 10;
        
        if (checkTime.isAfter(workEnd.minusMinutes(earlyGrace))) {
            return 1; // 正常
        } else {
            return 3; // 早退
        }
    }
}
