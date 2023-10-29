package com.xzavier0722.mc.plugin.slimefun4.storage.controller;

import com.xzavier0722.mc.plugin.slimefun4.storage.common.ScopeKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

class ScopedLock {
    private final Map<ScopeKey, ReentrantLock> locks;

    ScopedLock() {
        locks = new ConcurrentHashMap<>();
    }

    void lock(ScopeKey scopeKey) {
        var lock = locks.computeIfAbsent(scopeKey, k -> new ReentrantLock());
        lock.lock();
    }

    void unlock(ScopeKey scopeKey) {
        var lock = locks.get(scopeKey);
        if (lock == null) {
            return;
        }

        lock.unlock();
        if (!lock.isLocked()) {
            locks.remove(scopeKey);
        }
    }

    boolean hasLock(ScopeKey scopeKey) {
        return locks.containsKey(scopeKey);
    }
}
