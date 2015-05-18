package com.openatlas.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BundleLock {
    static Map<String, ReentrantReadWriteLock> bundleIdentifierMap;

    static {
        bundleIdentifierMap = new HashMap();
    }

    public static void WriteLock(String str) {
        ReentrantReadWriteLock reentrantReadWriteLock;
        synchronized (bundleIdentifierMap) {
            reentrantReadWriteLock = bundleIdentifierMap.get(str);
            if (reentrantReadWriteLock == null) {
                reentrantReadWriteLock = new ReentrantReadWriteLock();
                bundleIdentifierMap.put(str, reentrantReadWriteLock);
            }
        }
        reentrantReadWriteLock.writeLock().lock();
    }

    public static void WriteUnLock(String str) {
        synchronized (bundleIdentifierMap) {
            ReentrantReadWriteLock reentrantReadWriteLock = bundleIdentifierMap.get(str);
            if (reentrantReadWriteLock == null) {
                return;
            }
            reentrantReadWriteLock.writeLock().unlock();
        }
    }

    public static void ReadLock(String str) {
        ReentrantReadWriteLock reentrantReadWriteLock;
        synchronized (bundleIdentifierMap) {
            reentrantReadWriteLock = bundleIdentifierMap.get(str);
            if (reentrantReadWriteLock == null) {
                reentrantReadWriteLock = new ReentrantReadWriteLock();
                bundleIdentifierMap.put(str, reentrantReadWriteLock);
            }
        }
        reentrantReadWriteLock.readLock().lock();
    }

    public static void ReadUnLock(String str) {
        synchronized (bundleIdentifierMap) {
            ReentrantReadWriteLock reentrantReadWriteLock = bundleIdentifierMap.get(str);
            if (reentrantReadWriteLock == null) {
                return;
            }
            reentrantReadWriteLock.readLock().unlock();
        }
    }
}