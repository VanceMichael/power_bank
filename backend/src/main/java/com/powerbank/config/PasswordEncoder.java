package com.powerbank.config;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码编码器 - 使用 BCrypt 算法
 * BCrypt 自动处理盐值，每次加密结果不同但可正确验证
 */
@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(10);

    public String encode(String rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        return bcrypt.matches(rawPassword, encodedPassword);
    }
}
