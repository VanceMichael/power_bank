package com.powerbank.service;

import com.powerbank.common.BusinessException;
import com.powerbank.entity.PowerBank;
import com.powerbank.entity.RentalOrder;
import com.powerbank.mapper.PowerBankMapper;
import com.powerbank.mapper.RentalOrderMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("租赁服务测试")
class RentalServiceTest {

    @Mock
    private RentalOrderMapper rentalOrderMapper;

    @Mock
    private PowerBankMapper powerBankMapper;

    @InjectMocks
    private RentalService rentalService;

    @Test
    @DisplayName("租赁 - 充电宝不存在应抛出异常")
    void rent_powerBankNotFound_shouldThrow() {
        when(powerBankMapper.selectById(99L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> rentalService.rent(1L, 99L));
    }

    @Test
    @DisplayName("租赁 - 充电宝非空闲应抛出异常")
    void rent_powerBankNotAvailable_shouldThrow() {
        PowerBank pb = new PowerBank();
        pb.setId(1L);
        pb.setStatus(1); // 占用中

        when(powerBankMapper.selectById(1L)).thenReturn(pb);
        assertThrows(BusinessException.class, () -> rentalService.rent(1L, 1L));
    }

    @Test
    @DisplayName("租赁 - 正常租赁应成功")
    void rent_validInput_shouldSucceed() {
        PowerBank pb = new PowerBank();
        pb.setId(1L);
        pb.setStatus(0); // 空闲
        pb.setHourlyRate(new BigDecimal("2.00"));

        when(powerBankMapper.selectById(1L)).thenReturn(pb);
        when(rentalOrderMapper.insert(any(RentalOrder.class))).thenReturn(1);
        when(powerBankMapper.updateById(any(PowerBank.class))).thenReturn(1);

        assertDoesNotThrow(() -> rentalService.rent(2L, 1L));
        verify(rentalOrderMapper).insert(any(RentalOrder.class));
    }

    @Test
    @DisplayName("归还 - 订单不存在应抛出异常")
    void returnPowerBank_orderNotFound_shouldThrow() {
        when(rentalOrderMapper.selectById(99L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> rentalService.returnPowerBank(99L, 1L));
    }

    @Test
    @DisplayName("归还 - 非本人订单应抛出异常")
    void returnPowerBank_notOwner_shouldThrow() {
        RentalOrder order = new RentalOrder();
        order.setId(1L);
        order.setUserId(2L); // 属于用户2
        order.setStatus(0);

        when(rentalOrderMapper.selectById(1L)).thenReturn(order);
        assertThrows(BusinessException.class, () -> rentalService.returnPowerBank(1L, 3L)); // 用户3尝试归还
    }

    @Test
    @DisplayName("支付 - 已支付订单不可重复支付")
    void pay_alreadyPaid_shouldThrow() {
        RentalOrder order = new RentalOrder();
        order.setId(1L);
        order.setUserId(1L);
        order.setPaymentStatus(1); // 已支付

        when(rentalOrderMapper.selectById(1L)).thenReturn(order);
        assertThrows(BusinessException.class, () -> rentalService.pay(1L, 1L));
    }
}
