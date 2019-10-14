package com.dbvr.baselibrary.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String getDateForNow(String mType){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(mType);// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }


    // 将秒转化成小时分钟秒
    public static String formatMiss(int miss){
        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
        return hh+":"+mm+":"+ss;
    }

    public static String stampToDate(String s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH:mm:ss");

//        String a = simpleDateFormat2.format(date);

        long lt = new Long(s);
        Date date = new Date(lt);
        Date date2 = new Date(System.currentTimeMillis());
        String one = simpleDateFormat2.format(date);//转换后的日期
        String tow = simpleDateFormat2.format(date2);//当前系统时间
        if (one.equals(tow)) {
            //如果时间一样，返回时分秒即可
            return simpleDateFormat3.format(date);
        } else {//否则返回完整日期
            return simpleDateFormat.format(date);
        }
    }
}
