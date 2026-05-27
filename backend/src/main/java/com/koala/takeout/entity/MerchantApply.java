package com.koala.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("merchant_apply")
public class MerchantApply {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String logo;
    private String description;
    private String address;
    private java.math.BigDecimal latitude;
    private java.math.BigDecimal longitude;
    private String phone;
    private String categoryType;
    private String businessHours;
    private Integer status;
    private String rejectReason;
    private String merchantAccount;
    private String merchantPassword;
    private Long merchantId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
