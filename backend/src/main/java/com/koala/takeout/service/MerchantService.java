package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.koala.takeout.common.Constants;
import com.koala.takeout.common.PageResult;
import com.koala.takeout.entity.Merchant;
import com.koala.takeout.mapper.MerchantMapper;
import com.koala.takeout.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public List<Merchant> list(String keyword, String categoryType, String sortBy) {
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Merchant::getStatus, 1);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Merchant::getName, keyword)
                    .or().like(Merchant::getDescription, keyword));
        }
        if (StringUtils.hasText(categoryType)) {
            wrapper.eq(Merchant::getCategoryType, categoryType);
        }
        switch (sortBy != null ? sortBy : "default") {
            case "speed":
                wrapper.orderByAsc(Merchant::getDeliveryTime);
                break;
            case "rating":
                wrapper.orderByDesc(Merchant::getRating);
                break;
            case "sales":
                wrapper.orderByDesc(Merchant::getMonthlySales);
                break;
            default:
                wrapper.orderByDesc(Merchant::getMonthlySales);
                break;
        }
        return merchantMapper.selectList(wrapper);
    }

    public Merchant getById(Long id) {
        return merchantMapper.selectById(id);
    }

    public Map<String, Object> login(String account, String password) {
        String md5Pwd = DigestUtils.md5DigestAsHex(password.getBytes());
        Merchant merchant = merchantMapper.selectOne(
                new LambdaQueryWrapper<Merchant>()
                        .eq(Merchant::getAccount, account)
                        .eq(Merchant::getPassword, md5Pwd));
        if (merchant == null) {
            return null;
        }
        String token = jwtUtil.generateToken(merchant.getId(), Constants.ROLE_MERCHANT);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("merchant", merchant);
        return result;
    }

    public void save(Merchant merchant) {
        if (StringUtils.hasText(merchant.getPassword())) {
            merchant.setPassword(DigestUtils.md5DigestAsHex(merchant.getPassword().getBytes()));
        }
        merchantMapper.insert(merchant);
    }

    public void update(Merchant merchant) {
        merchantMapper.updateById(merchant);
    }

    public void updateStatus(Long id, Integer status) {
        Merchant merchant = new Merchant();
        merchant.setId(id);
        merchant.setStatus(status);
        merchantMapper.updateById(merchant);
    }

    public PageResult<Merchant> page(int pageNum, int pageSize, String keyword, Integer status) {
        Page<Merchant> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Merchant> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Merchant::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Merchant::getStatus, status);
        }
        wrapper.orderByDesc(Merchant::getCreateTime);
        Page<Merchant> result = merchantMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }
}
