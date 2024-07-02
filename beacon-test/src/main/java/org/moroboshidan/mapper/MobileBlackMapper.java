package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.MobileBlack;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface MobileBlackMapper {

    @Select("select black_number,client_id from mobile_black where is_delete = 0")
    List<MobileBlack> findAll();

}
