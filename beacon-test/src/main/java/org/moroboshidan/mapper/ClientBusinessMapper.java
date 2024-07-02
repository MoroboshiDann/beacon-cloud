package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.ClientBusiness;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface ClientBusinessMapper {

    @Select("select * from client_business where id = #{id}")
    ClientBusiness findById(@Param("id") Long id);

}
