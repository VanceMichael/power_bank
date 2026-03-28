package com.powerbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powerbank.common.BusinessException;
import com.powerbank.entity.PowerBank;
import com.powerbank.entity.RentalOrder;
import com.powerbank.entity.User;
import com.powerbank.mapper.PowerBankMapper;
import com.powerbank.mapper.RentalOrderMapper;
import com.powerbank.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalOrderMapper rentalOrderMapper;
    private final PowerBankMapper powerBankMapper;
    private final UserMapper userMapper;

    private static final BigDecimal DEFAULT_DEPOSIT = new BigDecimal("99.00");
    private static final int MAX_RENTAL_HOURS = 24; // 超过24小时算超时
    
    // 信誉分规则
    private static final int CREDIT_ON_TIME_RETURN = 2;    // 按时归还 +2 分
    private static final int CREDIT_OVERTIME_PENALTY = 10; // 超时归还 -10 分
    private static final int CREDIT_MIN = 0;               // 最低信誉分
    private static final int CREDIT_MAX = 100;             // 最高信誉分

    @Transactional
    public RentalOrder rent(Long userId, Long powerBankId) {
        // 检查用户信誉分
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getCreditScore() < 30) {
            throw new BusinessException("您的信誉分过低（低于30分），暂时无法租赁充电宝");
        }
        
        // 检查是否有未归还的订单
        Long activeCount = rentalOrderMapper.selectCount(
                new LambdaQueryWrapper<RentalOrder>()
                        .eq(RentalOrder::getUserId, userId)
                        .eq(RentalOrder::getStatus, 0));
        if (activeCount > 0) {
            throw new BusinessException("您有未归还的充电宝，请先归还后再租赁");
        }

        PowerBank pb = powerBankMapper.selectById(powerBankId);
        if (pb == null) {
            throw new BusinessException("充电宝不存在");
        }
        if (pb.getStatus() != 0) {
            throw new BusinessException("该充电宝当前不可租赁");
        }

        // 创建订单
        RentalOrder order = new RentalOrder();
        order.setUserId(userId);
        order.setPowerBankId(powerBankId);
        order.setRentalTime(LocalDateTime.now());
        order.setDeposit(DEFAULT_DEPOSIT);
        order.setDepositStatus(1);
        order.setPaymentStatus(0);
        order.setStatus(0);
        order.setOvertime(0);
        rentalOrderMapper.insert(order);

        // 更新充电宝状态为占用
        pb.setStatus(1);
        powerBankMapper.updateById(pb);

        log.info("租赁成功: userId={}, powerBankId={}, orderId={}", userId, powerBankId, order.getId());
        return order;
    }

    @Transactional
    public RentalOrder returnPowerBank(Long orderId, Long userId) {
        RentalOrder order = rentalOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("该订单已归还");
        }

        LocalDateTime now = LocalDateTime.now();
        order.setReturnTime(now);
        order.setStatus(1);

        // 计算费用
        PowerBank pb = powerBankMapper.selectById(order.getPowerBankId());
        Duration duration = Duration.between(order.getRentalTime(), now);
        long minutes = duration.toMinutes();
        // 不足1小时按1小时计算，最少收费1小时
        long hours = minutes <= 0 ? 1 : (minutes + 59) / 60;
        BigDecimal fee = pb.getHourlyRate().multiply(BigDecimal.valueOf(hours));
        order.setTotalFee(fee.setScale(2, RoundingMode.HALF_UP));

        // 超时判断并更新信誉分
        User user = userMapper.selectById(userId);
        int creditChange = 0;
        
        if (hours > MAX_RENTAL_HOURS) {
            // 超时归还，扣除信誉分
            order.setOvertime(1);
            order.setStatus(2);
            creditChange = -CREDIT_OVERTIME_PENALTY;
            log.info("超时归还: orderId={}, 扣除信誉分 {} 分", orderId, CREDIT_OVERTIME_PENALTY);
        } else {
            // 按时归还，增加信誉分
            creditChange = CREDIT_ON_TIME_RETURN;
            log.info("按时归还: orderId={}, 增加信誉分 {} 分", orderId, CREDIT_ON_TIME_RETURN);
        }
        
        // 更新用户信誉分（限制在 0-100 范围内）
        int newCreditScore = Math.max(CREDIT_MIN, Math.min(CREDIT_MAX, user.getCreditScore() + creditChange));
        user.setCreditScore(newCreditScore);
        userMapper.updateById(user);

        rentalOrderMapper.updateById(order);

        // 释放充电宝
        pb.setStatus(0);
        powerBankMapper.updateById(pb);

        log.info("归还成功: orderId={}, 时长={}小时, 费用={}, 信誉分变化={}, 当前信誉分={}", 
                orderId, hours, order.getTotalFee(), creditChange, newCreditScore);
        return order;
    }

    @Transactional
    public void pay(Long orderId, Long userId) {
        RentalOrder order = rentalOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        if (order.getPaymentStatus() == 1) {
            throw new BusinessException("该订单已支付");
        }
        if (order.getStatus() == 0) {
            throw new BusinessException("请先归还充电宝再支付");
        }
        order.setPaymentStatus(1);
        order.setDepositStatus(2); // 退还押金
        rentalOrderMapper.updateById(order);
        log.info("支付成功: orderId={}", orderId);
    }

    public List<RentalOrder> getMyOrders(Long userId) {
        return rentalOrderMapper.selectOrdersByUserId(userId);
    }

    public List<RentalOrder> getCurrentOrders(Long userId) {
        return rentalOrderMapper.selectCurrentOrders(userId);
    }

    public IPage<RentalOrder> listAll(int page, int size) {
        return rentalOrderMapper.selectAllOrders(new Page<>(page, size));
    }

    public List<RentalOrder> searchByUserId(Long userId) {
        return rentalOrderMapper.searchByUserId(userId);
    }
}
