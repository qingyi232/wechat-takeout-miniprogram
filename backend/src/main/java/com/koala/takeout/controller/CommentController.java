package com.koala.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.common.Result;
import com.koala.takeout.entity.Comment;
import com.koala.takeout.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @GetMapping("/list/{merchantId}")
    public Result<List<Comment>> list(@PathVariable Long merchantId) {
        return Result.success(commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getMerchantId, merchantId)
                        .orderByDesc(Comment::getCreateTime)));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);
        commentMapper.insert(comment);
        return Result.success();
    }
}
