package com.koala.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.DishSpec;
import com.koala.takeout.entity.DishSpecGroup;
import com.koala.takeout.mapper.DishSpecGroupMapper;
import com.koala.takeout.mapper.DishSpecMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dish-spec")
public class DishSpecController {

    @Autowired
    private DishSpecGroupMapper specGroupMapper;

    @Autowired
    private DishSpecMapper specMapper;

    @GetMapping("/dish/{dishId}")
    public Result<List<Map<String, Object>>> getByDish(@PathVariable Long dishId) {
        List<DishSpecGroup> groups = specGroupMapper.selectList(
                new LambdaQueryWrapper<DishSpecGroup>()
                        .eq(DishSpecGroup::getDishId, dishId)
                        .orderByAsc(DishSpecGroup::getSortOrder));
        if (groups.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<Long> groupIds = groups.stream().map(DishSpecGroup::getId).collect(Collectors.toList());
        List<DishSpec> specs = specMapper.selectList(
                new LambdaQueryWrapper<DishSpec>()
                        .in(DishSpec::getGroupId, groupIds)
                        .orderByAsc(DishSpec::getSortOrder));
        Map<Long, List<DishSpec>> specMap = specs.stream()
                .collect(Collectors.groupingBy(DishSpec::getGroupId));
        List<Map<String, Object>> result = new ArrayList<>();
        for (DishSpecGroup group : groups) {
            Map<String, Object> groupData = new HashMap<>();
            groupData.put("id", group.getId());
            groupData.put("groupName", group.getGroupName());
            groupData.put("required", group.getRequired());
            groupData.put("specs", specMap.getOrDefault(group.getId(), Collections.emptyList()));
            result.add(groupData);
        }
        return Result.success(result);
    }

    @PostMapping("/group")
    public Result<Void> saveGroup(@RequestBody DishSpecGroup group) {
        specGroupMapper.insert(group);
        return Result.success();
    }

    @PostMapping
    public Result<Void> saveSpec(@RequestBody DishSpec spec) {
        specMapper.insert(spec);
        return Result.success();
    }

    @DeleteMapping("/group/{id}")
    public Result<Void> deleteGroup(@PathVariable Long id) {
        specGroupMapper.deleteById(id);
        specMapper.delete(new LambdaQueryWrapper<DishSpec>().eq(DishSpec::getGroupId, id));
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSpec(@PathVariable Long id) {
        specMapper.deleteById(id);
        return Result.success();
    }
}
