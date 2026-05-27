package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.common.Constants;
import com.koala.takeout.entity.User;
import com.koala.takeout.mapper.UserMapper;
import com.koala.takeout.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(String openid, String nickName, String avatarUrl) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickName(nickName);
            user.setAvatarUrl(avatarUrl);
            user.setStatus(1);
            userMapper.insert(user);
        } else {
            user.setNickName(nickName);
            user.setAvatarUrl(avatarUrl);
            userMapper.updateById(user);
        }
        String token = jwtUtil.generateToken(user.getId(), Constants.ROLE_USER);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        return result;
    }

    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    public void update(User user) {
        userMapper.updateById(user);
    }
}
