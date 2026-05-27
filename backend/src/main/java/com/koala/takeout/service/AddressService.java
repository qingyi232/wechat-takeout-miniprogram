package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.entity.Address;
import com.koala.takeout.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    public List<Address> listByUser(Long userId) {
        return addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .orderByDesc(Address::getIsDefault)
                        .orderByDesc(Address::getCreateTime));
    }

    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }

    public void save(Address address) {
        if (address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        addressMapper.insert(address);
    }

    public void update(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            clearDefault(address.getUserId());
        }
        addressMapper.updateById(address);
    }

    public void delete(Long id) {
        addressMapper.deleteById(id);
    }

    public Address getDefault(Long userId) {
        return addressMapper.selectOne(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, 1));
    }

    private void clearDefault(Long userId) {
        List<Address> list = addressMapper.selectList(
                new LambdaQueryWrapper<Address>()
                        .eq(Address::getUserId, userId)
                        .eq(Address::getIsDefault, 1));
        for (Address addr : list) {
            addr.setIsDefault(0);
            addressMapper.updateById(addr);
        }
    }
}
