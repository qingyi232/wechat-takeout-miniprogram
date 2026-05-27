package com.koala.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.koala.takeout.common.Constants;
import com.koala.takeout.common.PageResult;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Merchant;
import com.koala.takeout.entity.MerchantApply;
import com.koala.takeout.mapper.MerchantApplyMapper;
import com.koala.takeout.mapper.MerchantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/merchant-apply")
public class MerchantApplyController {

    @Autowired
    private MerchantApplyMapper merchantApplyMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody MerchantApply apply, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        LambdaQueryWrapper<MerchantApply> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(MerchantApply::getUserId, userId)
                    .in(MerchantApply::getStatus, 0, 1);
        Long existCount = merchantApplyMapper.selectCount(checkWrapper);
        if (existCount > 0) {
            return Result.error("您已有待审核或已通过的申请");
        }
        apply.setUserId(userId);
        apply.setStatus(0);
        apply.setMerchantAccount(null);
        apply.setMerchantPassword(null);
        apply.setMerchantId(null);
        merchantApplyMapper.insert(apply);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<MerchantApply> myApply(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        MerchantApply apply = merchantApplyMapper.selectOne(
                new LambdaQueryWrapper<MerchantApply>()
                        .eq(MerchantApply::getUserId, userId)
                        .orderByDesc(MerchantApply::getCreateTime)
                        .last("LIMIT 1"));
        return Result.success(apply);
    }

    @GetMapping("/admin/list")
    public Result<PageResult<MerchantApply>> adminList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        Page<MerchantApply> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MerchantApply> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(MerchantApply::getStatus, status);
        }
        wrapper.orderByDesc(MerchantApply::getCreateTime);
        Page<MerchantApply> result = merchantApplyMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @PutMapping("/admin/audit/{id}")
    public Result<Void> audit(@PathVariable Long id,
                              @RequestParam Integer status,
                              @RequestParam(required = false) String rejectReason) {
        MerchantApply apply = merchantApplyMapper.selectById(id);
        if (apply == null) {
            return Result.error("申请不存在");
        }
        if (apply.getStatus() != 0) {
            return Result.error("该申请已处理");
        }

        if (status == 1) {
            String account = "m_" + apply.getPhone();
            String password = UUID.randomUUID().toString().substring(0, 8);
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

            Merchant merchant = new Merchant();
            merchant.setName(apply.getName());
            merchant.setLogo(apply.getLogo());
            merchant.setDescription(apply.getDescription());
            merchant.setAddress(apply.getAddress());
            merchant.setLatitude(apply.getLatitude());
            merchant.setLongitude(apply.getLongitude());
            merchant.setPhone(apply.getPhone());
            merchant.setCategoryType(apply.getCategoryType());
            merchant.setBusinessHours(apply.getBusinessHours() != null ? apply.getBusinessHours() : "08:00-22:00");
            merchant.setAccount(account);
            merchant.setPassword(md5Password);
            merchant.setStatus(1);
            merchantMapper.insert(merchant);

            apply.setStatus(1);
            apply.setMerchantAccount(account);
            apply.setMerchantPassword(password);
            apply.setMerchantId(merchant.getId());
        } else {
            apply.setStatus(2);
            apply.setRejectReason(rejectReason != null ? rejectReason : "不符合入驻条件");
        }

        merchantApplyMapper.updateById(apply);
        return Result.success();
    }
}
