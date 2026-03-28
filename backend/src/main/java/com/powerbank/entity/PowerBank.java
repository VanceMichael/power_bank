package com.powerbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("power_bank")
public class PowerBank {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private Integer status; // 0-空闲 1-占用 2-故障
    private BigDecimal hourlyRate;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
