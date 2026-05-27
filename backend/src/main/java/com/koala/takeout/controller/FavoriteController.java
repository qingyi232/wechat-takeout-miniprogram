package com.koala.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Favorite;
import com.koala.takeout.entity.Merchant;
import com.koala.takeout.mapper.FavoriteMapper;
import com.koala.takeout.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private MerchantMapper merchantMapper;

    @GetMapping("/list")
    public Result<List<Merchant>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Favorite> favorites = favoriteMapper.selectList(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime));
        List<Merchant> merchants = new ArrayList<>();
        for (Favorite f : favorites) {
            Merchant m = merchantMapper.selectById(f.getMerchantId());
            if (m != null) merchants.add(m);
        }
        return Result.success(merchants);
    }

    @PostMapping("/{merchantId}")
    public Result<Void> add(@PathVariable Long merchantId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Favorite existing = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getMerchantId, merchantId));
        if (existing == null) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setMerchantId(merchantId);
            favoriteMapper.insert(favorite);
        }
        return Result.success();
    }

    @DeleteMapping("/{merchantId}")
    public Result<Void> remove(@PathVariable Long merchantId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteMapper.delete(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getMerchantId, merchantId));
        return Result.success();
    }

    @GetMapping("/check/{merchantId}")
    public Result<Boolean> check(@PathVariable Long merchantId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getMerchantId, merchantId));
        return Result.success(count > 0);
    }
}
