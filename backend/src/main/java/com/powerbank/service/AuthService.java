package com.powerbank.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.powerbank.common.BusinessException;
import com.powerbank.config.JwtUtil;
import com.powerbank.config.PasswordEncoder;
import com.powerbank.dto.LoginDTO;
import com.powerbank.dto.RegisterDTO;
import com.powerbank.entity.User;
import com.powerbank.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole());
        result.put("realName", user.getRealName());
        log.info("用户登录成功: {}", user.getUsername());
        return result;
    }

    public void register(RegisterDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        Long idCardCount = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getIdCard, dto.getIdCard()));
        if (idCardCount > 0) {
            throw new BusinessException("该身份证号已被注册");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setIdCard(dto.getIdCard());
        user.setPhone(dto.getPhone());
        user.setRole(0); // 只允许注册普通用户，管理员通过数据库预置
        user.setCreditScore(100);
        userMapper.insert(user);
        log.info("新用户注册: {}", dto.getUsername());
    }
}
