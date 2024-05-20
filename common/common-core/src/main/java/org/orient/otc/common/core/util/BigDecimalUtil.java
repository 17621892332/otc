package org.orient.otc.common.core.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * bigDecimal工具类
 */
public class BigDecimalUtil {
    /**
     * 百分比数转为小数
     * @param s 百分比数
     * @return 小数
     */
    public static BigDecimal percentageToBigDecimal(BigDecimal s) {
        return s.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
    }

    /**
     * 小数数转为百分比
     * @param s 小数
     * @return 百分比数
     */
    public static BigDecimal bigDecimalToPercentage(BigDecimal s) {
        return s.multiply(BigDecimal.valueOf(100));
    }

    /**
     * 获取bigDecimal 四舍五入保留指定位数的字符串 并返回String
     * @param value  数值
     * @param scale  有效位数
     * @return 返回字符串
     */
    public static String getBigDecimalString(BigDecimal value,int scale){
        if (value != null){
            return String.valueOf(value.setScale(scale,RoundingMode.HALF_UP));
        } else {
            return null;
        }
    }
    /**
     * 获取bigDecimal 四舍五入保留指定小数位数 并返回bigDecimal数值对象
     * @param value 数值
     * @param scale  有效位数
     * @return  返回处理后数字
     */
    public static BigDecimal getBigDecimalScale(BigDecimal value,int scale){
        if (value != null){
            return value.setScale(scale,RoundingMode.HALF_UP);
        } else {
            return null;
        }
    }
    /**
     * 四舍五入保留两位小数,千分位展示
     * @param bigDecimalValue 数值
     * @return 返回字符串
     */
    public static String getThousandsString(BigDecimal bigDecimalValue){
        if (bigDecimalValue != null){
            String pattern = "#,##0.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            return decimalFormat.format(bigDecimalValue);
        } else {
            return null;
        }
    }

    /**
     * 字符串转bigdecimal
     * @param value 入参
     * @return 返回数值
     */
    public static BigDecimal getString2BigDecimal(String value){
        // 去除千分位
        value = value.replaceAll(",","").replaceAll("，","");
        return new BigDecimal(value);
    }
}
