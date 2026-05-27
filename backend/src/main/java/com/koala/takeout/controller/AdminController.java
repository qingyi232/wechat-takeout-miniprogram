package com.koala.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Admin;
import com.koala.takeout.entity.Merchant;
import com.koala.takeout.entity.SystemConfig;
import com.koala.takeout.mapper.AdminMapper;
import com.koala.takeout.mapper.MerchantMapper;
import com.koala.takeout.mapper.SystemConfigMapper;
import com.koala.takeout.service.MerchantService;
import com.koala.takeout.service.OrderService;
import com.koala.takeout.utils.JwtUtil;
import com.koala.takeout.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, username)
                        .eq(Admin::getPassword, md5Pwd));
        if (admin == null) {
            return Result.error("账号或密码错误");
        }
        String token = jwtUtil.generateToken(admin.getId(), Constants.ROLE_ADMIN);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("admin", admin);
        return Result.success(result);
    }

    @GetMapping("/config/list")
    public Result<List<SystemConfig>> configList() {
        return Result.success(systemConfigMapper.selectList(null));
    }

    @PutMapping("/config")
    public Result<Void> updateConfig(@RequestBody SystemConfig config) {
        systemConfigMapper.updateById(config);
        return Result.success();
    }

    @PutMapping("/merchant/audit/{id}")
    public Result<Void> auditMerchant(@PathVariable Long id, @RequestParam Integer status) {
        merchantService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/stats")
    public Result<Map<String, Object>> platformStats() {
        Map<String, Object> stats = new HashMap<>();
        Long merchantCount = merchantMapper.selectCount(
                new LambdaQueryWrapper<Merchant>().eq(Merchant::getStatus, 1));
        stats.put("merchantCount", merchantCount);
        stats.put("orderStats", orderService.adminOrders(1, 1, null, null).getTotal());
        return Result.success(stats);
    }
}
