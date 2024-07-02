package org.moroboshidan.common.util;

import org.moroboshidan.common.model.StandardSubmit;

import java.util.concurrent.ConcurrentHashMap;

public class CMPPSubmitRepoMapUtil {

    private static ConcurrentHashMap<String, StandardSubmit> map = new ConcurrentHashMap<>();


    public static void put(int sequence,StandardSubmit submit){
        map.put(sequence + "",submit);
    }

    public static StandardSubmit get(int sequence){
        return map.get(sequence + "");
    }

    public static StandardSubmit remove(int sequence){
        return map.remove(sequence + "");
    }


}
