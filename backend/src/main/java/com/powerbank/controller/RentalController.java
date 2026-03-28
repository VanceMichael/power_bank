package com.powerbank.controller;

import com.powerbank.common.Result;
import com.powerbank.config.RequireAdmin;
import com.powerbank.entity.RentalOrder;
import com.powerbank.service.RentalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @PostMapping("/rent")
    public Result<RentalOrder> rent(HttpServletRequest request, @RequestParam Long powerBankId) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(rentalService.rent(userId, powerBankId));
    }

    @PutMapping("/return/{id}")
    public Result<RentalOrder> returnPowerBank(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(rentalService.returnPowerBank(id, userId));
    }

    @PutMapping("/pay/{id}")
    public Result<Void> pay(HttpServletRequest request, @PathVariable Long id) {
        Long userId = (Long) request.getAttribute("userId");
        rentalService.pay(id, userId);
        return Result.success();
    }

    @GetMapping("/my-orders")
    public Result<List<RentalOrder>> myOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(rentalService.getMyOrders(userId));
    }

    @GetMapping("/current")
    public Result<List<RentalOrder>> currentOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(rentalService.getCurrentOrders(userId));
    }

    @RequireAdmin
    @GetMapping("/list")
    public Result<?> listAll(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(rentalService.listAll(page, size));
    }

    @RequireAdmin
    @GetMapping("/search")
    public Result<?> search(@RequestParam Long userId) {
        return Result.success(rentalService.searchByUserId(userId));
    }
}
