package com.koala.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("dish_spec_group")
public class DishSpecGroup {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dishId;
    private String groupName;
    private Integer required;
    private Integer sortOrder;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
