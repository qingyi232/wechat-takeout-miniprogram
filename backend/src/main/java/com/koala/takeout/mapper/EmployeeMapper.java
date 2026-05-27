package com.koala.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.koala.takeout.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
