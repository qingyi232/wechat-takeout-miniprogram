package com.koala.takeout.interceptor;

import com.alibaba.fastjson.JSON;
import com.koala.takeout.common.Constants;
import com.koala.takeout.common.Result;
import com.koala.takeout.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(JSON.toJSONString(Result.error(401, "未登录")));
            return false;
        }
        try {
            if (jwtUtil.isTokenExpired(token)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(401);
                response.getWriter().write(JSON.toJSONString(Result.error(401, "登录已过期")));
                return false;
            }
            Claims claims = jwtUtil.parseToken(token);
            Long userId = claims.get("id", Long.class);
            String role = claims.get("role", String.class);
            request.setAttribute("userId", userId);
            request.setAttribute("userRole", role);

            if (!checkRolePermission(request, role)) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(403);
                response.getWriter().write(JSON.toJSONString(Result.error(403, "无权限访问")));
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(JSON.toJSONString(Result.error(401, "无效的Token")));
            return false;
        }
    }

    private boolean checkRolePermission(HttpServletRequest request, String role) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if (uri.startsWith("/api/admin/") && !uri.equals("/api/admin/login")) {
            return Constants.ROLE_ADMIN.equals(role);
        }

        if (uri.startsWith("/api/announcement/") && !uri.startsWith("/api/announcement/list")) {
            if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return Constants.ROLE_ADMIN.equals(role);
            }
        }

        if (uri.startsWith("/api/employee/")) {
            return Constants.ROLE_MERCHANT.equals(role) || Constants.ROLE_ADMIN.equals(role);
        }

        if (uri.startsWith("/api/order/merchant/")) {
            return Constants.ROLE_MERCHANT.equals(role) || Constants.ROLE_ADMIN.equals(role);
        }

        if (uri.startsWith("/api/order/admin/")) {
            return Constants.ROLE_ADMIN.equals(role);
        }

        if (uri.startsWith("/api/dish/") || uri.startsWith("/api/category/") || uri.startsWith("/api/combo/")) {
            if ("POST".equals(method) || "PUT".equals(method) || "DELETE".equals(method)) {
                return Constants.ROLE_MERCHANT.equals(role) || Constants.ROLE_ADMIN.equals(role);
            }
        }

        if (uri.startsWith("/api/address/") || uri.startsWith("/api/favorite/")) {
            return Constants.ROLE_USER.equals(role);
        }

        if (uri.startsWith("/api/order/create") || uri.startsWith("/api/order/pay/") || uri.startsWith("/api/order/user/")) {
            return Constants.ROLE_USER.equals(role);
        }

        if (uri.startsWith("/api/merchant-apply/admin/")) {
            return Constants.ROLE_ADMIN.equals(role);
        }

        return true;
    }
}
