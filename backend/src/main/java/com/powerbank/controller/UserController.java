package com.powerbank.controller;

import com.powerbank.common.Result;
import com.powerbank.config.RequireAdmin;
import com.powerbank.dto.UpdatePasswordDTO;
import com.powerbank.dto.UpdateProfileDTO;
import com.powerbank.entity.User;
import com.powerbank.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public Result<User> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getById(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @Valid @RequestBody UpdateProfileDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> updatePassword(HttpServletRequest request, @Valid @RequestBody UpdatePasswordDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, dto);
        return Result.success();
    }

    @RequireAdmin
    @GetMapping("/list")
    public Result<?> listUsers(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.listUsers(page, size));
    }

    @RequireAdmin
    @GetMapping("/search")
    public Result<?> searchUsers(@RequestParam String field,
                                 @RequestParam String keyword,
                                 @RequestParam(defaultValue = "1") int page,
                                 @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.searchUsers(field, keyword, page, size));
    }

    @RequireAdmin
    @PostMapping
    public Result<Void> createUser(@Valid @RequestBody User user) {
        userService.createUser(user);
        return Result.success();
    }

    @RequireAdmin
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        userService.updateUser(id, user);
        return Result.success();
    }

    @RequireAdmin
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
