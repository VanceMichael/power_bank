package com.powerbank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PowerBankDTO {
    @NotBlank(message = "充电宝编号不能为空")
    private String code;

    @NotNull(message = "每小时费用不能为空")
    @DecimalMin(value = "0.01", message = "费用必须大于0")
    private BigDecimal hourlyRate;
}
