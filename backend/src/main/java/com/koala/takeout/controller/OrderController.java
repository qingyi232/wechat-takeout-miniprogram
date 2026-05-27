package com.koala.takeout.controller;

import com.koala.takeout.common.PageResult;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Orders;
import com.koala.takeout.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public Result<Orders> createOrder(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long merchantId = Long.parseLong(params.get("merchantId").toString());
        Long addressId = params.get("addressId") != null ? Long.parseLong(params.get("addressId").toString()) : null;
        String addressDetail = (String) params.get("addressDetail");
        String contactName = (String) params.get("contactName");
        String contactPhone = (String) params.get("contactPhone");
        String remark = (String) params.get("remark");
        List<Map<String, Object>> items = (List<Map<String, Object>>) params.get("items");
        Orders order = orderService.createOrder(userId, merchantId, addressId, addressDetail, contactName, contactPhone, remark, items);
        return Result.success(order);
    }

    @PutMapping("/pay/{orderId}")
    public Result<Void> payOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.payOrder(orderId, userId);
        return Result.success();
    }

    @PutMapping("/status/{orderId}")
    public Result<Void> updateStatus(@PathVariable Long orderId, @RequestParam Integer status) {
        orderService.updateStatus(orderId, status);
        return Result.success();
    }

    @GetMapping("/user/list")
    public Result<List<Map<String, Object>>> userOrders(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.getUserOrders(userId, status));
    }

    @GetMapping("/detail/{orderId}")
    public Result<Map<String, Object>> orderDetail(@PathVariable Long orderId) {
        return Result.success(orderService.getOrderDetail(orderId));
    }

    @GetMapping("/merchant/list")
    public Result<PageResult<Orders>> merchantOrders(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.merchantOrders(merchantId, pageNum, pageSize, status));
    }

    @GetMapping("/merchant/stats")
    public Result<Map<String, Object>> merchantStats(@RequestParam Long merchantId) {
        return Result.success(orderService.getMerchantStats(merchantId));
    }

    @GetMapping("/merchant/recent")
    public Result<List<Map<String, Object>>> recentStats(@RequestParam Long merchantId) {
        return Result.success(orderService.getRecentStats(merchantId));
    }

    @GetMapping("/admin/list")
    public Result<PageResult<Orders>> adminOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long merchantId) {
        return Result.success(orderService.adminOrders(pageNum, pageSize, status, merchantId));
    }
}
