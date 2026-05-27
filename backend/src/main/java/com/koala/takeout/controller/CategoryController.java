package com.koala.takeout.controller;

import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Category;
import com.koala.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list/{merchantId}")
    public Result<List<Category>> list(@PathVariable Long merchantId) {
        return Result.success(categoryService.listByMerchant(merchantId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
