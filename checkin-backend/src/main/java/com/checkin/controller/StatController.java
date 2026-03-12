package com.checkin.controller;

import com.checkin.common.Result;
import com.checkin.entity.CheckRecord;
import com.checkin.service.CheckinService;
import com.checkin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/stat")
@RequiredArgsConstructor
public class StatController {

    private final CheckinService checkinService;
    private final JwtUtil jwtUtil;

    /**
     * 个人考勤统计
     */
    @GetMapping("/personal")
    public Result<Map<String, Object>> personalStat(
            @RequestParam(required = false) String month,
            HttpServletRequest request) {
        
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        
        // 默认当前月份
        if (month == null || month.isEmpty()) {
            month = YearMonth.now().toString();
        }
        
        YearMonth ym = YearMonth.parse(month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        
        // 获取打卡记录
        List<CheckRecord> records = checkinService.list(
            checkinService.lambdaQuery()
                .eq(CheckRecord::getUserId, userId)
                .ge(CheckRecord::getCheckTime, startDate.atStartOfDay())
                .le(CheckRecord::getCheckTime, endDate.atTime(java.time.LocalTime.MAX))
        );
        
        // 统计数据
        int workDays = 0; // 应出勤天数（工作日）
        int actualDays = 0; // 实际出勤天数
        int normalCount = 0;
        int lateCount = 0;
        int earlyCount = 0;
        int absentCount = 0;
        
        for (CheckRecord record : records) {
            if (record.getCheckType() == 1) { // 上班打卡
                actualDays++;
                switch (record.getStatus()) {
                    case 1: normalCount++; break;
                    case 2: lateCount++; break;
                    case 4: absentCount++; break;
                }
            } else if (record.getCheckType() == 2) { // 下班打卡
                if (record.getStatus() == 3) {
                    earlyCount++;
                }
            }
        }
        
        // 计算应出勤天数（简化：减去周末）
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            if (date.getDayOfWeek().getValue() <= 5) { // 周一到周五
                workDays++;
            }
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("month", month);
        data.put("workDays", workDays);
        data.put("actualDays", actualDays);
        data.put("normalCount", normalCount);
        data.put("lateCount", lateCount);
        data.put("earlyCount", earlyCount);
        data.put("absentCount", absentCount);
        data.put("attendanceRate", actualDays > 0 ? String.format("%.2f%%", (double) normalCount / actualDays * 100) : "0%");
        
        return Result.success(data);
    }

    /**
     * 从请求中获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
