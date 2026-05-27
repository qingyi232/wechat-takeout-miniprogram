package com.koala.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("merchant")
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logo;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phone;
    private BigDecimal rating;
    private Integer monthlySales;
    private Integer deliveryTime;
    private Integer deliveryRange;
    private BigDecimal deliveryFee;
    private Integer freeDelivery;
    private Integer platformDelivery;
    private String categoryType;
    private String businessHours;
    private Integer status;
    private String account;
    private String password;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
