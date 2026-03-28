package com.powerbank.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powerbank.common.BusinessException;
import com.powerbank.dto.PowerBankDTO;
import com.powerbank.entity.PowerBank;
import com.powerbank.mapper.PowerBankMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("充电宝服务测试")
class PowerBankServiceTest {

    @Mock
    private PowerBankMapper powerBankMapper;

    @InjectMocks
    private PowerBankService powerBankService;

    @Test
    @DisplayName("新增充电宝 - 编号已存在应抛出异常")
    void add_duplicateCode_shouldThrow() {
        PowerBankDTO dto = new PowerBankDTO();
        dto.setCode("PB-001");
        dto.setHourlyRate(new BigDecimal("2.00"));

        when(powerBankMapper.selectCount(any())).thenReturn(1L);
        assertThrows(BusinessException.class, () -> powerBankService.add(dto));
    }

    @Test
    @DisplayName("新增充电宝 - 正常新增应成功")
    void add_validInput_shouldSucceed() {
        PowerBankDTO dto = new PowerBankDTO();
        dto.setCode("PB-NEW");
        dto.setHourlyRate(new BigDecimal("3.00"));

        when(powerBankMapper.selectCount(any())).thenReturn(0L);
        when(powerBankMapper.insert(any(PowerBank.class))).thenReturn(1);

        assertDoesNotThrow(() -> powerBankService.add(dto));
        verify(powerBankMapper).insert(any(PowerBank.class));
    }

    @Test
    @DisplayName("更新充电宝 - 占用中不可编辑")
    void update_occupiedPowerBank_shouldThrow() {
        PowerBank existing = new PowerBank();
        existing.setId(1L);
        existing.setStatus(1); // 占用中

        when(powerBankMapper.selectById(1L)).thenReturn(existing);

        PowerBankDTO dto = new PowerBankDTO();
        dto.setCode("PB-001");
        dto.setHourlyRate(new BigDecimal("2.50"));

        assertThrows(BusinessException.class, () -> powerBankService.update(1L, dto));
    }

    @Test
    @DisplayName("更新状态 - 占用中不可变更状态")
    void updateStatus_occupiedPowerBank_shouldThrow() {
        PowerBank existing = new PowerBank();
        existing.setId(1L);
        existing.setStatus(1); // 占用中

        when(powerBankMapper.selectById(1L)).thenReturn(existing);
        assertThrows(BusinessException.class, () -> powerBankService.updateStatus(1L, 0));
    }

    @Test
    @DisplayName("按编号查询 - 不存在应抛出异常")
    void searchByCode_notFound_shouldThrow() {
        when(powerBankMapper.selectOne(any())).thenReturn(null);
        assertThrows(BusinessException.class, () -> powerBankService.getByCode("PB-999"));
    }

    @Test
    @DisplayName("按编号查询 - 存在应返回结果")
    void searchByCode_found_shouldReturn() {
        PowerBank pb = new PowerBank();
        pb.setId(1L);
        pb.setCode("PB-001");
        pb.setStatus(0);
        pb.setHourlyRate(new BigDecimal("2.00"));

        when(powerBankMapper.selectOne(any())).thenReturn(pb);
        PowerBank result = powerBankService.getByCode("PB-001");
        assertEquals("PB-001", result.getCode());
        assertEquals(0, result.getStatus());
    }

    @Test
    @DisplayName("统计空闲充电宝数量")
    void countAvailable_shouldReturnCount() {
        when(powerBankMapper.selectCount(any())).thenReturn(5L);
        long count = powerBankService.countAvailable();
        assertEquals(5L, count);
    }
}
