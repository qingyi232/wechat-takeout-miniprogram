package com.koala.takeout.controller;

import com.koala.takeout.common.PageResult;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Dish;
import com.koala.takeout.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("/list/{merchantId}")
    public Result<List<Dish>> list(
            @PathVariable Long merchantId,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(dishService.listByMerchant(merchantId, categoryId));
    }

    @GetMapping("/detail/{id}")
    public Result<Dish> detail(@PathVariable Long id) {
        return Result.success(dishService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Dish dish) {
        dishService.save(dish);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Dish dish) {
        dishService.update(dish);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        dishService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<Dish>> page(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(dishService.page(merchantId, pageNum, pageSize, keyword, categoryId));
    }
}
