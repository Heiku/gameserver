package com.ljh.gamedemo.run.record;

import com.google.common.collect.Maps;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.Map;

/**
 * 用于记录自动恢复任务的future
 *
 * @Author: Heiku
 * @Date: 2019/7/19
 */
public class FutureMap {

    /**
     * allFutureMap (hashcode, future)
     */
    public static Map<Integer, ScheduledFuture> futureMap = Maps.newConcurrentMap();

    /**
     * 回复任务的 futureMao (roleId, future)
     */
    private static Map<Long, ScheduledFuture> recoverFutureMap = Maps.newConcurrentMap();

    public static Map<Long, ScheduledFuture> getRecoverFutureMap() {
        return recoverFutureMap;
    }

    public static Map<Integer, ScheduledFuture> getFutureMap() {
        return futureMap;
    }
}
