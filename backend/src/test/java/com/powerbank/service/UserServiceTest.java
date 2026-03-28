package com.powerbank.service;

import com.powerbank.common.BusinessException;
import com.powerbank.config.PasswordEncoder;
import com.powerbank.dto.UpdatePasswordDTO;
import com.powerbank.entity.User;
import com.powerbank.mapper.UserMapper;
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
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("获取用户信息 - 用户不存在应抛出异常")
    void getInfo_userNotFound_shouldThrow() {
        when(userMapper.selectById(99L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> userService.getById(99L));
    }

    @Test
    @DisplayName("获取用户信息 - 正常返回")
    void getInfo_validUser_shouldReturn() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRealName("张三");
        user.setCreditScore(100);

        when(userMapper.selectById(1L)).thenReturn(user);
        User result = userService.getById(1L);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getPassword()); // 密码应被清空
    }

    @Test
    @DisplayName("修改密码 - 原密码错误应抛出异常")
    void updatePassword_wrongOldPassword_shouldThrow() {
        User user = new User();
        user.setId(1L);
        user.setPassword("hashedCorrectPassword");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches("wrongOld", "hashedCorrectPassword")).thenReturn(false);

        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("wrongOld");
        dto.setNewPassword("newpass123");

        assertThrows(BusinessException.class, () -> userService.updatePassword(1L, dto));
    }

    @Test
    @DisplayName("修改密码 - 正常修改应成功")
    void updatePassword_validInput_shouldSucceed() {
        User user = new User();
        user.setId(1L);
        user.setPassword("hashedOldPassword");

        when(userMapper.selectById(1L)).thenReturn(user);
        when(passwordEncoder.matches("oldpass", "hashedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newpass123")).thenReturn("hashedNewPassword");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        UpdatePasswordDTO dto = new UpdatePasswordDTO();
        dto.setOldPassword("oldpass");
        dto.setNewPassword("newpass123");

        assertDoesNotThrow(() -> userService.updatePassword(1L, dto));
        verify(userMapper).updateById(any(User.class));
    }
}
