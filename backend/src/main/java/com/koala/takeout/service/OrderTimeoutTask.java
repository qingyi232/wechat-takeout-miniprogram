package com.koala.takeout.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.koala.takeout.common.Constants;
import com.koala.takeout.entity.Orders;
import com.koala.takeout.entity.SystemConfig;
import com.koala.takeout.mapper.OrdersMapper;
import com.koala.takeout.mapper.SystemConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderTimeoutTask {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Scheduled(fixedRate = 60000)
    public void cancelTimeoutOrders() {
        int timeoutMinutes = 30;
        try {
            SystemConfig config = systemConfigMapper.selectOne(
                new LambdaQueryWrapper<SystemConfig>().eq(SystemConfig::getConfigKey, "order_timeout"));
            if (config != null) {
                timeoutMinutes = Integer.parseInt(config.getConfigValue());
            }
        } catch (Exception e) {
            log.warn("获取订单超时配置失败，使用默认值30分钟");
        }

        LocalDateTime deadline = LocalDateTime.now().minusMinutes(timeoutMinutes);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getStatus, Constants.ORDER_PENDING_PAY)
               .lt(Orders::getCreateTime, deadline);
        List<Orders> timeoutOrders = ordersMapper.selectList(wrapper);

        for (Orders order : timeoutOrders) {
            order.setStatus(Constants.ORDER_CANCELLED);
            order.setCancelTime(LocalDateTime.now());
            ordersMapper.updateById(order);
            log.info("订单[{}]超时未支付，已自动取消", order.getOrderNo());
        }

        if (!timeoutOrders.isEmpty()) {
            log.info("本次共取消{}个超时未支付订单", timeoutOrders.size());
        }
    }
}
