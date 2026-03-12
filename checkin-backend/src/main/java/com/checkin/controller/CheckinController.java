package com.checkin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.checkin.common.PageResult;
import com.checkin.common.Result;
import com.checkin.dto.CheckinDTO;
import com.checkin.entity.CheckRecord;
import com.checkin.service.CheckinService;
import com.checkin.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 打卡控制器
 */
@RestController
@RequestMapping("/checkin")
@RequiredArgsConstructor
public class CheckinController {

    private final CheckinService checkinService;
    private final JwtUtil jwtUtil;

    /**
     * 上班打卡
     */
    @PostMapping("/work")
    public Result<CheckRecord> workCheckin(@RequestBody @Validated CheckinDTO dto,
                                           HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        CheckRecord record = checkinService.workCheckin(userId, dto);
        return Result.success("上班打卡成功", record);
    }

    /**
     * 下班打卡
     */
    @PostMapping("/off")
    public Result<CheckRecord> offCheckin(@RequestBody @Validated CheckinDTO dto,
                                          HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        CheckRecord record = checkinService.offCheckin(userId, dto);
        return Result.success("下班打卡成功", record);
    }

    /**
     * 查询打卡记录
     */
    @GetMapping("/records")
    public Result<PageResult<CheckRecord>> getRecords(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        
        Page<CheckRecord> pageResult = checkinService.getRecords(userId, startDate, endDate, page, size);
        return Result.success(PageResult.from(pageResult));
    }

    /**
     * 获取今日打卡状态
     */
    @GetMapping("/today/status")
    public Result<Map<String, Object>> getTodayStatus(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        Long userId = jwtUtil.getUserId(token);
        
        CheckRecord workRecord = checkinService.getTodayRecord(userId, 1);
        CheckRecord offRecord = checkinService.getTodayRecord(userId, 2);
        
        Map<String, Object> data = new HashMap<>();
        data.put("workRecord", workRecord);
        data.put("offRecord", offRecord);
        data.put("hasWorkCheckin", workRecord != null);
        data.put("hasOffCheckin", offRecord != null);
        
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
