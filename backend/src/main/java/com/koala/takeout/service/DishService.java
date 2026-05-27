package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.koala.takeout.common.PageResult;
import com.koala.takeout.entity.Dish;
import com.koala.takeout.mapper.DishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class DishService {

    @Autowired
    private DishMapper dishMapper;

    public List<Dish> listByMerchant(Long merchantId, Long categoryId) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId);
        wrapper.eq(Dish::getStatus, 1);
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Dish::getSales);
        return dishMapper.selectList(wrapper);
    }

    public Dish getById(Long id) {
        return dishMapper.selectById(id);
    }

    public void save(Dish dish) {
        dishMapper.insert(dish);
    }

    public void update(Dish dish) {
        dishMapper.updateById(dish);
    }

    public void delete(Long id) {
        dishMapper.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateById(dish);
    }

    public PageResult<Dish> page(Long merchantId, int pageNum, int pageSize, String keyword, Long categoryId) {
        Page<Dish> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getMerchantId, merchantId);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Dish::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Dish::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Dish::getCreateTime);
        Page<Dish> result = dishMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }
}
