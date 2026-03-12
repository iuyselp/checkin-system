package com.checkin.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 打卡 DTO
 */
@Data
public class CheckinDTO {

    /**
     * 打卡点 ID
     */
    @NotNull(message = "打卡点 ID 不能为空")
    private Long pointId;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private BigDecimal longitude;
}
