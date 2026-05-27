package com.koala.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.koala.takeout.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
