package com.powerbank.service;

import com.powerbank.common.BusinessException;
import com.powerbank.config.PasswordEncoder;
import com.powerbank.dto.LoginDTO;
import com.powerbank.dto.RegisterDTO;
import com.powerbank.entity.User;
import com.powerbank.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("认证服务测试")
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterDTO registerDTO;

    @BeforeEach
    void setUp() {
        registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setPassword("pass123");
        registerDTO.setConfirmPassword("pass123");
        registerDTO.setRealName("张三");
        registerDTO.setIdCard("110101199501011234");
        registerDTO.setPhone("13900001111");
    }

    @Test
    @DisplayName("注册 - 两次密码不一致应抛出异常")
    void register_passwordMismatch_shouldThrow() {
        registerDTO.setConfirmPassword("different");
        assertThrows(BusinessException.class, () -> authService.register(registerDTO));
    }

    @Test
    @DisplayName("注册 - 用户名已存在应抛出异常")
    void register_duplicateUsername_shouldThrow() {
        when(userMapper.selectCount(any())).thenReturn(1L);
        assertThrows(BusinessException.class, () -> authService.register(registerDTO));
    }

    @Test
    @DisplayName("注册 - 正常注册应成功")
    void register_validInput_shouldSucceed() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.insert(any(User.class))).thenReturn(1);

        assertDoesNotThrow(() -> authService.register(registerDTO));
        verify(userMapper).insert(any(User.class));
    }

    @Test
    @DisplayName("登录 - 用户不存在应抛出异常")
    void login_userNotFound_shouldThrow() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("nonexistent");
        loginDTO.setPassword("pass123");

        when(userMapper.selectOne(any())).thenReturn(null);
        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    }

    @Test
    @DisplayName("登录 - 密码错误应抛出异常")
    void login_wrongPassword_shouldThrow() {
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("wrongpass");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedCorrectPassword");

        when(userMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches("wrongpass", "hashedCorrectPassword")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(loginDTO));
    }
}
