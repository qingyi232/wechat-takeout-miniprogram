package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.koala.takeout.common.PageResult;
import com.koala.takeout.entity.Combo;
import com.koala.takeout.entity.ComboDish;
import com.koala.takeout.mapper.ComboMapper;
import com.koala.takeout.mapper.ComboDishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComboService {

    @Autowired
    private ComboMapper comboMapper;
    @Autowired
    private ComboDishMapper comboDishMapper;

    public List<Combo> listByMerchant(Long merchantId) {
        return comboMapper.selectList(
                new LambdaQueryWrapper<Combo>()
                        .eq(Combo::getMerchantId, merchantId)
                        .eq(Combo::getStatus, 1));
    }

    public Combo getById(Long id) {
        return comboMapper.selectById(id);
    }

    public List<ComboDish> getComboDishes(Long comboId) {
        return comboDishMapper.selectList(
                new LambdaQueryWrapper<ComboDish>().eq(ComboDish::getComboId, comboId));
    }

    @Transactional
    public void save(Combo combo, List<ComboDish> dishes) {
        comboMapper.insert(combo);
        for (ComboDish cd : dishes) {
            cd.setComboId(combo.getId());
            comboDishMapper.insert(cd);
        }
    }

    @Transactional
    public void update(Combo combo, List<ComboDish> dishes) {
        comboMapper.updateById(combo);
        comboDishMapper.delete(new LambdaQueryWrapper<ComboDish>().eq(ComboDish::getComboId, combo.getId()));
        if (dishes != null) {
            for (ComboDish cd : dishes) {
                cd.setComboId(combo.getId());
                comboDishMapper.insert(cd);
            }
        }
    }

    public void delete(Long id) {
        comboMapper.deleteById(id);
        comboDishMapper.delete(new LambdaQueryWrapper<ComboDish>().eq(ComboDish::getComboId, id));
    }

    public void updateStatus(Long id, Integer status) {
        Combo combo = new Combo();
        combo.setId(id);
        combo.setStatus(status);
        comboMapper.updateById(combo);
    }

    public PageResult<Combo> page(Long merchantId, int pageNum, int pageSize) {
        Page<Combo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Combo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Combo::getMerchantId, merchantId).orderByDesc(Combo::getCreateTime);
        Page<Combo> result = comboMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }
}
