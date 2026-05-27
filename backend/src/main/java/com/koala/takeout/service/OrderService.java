package com.koala.takeout.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.koala.takeout.common.Constants;
import com.koala.takeout.common.PageResult;
import com.koala.takeout.entity.*;
import com.koala.takeout.mapper.*;
import com.koala.takeout.websocket.OrderWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ComboMapper comboMapper;
    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private CommentMapper commentMapper;

    @Transactional
    public Orders createOrder(Long userId, Long merchantId, Long addressId,
                              String addressDetail, String contactName, String contactPhone,
                              String remark, List<Map<String, Object>> items) {
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Object comboIdObj = item.get("comboId");
            Object dishIdObj = item.get("dishId");

            if (comboIdObj != null && !comboIdObj.toString().equals("") && !comboIdObj.toString().equals("null")) {
                Long comboId = Long.parseLong(comboIdObj.toString());
                Integer quantity = Integer.parseInt(item.get("quantity").toString());
                Combo combo = comboMapper.selectById(comboId);
                if (combo == null) continue;

                BigDecimal subtotal = combo.getPrice().multiply(BigDecimal.valueOf(quantity));
                totalAmount = totalAmount.add(subtotal);

                OrderItem orderItem = new OrderItem();
                orderItem.setComboId(comboId);
                orderItem.setName(combo.getName());
                orderItem.setImage(combo.getImage());
                orderItem.setPrice(combo.getPrice());
                orderItem.setQuantity(quantity);
                orderItem.setAmount(subtotal);
                orderItems.add(orderItem);
            } else if (dishIdObj != null) {
                Long dishId = Long.parseLong(dishIdObj.toString());
                Integer quantity = Integer.parseInt(item.get("quantity").toString());
                Dish dish = dishMapper.selectById(dishId);
                if (dish == null) continue;

                // 如果前端传了price（含规格加价），使用前端价格；否则用数据库价格
                BigDecimal itemPrice;
                if (item.get("price") != null) {
                    itemPrice = new BigDecimal(item.get("price").toString());
                } else {
                    itemPrice = dish.getCurrentPrice();
                }

                BigDecimal originalSubtotal = dish.getOriginalPrice().multiply(BigDecimal.valueOf(quantity));
                BigDecimal currentSubtotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
                totalAmount = totalAmount.add(originalSubtotal);
                // 如果规格加价后比原价还高，折扣为0
                if (originalSubtotal.compareTo(currentSubtotal) > 0) {
                    discountAmount = discountAmount.add(originalSubtotal.subtract(currentSubtotal));
                } else {
                    totalAmount = totalAmount.subtract(originalSubtotal).add(currentSubtotal);
                }

                String itemName = dish.getName();
                if (item.get("name") != null && !item.get("name").toString().isEmpty()) {
                    itemName = item.get("name").toString();
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setDishId(dishId);
                orderItem.setName(itemName);
                orderItem.setImage(dish.getImage());
                orderItem.setPrice(itemPrice);
                orderItem.setQuantity(quantity);
                orderItem.setAmount(currentSubtotal);
                orderItems.add(orderItem);

                dish.setSales(dish.getSales() + quantity);
                dish.setStock(dish.getStock() - quantity);
                dishMapper.updateById(dish);
            }
        }

        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        Merchant merchant = merchantMapper.selectById(merchantId);
        BigDecimal deliveryFee = merchant != null ? merchant.getDeliveryFee() : BigDecimal.ZERO;
        if (merchant != null && merchant.getFreeDelivery() == 1) {
            deliveryFee = BigDecimal.ZERO;
        }
        payAmount = payAmount.add(deliveryFee);

        Orders order = new Orders();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setMerchantId(merchantId);
        order.setAddressId(addressId);
        order.setAddressDetail(addressDetail);
        order.setContactName(contactName);
        order.setContactPhone(contactPhone);
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setDeliveryFee(deliveryFee);
        order.setStatus(Constants.ORDER_PENDING_PAY);
        order.setRemark(remark);
        ordersMapper.insert(order);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }

        return order;
    }

    public void payOrder(Long orderId, Long userId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) return;
        if (order.getStatus() != Constants.ORDER_PENDING_PAY) return;

        order.setStatus(Constants.ORDER_PENDING_ACCEPT);
        order.setPayTime(LocalDateTime.now());
        ordersMapper.updateById(order);

        Map<String, Object> msg = new HashMap<>();
        msg.put("type", "new_order");
        msg.put("orderId", order.getId());
        msg.put("orderNo", order.getOrderNo());
        msg.put("payAmount", order.getPayAmount());
        OrderWebSocket.sendToMerchant(order.getMerchantId(), JSON.toJSONString(msg));

        Merchant merchant = merchantMapper.selectById(order.getMerchantId());
        if (merchant != null) {
            // 按订单中商品总数量增加月售
            List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            int totalQty = 0;
            for (OrderItem item : items) {
                totalQty += item.getQuantity();
            }
            merchant.setMonthlySales(merchant.getMonthlySales() + totalQty);
            merchantMapper.updateById(merchant);
        }
    }

    public void updateStatus(Long orderId, Integer status) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) return;
        order.setStatus(status);
        if (status == Constants.ORDER_DELIVERING) {
            order.setAcceptTime(LocalDateTime.now());
        } else if (status == Constants.ORDER_COMPLETED) {
            order.setCompleteTime(LocalDateTime.now());
        } else if (status == Constants.ORDER_CANCELLED) {
            order.setCancelTime(LocalDateTime.now());
        }
        ordersMapper.updateById(order);
    }

    public List<Map<String, Object>> getUserOrders(Long userId, Integer status) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        List<Orders> orders = ordersMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Orders order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("orderNo", order.getOrderNo());
            map.put("userId", order.getUserId());
            map.put("merchantId", order.getMerchantId());
            map.put("totalAmount", order.getTotalAmount());
            map.put("discountAmount", order.getDiscountAmount());
            map.put("payAmount", order.getPayAmount());
            map.put("deliveryFee", order.getDeliveryFee());
            map.put("status", order.getStatus());
            map.put("remark", order.getRemark());
            map.put("createTime", order.getCreateTime());
            Long commentCount = commentMapper.selectCount(
                new LambdaQueryWrapper<Comment>().eq(Comment::getOrderId, order.getId()));
            map.put("commented", commentCount > 0);
            Merchant merchant = merchantMapper.selectById(order.getMerchantId());
            map.put("merchantName", merchant != null ? merchant.getName() : "");
            map.put("merchantLogo", merchant != null ? merchant.getLogo() : "");
            List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
            if (!items.isEmpty()) {
                map.put("firstItemImage", items.get(0).getImage());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    if (i > 0) sb.append("、");
                    sb.append(items.get(i).getName());
                    if (items.get(i).getQuantity() > 1) {
                        sb.append("x").append(items.get(i).getQuantity());
                    }
                }
                map.put("itemsDesc", sb.toString());
                map.put("itemCount", items.size());
            } else {
                map.put("firstItemImage", "");
                map.put("itemsDesc", "");
                map.put("itemCount", 0);
            }
            result.add(map);
        }
        return result;
    }

    public Map<String, Object> getOrderDetail(Long orderId) {
        Orders order = ordersMapper.selectById(orderId);
        if (order == null) return null;
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        Merchant merchant = merchantMapper.selectById(order.getMerchantId());

        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("items", items);
        result.put("merchantName", merchant != null ? merchant.getName() : "");
        return result;
    }

    public PageResult<Orders> merchantOrders(Long merchantId, int pageNum, int pageSize, Integer status) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getMerchantId, merchantId);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        wrapper.orderByDesc(Orders::getCreateTime);
        Page<Orders> result = ordersMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }

    public Map<String, Object> getMerchantStats(Long merchantId) {
        return ordersMapper.getMerchantStats(merchantId);
    }

    public List<Map<String, Object>> getRecentStats(Long merchantId) {
        return ordersMapper.getRecentStats(merchantId);
    }

    public PageResult<Orders> adminOrders(int pageNum, int pageSize, Integer status, Long merchantId) {
        Page<Orders> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Orders::getStatus, status);
        if (merchantId != null) wrapper.eq(Orders::getMerchantId, merchantId);
        wrapper.orderByDesc(Orders::getCreateTime);
        Page<Orders> result = ordersMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getRecords());
    }
}
