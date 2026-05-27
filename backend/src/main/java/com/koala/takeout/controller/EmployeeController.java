package com.koala.takeout.controller;

import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Employee;
import com.koala.takeout.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/list/{merchantId}")
    public Result<List<Employee>> list(@PathVariable Long merchantId) {
        return Result.success(employeeService.listByMerchant(merchantId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Employee employee) {
        employeeService.save(employee);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Employee employee) {
        employeeService.update(employee);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        employeeService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success();
    }
}
