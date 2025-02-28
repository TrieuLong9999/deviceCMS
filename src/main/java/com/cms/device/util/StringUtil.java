package com.cms.device.util;

import java.util.UUID;

public class StringUtil {
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }
    public static boolean isEmpty(String data){
        return data == null || data.isEmpty();
    }
}
