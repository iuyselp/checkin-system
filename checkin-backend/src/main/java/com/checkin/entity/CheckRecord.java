package com.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 打卡记录实体
 */
@Data
@TableName("t_check_record")
public class CheckRecord implements Serializable {

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
     * 打卡点 ID
     */
    private Long pointId;

    /**
     * 打卡类型 1-上班 2-下班
     */
    private Integer checkType;

    /**
     * 打卡时间
     */
    private LocalDateTime checkTime;

    /**
     * 打卡纬度
     */
    private BigDecimal latitude;

    /**
     * 打卡经度
     */
    private BigDecimal longitude;

    /**
     * 与打卡点距离（米）
     */
    private Integer distance;

    /**
     * 状态 1-正常 2-迟到 3-早退 4-缺卡
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
