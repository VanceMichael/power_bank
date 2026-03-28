package com.powerbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.powerbank.common.BusinessException;
import com.powerbank.config.PasswordEncoder;
import com.powerbank.dto.UpdatePasswordDTO;
import com.powerbank.dto.UpdateProfileDTO;
import com.powerbank.entity.User;
import com.powerbank.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    @Transactional
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User existing = userMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        // 检查用户名是否被其他人占用
        User byUsername = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (byUsername != null && !byUsername.getId().equals(userId)) {
            throw new BusinessException("用户名已被占用");
        }
        existing.setUsername(dto.getUsername());
        existing.setRealName(dto.getRealName());
        existing.setIdCard(dto.getIdCard());
        existing.setPhone(dto.getPhone());
        userMapper.updateById(existing);
        log.info("用户信息更新: userId={}", userId);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户修改密码: userId={}", userId);
    }

    public IPage<User> listUsers(int page, int size) {
        Page<User> p = new Page<>(page, size);
        IPage<User> result = userMapper.selectPage(p,
                new LambdaQueryWrapper<User>().eq(User::getRole, 0).orderByDesc(User::getCreatedAt));
        result.getRecords().forEach(u -> {
            u.setPassword(null);
            u.setIdCard(maskIdCard(u.getIdCard()));
            u.setPhone(maskPhone(u.getPhone()));
        });
        return result;
    }

    public IPage<User> searchUsers(String field, String keyword, int page, int size) {
        Page<User> p = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getRole, 0);
        switch (field) {
            case "username" -> wrapper.like(User::getUsername, keyword);
            case "realName" -> wrapper.like(User::getRealName, keyword);
            case "idCard" -> wrapper.like(User::getIdCard, keyword);
            case "phone" -> wrapper.like(User::getPhone, keyword);
            default -> throw new BusinessException("不支持的搜索字段: " + field);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        IPage<User> result = userMapper.selectPage(p, wrapper);
        result.getRecords().forEach(u -> {
            u.setPassword(null);
            u.setIdCard(maskIdCard(u.getIdCard()));
            u.setPhone(maskPhone(u.getPhone()));
        });
        return result;
    }
    
    /**
     * 身份证号脱敏：显示前4位和后4位，中间用*替代
     * 例如：110101199001011234 -> 1101**********1234
     */
    private String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 8) {
            return idCard;
        }
        return idCard.substring(0, 4) + "**********" + idCard.substring(idCard.length() - 4);
    }
    
    /**
     * 手机号脱敏：显示前3位和后4位，中间用*替代
     * 例如：13912345678 -> 139****5678
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * 管理员新增用户
     */
    @Transactional
    public void createUser(User user) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        if (user.getIdCard() != null) {
            Long idCardCount = userMapper.selectCount(
                    new LambdaQueryWrapper<User>().eq(User::getIdCard, user.getIdCard()));
            if (idCardCount > 0) {
                throw new BusinessException("该身份证号已被注册");
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(0); // 只能创建普通用户
        if (user.getCreditScore() == null) {
            user.setCreditScore(100);
        }
        userMapper.insert(user);
        log.info("管理员新增用户: {}", user.getUsername());
    }

    /**
     * 管理员修改用户信息
     */
    @Transactional
    public void updateUser(Long userId, User user) {
        User existing = userMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        if (existing.getRole() == 1) {
            throw new BusinessException("不能修改管理员账户");
        }
        // 检查用户名是否被其他人占用
        if (user.getUsername() != null) {
            User byUsername = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
            if (byUsername != null && !byUsername.getId().equals(userId)) {
                throw new BusinessException("用户名已被占用");
            }
            existing.setUsername(user.getUsername());
        }
        if (user.getRealName() != null) {
            existing.setRealName(user.getRealName());
        }
        if (user.getIdCard() != null) {
            existing.setIdCard(user.getIdCard());
        }
        if (user.getPhone() != null) {
            existing.setPhone(user.getPhone());
        }
        if (user.getCreditScore() != null) {
            existing.setCreditScore(user.getCreditScore());
        }
        userMapper.updateById(existing);
        log.info("管理员修改用户: userId={}", userId);
    }

    /**
     * 管理员删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        User existing = userMapper.selectById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }
        if (existing.getRole() == 1) {
            throw new BusinessException("不能删除管理员账户");
        }
        // 检查用户是否有订单记录（外键约束）
        // 由于存在外键约束，如果用户有订单则无法删除
        // 这里通过捕获异常来处理
        try {
            userMapper.deleteById(userId);
            log.info("管理员删除用户: userId={}", userId);
        } catch (Exception e) {
            log.error("删除用户失败: userId={}, error={}", userId, e.getMessage());
            throw new BusinessException("该用户存在关联订单，无法删除");
        }
    }
}
