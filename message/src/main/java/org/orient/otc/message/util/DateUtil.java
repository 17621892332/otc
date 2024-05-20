package org.orient.otc.message.util;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */

@Data
public class DateUtil {
    /**
     * 获取上1小时之内的
     * @return 返回日期
     */
    public static Date getPreHour(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY,-1); // 1小时之前的
        return calendar.getTime();
    }
    /**
     * 获取上一天的0时0分
     * @return 返回日期
     */
    public static Date getPreDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,-1); // 1天之前的
        calendar.set(Calendar.HOUR_OF_DAY,0); // 0时
        calendar.set(Calendar.MINUTE,0); // 0分
        calendar.set(Calendar.SECOND,0); // 0秒
        return calendar.getTime();
    }


    /**
     * 比较两个日期是否同一天
     * @param localDate 入参1
     * @param date 入参2
     * @return 返回boolean
     */
    public static boolean equalsDateAndLocalDate(LocalDate localDate,Date date){
        if (localDate!=null && date !=null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateStr = localDate.format(formatter);
            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr2 = formatter2.format(date);
            return dateStr.equals(dateStr2);
        } else {
            return false;
        }
    }
}
