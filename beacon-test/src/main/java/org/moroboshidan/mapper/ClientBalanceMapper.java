package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.moroboshidan.entity.ClientBalance;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface ClientBalanceMapper {

    @Select("select * from client_balance where client_id = #{clientId}")
    ClientBalance findByClientId(@Param("clientId")Long clientId);

}
