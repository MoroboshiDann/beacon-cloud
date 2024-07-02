package org.moroboshidan.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String obj2JSON(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("转换JSON失败！");
        }
    }
}
