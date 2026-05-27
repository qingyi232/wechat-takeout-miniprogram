package com.koala.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.koala.takeout.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
