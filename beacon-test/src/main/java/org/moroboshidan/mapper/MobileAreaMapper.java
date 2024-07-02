package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.moroboshidan.entity.MobileArea;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface MobileAreaMapper {

    @Select("select mobile_number,mobile_area,mobile_type from mobile_area")
    List<MobileArea> findAll();

}
