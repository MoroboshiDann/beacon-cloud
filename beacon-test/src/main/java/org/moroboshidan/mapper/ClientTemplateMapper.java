package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.ClientTemplate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface ClientTemplateMapper {

    @Select("select * from client_template where sign_id = #{signId}")
    List<ClientTemplate> findBySignId(@Param("signId") Long signId);

}
