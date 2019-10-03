package com.dbvr.baselibrary.utils;

public class StringUtils {

    public static boolean isEmpty(String str){
        if (null == str || str.equals("") || str.equals("null")){
            return true;
        }
        return false;
    }

    public static String removeStr(String str){
        return str.replace("txt:","");
    }
}
