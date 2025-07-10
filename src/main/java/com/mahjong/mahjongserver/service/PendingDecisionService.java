package com.mahjong.mahjongserver.service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

@Component
public class PendingDecisionService {
    private final Map<String, CompletableFuture<Object>> decisions = new ConcurrentHashMap<>();

    public <T> CompletableFuture<T> waitForDecision(String username, String decisionType) {
        String key = username + ":" + decisionType;
        CompletableFuture<T> future = new CompletableFuture<>();
        decisions.put(key, (CompletableFuture<Object>) future);
        return future;
    }

    public void submitDecision(String username, String decisionType, Object result) {
        String key = username + ":" + decisionType;
        CompletableFuture<Object> future = decisions.remove(key);
        if (future != null) {
            future.complete(result);
        }
    }
}
