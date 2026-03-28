package com.powerbank.controller;

import com.powerbank.common.Result;
import com.powerbank.config.RequireAdmin;
import com.powerbank.dto.PowerBankDTO;
import com.powerbank.entity.PowerBank;
import com.powerbank.service.PowerBankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/powerbank")
@RequiredArgsConstructor
public class PowerBankController {

    private final PowerBankService powerBankService;

    @GetMapping("/available")
    public Result<List<PowerBank>> getAvailable() {
        return Result.success(powerBankService.getAvailable());
    }

    @GetMapping("/available/count")
    public Result<Long> getAvailableCount() {
        return Result.success(powerBankService.countAvailable());
    }

    @RequireAdmin
    @GetMapping("/list")
    public Result<?> listAll(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(powerBankService.listAll(page, size));
    }

    @GetMapping("/search")
    public Result<PowerBank> searchByCode(@RequestParam String code) {
        return Result.success(powerBankService.getByCode(code));
    }

    @RequireAdmin
    @PostMapping
    public Result<Void> add(@Valid @RequestBody PowerBankDTO dto) {
        powerBankService.add(dto);
        return Result.success();
    }

    @RequireAdmin
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PowerBankDTO dto) {
        powerBankService.update(id, dto);
        return Result.success();
    }

    @RequireAdmin
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        powerBankService.updateStatus(id, status);
        return Result.success();
    }
}
