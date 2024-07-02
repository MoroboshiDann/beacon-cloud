package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.ClientSign;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface ClientSignMapper {

    @Select("select * from client_sign where client_id = #{clientId}")
    List<ClientSign> findByClientId(@Param("clientId")Long clientId);

}
