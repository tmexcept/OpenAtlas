/**
 *  OpenAtlasForAndroid Project
The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, 
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies 
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
@author BunnyBlue
 * **/
package com.openatlas.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BundleLock {
    static ReentrantReadWriteLock[] mLock;

    static {
        mLock = new ReentrantReadWriteLock[10];
        for (int i = 0; i < 10; i++) {
            mLock[i] = new ReentrantReadWriteLock();
        }
    }

    public static void WriteLock(String location) {
        mLock[hash(location)].writeLock().lock();
    }

    public static void WriteUnLock(String location) {
        mLock[hash(location)].writeLock().unlock();
    }

    public static void ReadLock(String location) {
        mLock[hash(location)].readLock().lock();
    }

    public static void ReadUnLock(String location) {
        mLock[hash(location)].readLock().unlock();
    }

    private static int hash(String location) {
        int i = 0;
        int length = location.length();
        byte[] bytes = location.getBytes();
        int i2 = 0;
        while (i < length) {
            i2 += bytes[i];
            i++;
        }
        return i2 % 10;
    }
}
