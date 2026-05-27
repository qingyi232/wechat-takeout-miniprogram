package com.koala.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.koala.takeout.common.PageResult;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Announcement;
import com.koala.takeout.mapper.AnnouncementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement")
public class AnnouncementController {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @GetMapping("/list")
    public Result<List<Announcement>> listActive() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1).orderByDesc(Announcement::getCreateTime);
        return Result.success(announcementMapper.selectList(wrapper));
    }

    @GetMapping("/page")
    public Result<PageResult<Announcement>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (type != null) wrapper.eq(Announcement::getType, type);
        if (status != null) wrapper.eq(Announcement::getStatus, status);
        wrapper.orderByDesc(Announcement::getCreateTime);
        Page<Announcement> result = announcementMapper.selectPage(page, wrapper);
        return Result.success(new PageResult<>(result.getTotal(), result.getRecords()));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Announcement announcement) {
        announcementMapper.insert(announcement);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Announcement announcement) {
        announcementMapper.updateById(announcement);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementMapper.deleteById(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        Announcement a = new Announcement();
        a.setId(id);
        a.setStatus(status);
        announcementMapper.updateById(a);
        return Result.success();
    }
}
