package com.koala.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("combo_dish")
public class ComboDish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long comboId;
    private Long dishId;
    private Integer quantity;
}
