package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.ClientChannel;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface ClientChannelMapper {

    @Select("select * from client_channel where is_delete = 0")
    List<ClientChannel> findAll();

}
