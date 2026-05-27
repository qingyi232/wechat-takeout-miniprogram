package com.koala.takeout.controller;

import com.koala.takeout.common.PageResult;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Merchant;
import com.koala.takeout.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @GetMapping("/info")
    public Result<Merchant> info(HttpServletRequest request) {
        Long merchantId = (Long) request.getAttribute("userId");
        return Result.success(merchantService.getById(merchantId));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody Merchant merchant) {
        merchantService.save(merchant);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Merchant>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryType,
            @RequestParam(required = false, defaultValue = "default") String sortBy) {
        return Result.success(merchantService.list(keyword, categoryType, sortBy));
    }

    @GetMapping("/detail/{id}")
    public Result<Merchant> detail(@PathVariable Long id) {
        return Result.success(merchantService.getById(id));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        Map<String, Object> result = merchantService.login(account, password);
        if (result == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(result);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Merchant merchant) {
        merchantService.update(merchant);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<Merchant>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return Result.success(merchantService.page(pageNum, pageSize, keyword, status));
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        merchantService.updateStatus(id, status);
        return Result.success();
    }
}
