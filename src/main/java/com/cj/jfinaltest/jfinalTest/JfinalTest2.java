package com.cj.jfinaltest.jfinalTest;


import java.util.Calendar;
import java.util.Date;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-15 16:07
 */
public class JfinalTest2 {
    public static void main(String[] args) {
        Date date = new Date();
        try {
            Thread.sleep(Long.parseLong("2000"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Date nowDate = new Date();
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal.setTime(date);
        cal2.setTime(nowDate);
        float f = cal.getTimeInMillis() - cal2.getTimeInMillis();
        System.out.println(cal.getTimeInMillis());
        System.out.println(cal2.getTimeInMillis());
        System.out.println(f);

    }
}
