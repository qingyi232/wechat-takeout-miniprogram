package com.koala.takeout.config;

import com.koala.takeout.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("数据完整性异常: {}", e.getMessage());
        String msg = "数据保存失败";
        String cause = e.getMostSpecificCause().getMessage();
        if (cause != null) {
            if (cause.contains("doesn't have a default value")) {
                String field = cause.replace("Field '", "").replace("' doesn't have a default value", "");
                msg = "必填字段 " + field + " 不能为空";
            } else if (cause.contains("Duplicate entry")) {
                msg = "数据已存在，请勿重复添加";
            } else if (cause.contains("cannot be null")) {
                msg = "必填字段不能为空";
            }
        }
        return Result.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        return Result.error("操作失败: " + e.getMessage());
    }
}
