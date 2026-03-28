package com.powerbank.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rental_order")
public class RentalOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long powerBankId;
    private LocalDateTime rentalTime;
    private LocalDateTime returnTime;
    private BigDecimal deposit;
    private Integer depositStatus; // 0-未付 1-已付 2-已退
    private BigDecimal totalFee;
    private Integer paymentStatus; // 0-未支付 1-已支付
    private Integer status; // 0-进行中 1-已归还 2-超时
    private Integer overtime; // 0-否 1-是
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 非数据库字段 - 用于联表查询展示
    @TableField(exist = false)
    private String powerBankCode;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private String realName;
    @TableField(exist = false)
    private BigDecimal hourlyRate;
}
