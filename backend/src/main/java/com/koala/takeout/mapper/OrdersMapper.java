package com.koala.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.koala.takeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    @Select("SELECT DATE(create_time) as date, COUNT(*) as count, SUM(pay_amount) as amount " +
            "FROM orders WHERE merchant_id = #{merchantId} AND status IN (2,3,4) AND deleted = 0 " +
            "GROUP BY DATE(create_time) ORDER BY date DESC LIMIT 7")
    List<Map<String, Object>> getRecentStats(Long merchantId);

    @Select("SELECT COUNT(*) as totalOrders, IFNULL(SUM(pay_amount),0) as totalAmount " +
            "FROM orders WHERE merchant_id = #{merchantId} AND status IN (2,3,4) AND deleted = 0")
    Map<String, Object> getMerchantStats(Long merchantId);
}
