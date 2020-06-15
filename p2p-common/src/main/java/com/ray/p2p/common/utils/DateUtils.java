package com.ray.p2p.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Description:
 * @Author: Ray Li
 * @E-mail: ray.lir@outlook.com
 * @Date: 2020/5/16 11:20
 */
public class DateUtils {


    /**
     * 通过指定日期添加天数返回日期值
     *
     * @param date
	 * @param count
     * @return java.util.Date
     * @version 1.0.0
     * @author Ray Li
     * @date 2020/6/16 0:54
     */
    public static Date getDateByAddDays(Date date, Integer count) {

        //日期处理类对象
        Calendar instance = Calendar.getInstance();

        //设置日期处理类对象的日期值
        instance.setTime(date);

        //在指定日期上添加天数
        instance.add(Calendar.DATE,count);


        return instance.getTime();
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(getDateByAddDays(new SimpleDateFormat("yyyy-MM-dd").parse("2008-08-08"),-1));
    }

    public static Date getDateByAddMonths(Date date, Integer count) {
        //日期处理类对象
        Calendar instance = Calendar.getInstance();

        //设置日期处理类对象的日期值
        instance.setTime(date);

        //在指定日期上添加天数
        instance.add(Calendar.MONTH,count);


        return instance.getTime();
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
