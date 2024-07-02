package org.moroboshidan.common.util;

import org.moroboshidan.common.model.StandardReport;
import org.moroboshidan.common.model.StandardSubmit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于CMPP的状态回到时，获取核心信息的方式
 * @author zjw
 * @description
 */
public class CMPPDeliverMapUtil {

    private static ConcurrentHashMap<String, StandardReport> map = new ConcurrentHashMap<>();


    public static void put(String msgId,StandardReport submit){
        map.put(msgId,submit);
    }

    public static StandardReport get(String msgId){
        return map.get(msgId);
    }

    public static StandardReport remove(String msgId){
        return map.remove(msgId);
    }


}
