package com.mahjong.mahjongserver.infrastructure;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class TimeoutScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public void schedule(String key, Runnable task, long delayMillis) {
        cancel(key);
        ScheduledFuture<?> future = scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
        scheduledTasks.put(key, future);
    }

    public void cancel(String key) {
        ScheduledFuture<?> future = scheduledTasks.remove(key);
        if (future != null) future.cancel(false);
    }
}