package com.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 考勤规则实体
 */
@Data
@TableName("t_attend_rule")
public class AttendRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 上班时间
     */
    private LocalTime workStartTime;

    /**
     * 下班时间
     */
    private LocalTime workEndTime;

    /**
     * 迟到容忍分钟数
     */
    private Integer lateGraceMinutes;

    /**
     * 早退容忍分钟数
     */
    private Integer earlyGraceMinutes;

    /**
     * 工作日 1-7 表示周一到周日
     */
    private String workDays;

    /**
     * 状态 0-禁用 1-正常
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
