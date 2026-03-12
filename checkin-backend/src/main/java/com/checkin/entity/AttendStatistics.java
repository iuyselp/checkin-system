package com.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 考勤统计实体
 */
@Data
@TableName("t_attend_statistics")
public class AttendStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 统计月份 YYYY-MM
     */
    private String statMonth;

    /**
     * 应出勤天数
     */
    private Integer workDays;

    /**
     * 实际出勤天数
     */
    private Integer actualDays;

    /**
     * 迟到次数
     */
    private Integer lateCount;

    /**
     * 早退次数
     */
    private Integer earlyCount;

    /**
     * 缺勤次数
     */
    private Integer absentCount;

    /**
     * 正常次数
     */
    private Integer normalCount;

    /**
     * 状态 0-无效 1-有效
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
