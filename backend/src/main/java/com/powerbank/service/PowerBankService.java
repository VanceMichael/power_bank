package com.powerbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powerbank.common.BusinessException;
import com.powerbank.dto.PowerBankDTO;
import com.powerbank.entity.PowerBank;
import com.powerbank.mapper.PowerBankMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PowerBankService {

    private final PowerBankMapper powerBankMapper;

    public List<PowerBank> getAvailable() {
        return powerBankMapper.selectList(
                new LambdaQueryWrapper<PowerBank>().eq(PowerBank::getStatus, 0));
    }

    public long countAvailable() {
        return powerBankMapper.selectCount(
                new LambdaQueryWrapper<PowerBank>().eq(PowerBank::getStatus, 0));
    }

    public IPage<PowerBank> listAll(int page, int size) {
        return powerBankMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<PowerBank>().orderByAsc(PowerBank::getCode));
    }

    public PowerBank getByCode(String code) {
        PowerBank pb = powerBankMapper.selectOne(
                new LambdaQueryWrapper<PowerBank>().eq(PowerBank::getCode, code));
        if (pb == null) {
            throw new BusinessException("充电宝不存在: " + code);
        }
        return pb;
    }

    @Transactional
    public void add(PowerBankDTO dto) {
        Long count = powerBankMapper.selectCount(
                new LambdaQueryWrapper<PowerBank>().eq(PowerBank::getCode, dto.getCode()));
        if (count > 0) {
            throw new BusinessException("充电宝编号已存在");
        }
        PowerBank pb = new PowerBank();
        pb.setCode(dto.getCode());
        pb.setHourlyRate(dto.getHourlyRate());
        pb.setStatus(0);
        powerBankMapper.insert(pb);
        log.info("新增充电宝: {}", dto.getCode());
    }

    @Transactional
    public void update(Long id, PowerBankDTO dto) {
        PowerBank pb = powerBankMapper.selectById(id);
        if (pb == null) {
            throw new BusinessException("充电宝不存在");
        }
        if (pb.getStatus() == 1) {
            throw new BusinessException("占用中的充电宝不可编辑");
        }
        // 检查编号是否被其他充电宝占用
        PowerBank byCode = powerBankMapper.selectOne(
                new LambdaQueryWrapper<PowerBank>().eq(PowerBank::getCode, dto.getCode()));
        if (byCode != null && !byCode.getId().equals(id)) {
            throw new BusinessException("充电宝编号已被占用");
        }
        pb.setCode(dto.getCode());
        pb.setHourlyRate(dto.getHourlyRate());
        powerBankMapper.updateById(pb);
        log.info("更新充电宝: id={}", id);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        PowerBank pb = powerBankMapper.selectById(id);
        if (pb == null) {
            throw new BusinessException("充电宝不存在");
        }
        if (pb.getStatus() == 1) {
            throw new BusinessException("占用中的充电宝不可变更状态");
        }
        if (status < 0 || status > 2) {
            throw new BusinessException("无效的状态值");
        }
        pb.setStatus(status);
        powerBankMapper.updateById(pb);
        log.info("充电宝状态变更: id={}, status={}", id, status);
    }
}
