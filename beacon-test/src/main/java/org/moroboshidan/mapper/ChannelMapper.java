package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.Channel;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface ChannelMapper {

    @Select("select * from channel where is_delete = 0")
    List<Channel> findAll();

}
