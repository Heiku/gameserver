package com.ljh.gamedemo.util;

import java.util.Random;

/**
 * 公共工具类
 *
 * @Author: Heiku
 * @Date: 2019/8/14
 */
public class CommonUtil {


    public static long generateLong(){
        return Math.abs(new Random().nextLong());
    }
}
