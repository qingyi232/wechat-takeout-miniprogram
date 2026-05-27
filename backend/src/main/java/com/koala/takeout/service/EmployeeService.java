package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.entity.Employee;
import com.koala.takeout.mapper.EmployeeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    public List<Employee> listByMerchant(Long merchantId) {
        return employeeMapper.selectList(
                new LambdaQueryWrapper<Employee>()
                        .eq(Employee::getMerchantId, merchantId)
                        .orderByDesc(Employee::getCreateTime));
    }

    public void save(Employee employee) {
        employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        employeeMapper.insert(employee);
    }

    public void update(Employee employee) {
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        } else {
            employee.setPassword(null);
        }
        employeeMapper.updateById(employee);
    }

    public void updateStatus(Long id, Integer status) {
        Employee emp = new Employee();
        emp.setId(id);
        emp.setStatus(status);
        employeeMapper.updateById(emp);
    }

    public void delete(Long id) {
        employeeMapper.deleteById(id);
    }
}
