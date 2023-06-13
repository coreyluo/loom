package com.bazinga.util;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author yunshan
 * @date 2019/1/9
 */
public class DateTimeUtils {


    /**
     * 获取当天23:59:59秒的时间
     *
     * @return
     */
    public static Date getDate235959() {
        return getDate235959(new Date());
    }

    /**
     * 获取当前时间+N天的23:59:59秒的时间
     *
     * @param days
     * @return
     */
    public static Date getDate235959(int days) {
        return getDate235959(DateUtils.addDays(new Date(), days));
    }

    /**
     * 获取某天23:59:59秒的时间
     *
     * @param date
     * @return
     */
    public static Date getDate235959(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }
    public static String trans2CommonFormat(String originalQuoteTime ,boolean isL1Quote){
        if(isL1Quote){
            return  originalQuoteTime.replaceAll(":", "")+ "000";
        }else {
            if(originalQuoteTime.startsWith("9")){
                originalQuoteTime = "0" + originalQuoteTime;
            }
            int suffixLength = 9-originalQuoteTime.length();
            if(suffixLength == 1){
                originalQuoteTime = originalQuoteTime +"0";
            }else if(suffixLength ==2 ){
                originalQuoteTime = originalQuoteTime +"00";
            }else if(suffixLength == 3){
                originalQuoteTime = originalQuoteTime +"000";
            }
            return originalQuoteTime;
        }
    }
    /**
     * 获取当天00:00:00秒的时间
     *
     * @return
     */
    public static Date getDate000000() {
        return getDate000000(new Date());
    }

    /**
     * 获取当前时间+N天的00:00:00秒的时间
     *
     * @param days
     * @return
     */
    public static Date getDate000000(int days) {
        return getDate000000(DateUtils.addDays(new Date(), days));
    }

    /**
     * 获取某天00:00:00秒的时间
     *
     * @param date
     * @return
     */
    public static Date getDate000000(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    public static void main(String[] args) {


    }


}
