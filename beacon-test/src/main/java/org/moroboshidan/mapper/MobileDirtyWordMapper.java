package org.moroboshidan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zjw
 * @description
 */
@Mapper
public interface MobileDirtyWordMapper {

    @Select("select dirtyword from mobile_dirtyword")
    List<String> findDirtyWord();

}
