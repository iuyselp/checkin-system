package com.checkin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.checkin.common.PageResult;
import com.checkin.common.Result;
import com.checkin.entity.CheckPoint;
import com.checkin.service.CheckPointService;
import com.checkin.service.CheckinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 打卡点控制器
 */
@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
public class PointController {

    private final CheckPointService checkPointService;
    private final CheckinService checkinService;

    /**
     * 获取打卡点列表
     */
    @GetMapping("/list")
    public Result<PageResult<CheckPoint>> list(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        LambdaQueryWrapper<CheckPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckPoint::getStatus, 1);
        if (name != null && !name.isEmpty()) {
            wrapper.like(CheckPoint::getName, name);
        }
        wrapper.orderByDesc(CheckPoint::getCreateTime);
        
        Page<CheckPoint> pageResult = checkPointService.page(new Page<>(page, size), wrapper);
        return Result.success(PageResult.from(pageResult));
    }

    /**
     * 获取打卡点详情
     */
    @GetMapping("/{id}")
    public Result<CheckPoint> detail(@PathVariable Long id) {
        CheckPoint point = checkPointService.getById(id);
        return Result.success(point);
    }

    /**
     * 创建打卡点
     */
    @PostMapping
    public Result<CheckPoint> create(@RequestBody CheckPoint point) {
        checkPointService.save(point);
        return Result.success(point);
    }

    /**
     * 更新打卡点
     */
    @PutMapping("/{id}")
    public Result<CheckPoint> update(@PathVariable Long id, @RequestBody CheckPoint point) {
        point.setId(id);
        checkPointService.updateById(point);
        return Result.success(point);
    }

    /**
     * 删除打卡点
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkPointService.removeById(id);
        return Result.success();
    }

    /**
     * 校验位置是否在打卡点范围内
     */
    @PostMapping("/check")
    public Result<Map<String, Object>> checkLocation(
            @RequestParam Long pointId,
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude) {
        
        CheckPoint point = checkPointService.getById(pointId);
        if (point == null) {
            return Result.error("打卡点不存在");
        }

        double distance = checkinService.calculateDistance(
                latitude.doubleValue(),
                longitude.doubleValue(),
                point.getLatitude().doubleValue(),
                point.getLongitude().doubleValue()
        );

        int radius = point.getRadius() != null ? point.getRadius() : 100;
        boolean inRange = distance <= radius;

        Map<String, Object> data = new HashMap<>();
        data.put("inRange", inRange);
        data.put("distance", distance);
        data.put("radius", radius);
        data.put("pointName", point.getName());

        return Result.success(data);
    }

    /**
     * 获取所有可用打卡点（不分页）
     */
    @GetMapping("/all")
    public Result<List<CheckPoint>> all() {
        LambdaQueryWrapper<CheckPoint> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CheckPoint::getStatus, 1);
        List<CheckPoint> list = checkPointService.list(wrapper);
        return Result.success(list);
    }
}
