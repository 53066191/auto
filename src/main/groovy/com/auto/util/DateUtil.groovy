package com.auto.util

import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat

public class DateUtil {

    static SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String  getCurrentTime(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(d);
    }

    public static long getCurrentDay(){
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Long.parseLong(sdf.format(d));
    }


    public static String getTime(int num){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, num);    //得到前一天
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static Date getCurrentDate() throws ParseException {
        String now = getCurrentTime();
        Date date=defaultFormat.parse(now);
        return date;
    }

    /**
     * 将java.sql.Timestamp对象转化为String字符串
     * @param time
     *            要格式的java.sql.Timestamp对象
     * @param strFormat
     *            输出的String字符串格式的限定（如："yyyy-MM-dd HH:mm:ss"）
     * @return 表示日期的字符串
     */
    public static String dateToStr(java.sql.Timestamp time) {
        String strFormat="yyyy-MM-dd HH:mm:ss";
        DateFormat df = new SimpleDateFormat(strFormat);
        String str = df.format(time);
        return str;
    }


    public static Date strToDate(String str){
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date  date = null;
        try {
            date = format1.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}