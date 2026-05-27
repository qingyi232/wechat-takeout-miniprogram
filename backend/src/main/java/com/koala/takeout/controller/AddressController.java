package com.koala.takeout.controller;

import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Address;
import com.koala.takeout.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(addressService.listByUser(userId));
    }

    @GetMapping("/default")
    public Result<Address> getDefault(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(addressService.getDefault(userId));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Address address, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        address.setUserId(userId);
        addressService.save(address);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Address address) {
        addressService.update(address);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return Result.success();
    }
}
