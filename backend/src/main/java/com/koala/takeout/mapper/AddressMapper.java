package com.koala.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.koala.takeout.entity.Address;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<Address> {
}
