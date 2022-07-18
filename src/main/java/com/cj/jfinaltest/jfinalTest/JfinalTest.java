package com.cj.jfinaltest.jfinalTest;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-15 15:11
 */
public class JfinalTest {

    /**
     * 模拟缓存定时任务
     *
     * @param args
     */
    public static void main(String[] args) {
        new Timer("123", false).schedule(new TimerTask() {
            @Override
            public void run() {
                task();
            }
        }, 5000, 5000);
    }

    private static void task() {
        System.out.println("执行定时任务,移出token缓存");
    }
}
