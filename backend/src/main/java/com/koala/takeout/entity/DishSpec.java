package com.koala.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dish_spec")
public class DishSpec {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long groupId;
    private Long dishId;
    private String name;
    private BigDecimal priceAdjustment;
    private Integer isDefault;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
