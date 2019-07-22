package com.ljh.gamedemo.run.record;

import com.google.common.collect.Maps;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Map;

/**
 * @Author: Heiku
 * @Date: 2019/7/19
 *
 * 用于记录自动恢复任务的future
 */
public class FutureMap {

    public static Map<Integer, ScheduledFuture> futureMap = Maps.newConcurrentMap();
}
