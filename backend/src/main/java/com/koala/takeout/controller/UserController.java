package com.koala.takeout.controller;

import com.koala.takeout.common.Result;
import com.koala.takeout.entity.User;
import com.koala.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String openid = params.get("openid");
        String nickName = params.get("nickName");
        String avatarUrl = params.get("avatarUrl");
        if (openid == null || openid.isEmpty()) {
            return Result.error("openid不能为空");
        }
        Map<String, Object> result = userService.login(openid, nickName, avatarUrl);
        return Result.success(result);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        userService.update(user);
        return Result.success();
    }
}
