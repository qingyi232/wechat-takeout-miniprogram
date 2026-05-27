package com.koala.takeout.controller;

import com.koala.takeout.common.PageResult;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Combo;
import com.koala.takeout.entity.ComboDish;
import com.koala.takeout.service.ComboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/combo")
public class ComboController {

    @Autowired
    private ComboService comboService;

    @GetMapping("/list/{merchantId}")
    public Result<List<Combo>> list(@PathVariable Long merchantId) {
        return Result.success(comboService.listByMerchant(merchantId));
    }

    @GetMapping("/detail/{id}")
    public Result<Combo> detail(@PathVariable Long id) {
        return Result.success(comboService.getById(id));
    }

    @GetMapping("/dishes/{comboId}")
    public Result<List<ComboDish>> dishes(@PathVariable Long comboId) {
        return Result.success(comboService.getComboDishes(comboId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Map<String, Object> params) {
        Combo combo = new Combo();
        combo.setMerchantId(Long.parseLong(params.get("merchantId").toString()));
        combo.setName((String) params.get("name"));
        combo.setDescription((String) params.get("description"));
        combo.setImage((String) params.get("image"));
        combo.setPrice(new java.math.BigDecimal(params.get("price").toString()));
        combo.setCategoryId(params.get("categoryId") != null ? Long.parseLong(params.get("categoryId").toString()) : null);
        combo.setStatus(1);
        List<Map<String, Object>> dishList = (List<Map<String, Object>>) params.get("dishes");
        List<ComboDish> dishes = new java.util.ArrayList<>();
        if (dishList != null) {
            for (Map<String, Object> d : dishList) {
                ComboDish cd = new ComboDish();
                cd.setDishId(Long.parseLong(d.get("dishId").toString()));
                cd.setQuantity(Integer.parseInt(d.getOrDefault("quantity", "1").toString()));
                dishes.add(cd);
            }
        }
        comboService.save(combo, dishes);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Map<String, Object> params) {
        Combo combo = new Combo();
        combo.setId(Long.parseLong(params.get("id").toString()));
        combo.setName((String) params.get("name"));
        combo.setDescription((String) params.get("description"));
        combo.setImage((String) params.get("image"));
        combo.setPrice(new java.math.BigDecimal(params.get("price").toString()));
        List<Map<String, Object>> dishList = (List<Map<String, Object>>) params.get("dishes");
        List<ComboDish> dishes = new java.util.ArrayList<>();
        if (dishList != null) {
            for (Map<String, Object> d : dishList) {
                ComboDish cd = new ComboDish();
                cd.setDishId(Long.parseLong(d.get("dishId").toString()));
                cd.setQuantity(Integer.parseInt(d.getOrDefault("quantity", "1").toString()));
                dishes.add(cd);
            }
        }
        comboService.update(combo, dishes);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        comboService.delete(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        comboService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<Combo>> page(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(comboService.page(merchantId, pageNum, pageSize));
    }
}
