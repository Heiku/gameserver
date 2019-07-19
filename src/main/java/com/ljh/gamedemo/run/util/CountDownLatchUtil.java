package com.ljh.gamedemo.run.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 */
public class CountDownLatchUtil {

    public static CountDownLatch countDownLatch;

    private static boolean aborted;

    public static void newLatch(int num){
        countDownLatch = new CountDownLatch(num);
    }

    public static void abort(){
        aborted = true;
        countDownLatch.countDown();
    }

    public static void succeed(){
        countDownLatch.countDown();
    }

    public static void await() throws Exception{
        countDownLatch.await(2, TimeUnit.SECONDS);
        if (aborted){
            throw new Exception();

        }
    }
}
