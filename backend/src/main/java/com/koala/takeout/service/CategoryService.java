package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.entity.Category;
import com.koala.takeout.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> listByMerchant(Long merchantId) {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .eq(Category::getMerchantId, merchantId)
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort));
    }

    public void save(Category category) {
        categoryMapper.insert(category);
    }

    public void update(Category category) {
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
