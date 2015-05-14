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
package com.openAtlas.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.os.Process;
import android.util.Log;

import com.openAtlas.runtime.RuntimeVariables;

public class OpenAtlasFileLock {
    private static final String TAG = "OpenAtlasFileLock";
    private static String processName;
    private static OpenAtlasFileLock singleton;
    private Map<String, FileLockCount> mRefCountMap;

    private class FileLockCount {
        FileLock mFileLock;
        int mRefCount;

        FileLockCount(FileLock mFileLock, int mRefCount) {
            this.mFileLock = mFileLock;
            this.mRefCount = mRefCount;
        }
    }

    public OpenAtlasFileLock() {
        this.mRefCountMap = new HashMap();
    }

    static {
        int myPid = Process.myPid();
        if (RuntimeVariables.androidApplication.getApplicationContext() != null) {
            for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) RuntimeVariables.androidApplication
                    .getApplicationContext().getSystemService("activity"))
                    .getRunningAppProcesses()) {
                if (runningAppProcessInfo.pid == myPid) {
                    processName = runningAppProcessInfo.processName;
                }
            }
        }
    }

    public static OpenAtlasFileLock getInstance() {
        if (singleton == null) {
            singleton = new OpenAtlasFileLock();
        }
        return singleton;
    }

    private int RefCntInc(String filePath, FileLock fileLock) {
        Integer valueOf;
        Integer.valueOf(0);
        if (this.mRefCountMap.containsKey(filePath)) {
            FileLockCount fileLockCount = this.mRefCountMap
                    .get(filePath);
            int i = fileLockCount.mRefCount;
            fileLockCount.mRefCount = i + 1;
            valueOf = Integer.valueOf(i);
        } else {
            valueOf = Integer.valueOf(1);
            this.mRefCountMap.put(filePath,
                    new FileLockCount(fileLock, valueOf.intValue()));
        }
        return valueOf.intValue();
    }

    private int RefCntDec(String filePath) {
        Integer valueOf = Integer.valueOf(0);
        if (this.mRefCountMap.containsKey(filePath)) {
            FileLockCount fileLockCount = this.mRefCountMap
                    .get(filePath);
            int i = fileLockCount.mRefCount - 1;
            fileLockCount.mRefCount = i;
            valueOf = Integer.valueOf(i);
            if (valueOf.intValue() <= 0) {
                this.mRefCountMap.remove(filePath);
            }
        }
        return valueOf.intValue();
    }

    public boolean LockExclusive(File file) {
        if (file == null) {
            return false;
        }
        try {
            @SuppressWarnings("resource")
			FileChannel channel = new RandomAccessFile(file.getAbsolutePath(),
                    "rw").getChannel();
            if (channel == null) {
                return false;
            }
            Log.i(TAG, processName + " attempting to FileLock " + file);
            FileLock lock = channel.lock();
            if (!lock.isValid()) {
                return false;
            }
            RefCntInc(file.getAbsolutePath(), lock);
            Log.i(TAG, processName + " FileLock " + file + " Suc! ");
            return true;
        } catch (Exception e) {
            Log.e(TAG,
                    processName + " FileLock " + file + " FAIL! "
                            + e.getMessage());
            return false;
        }
    }

    public void unLock(File file) {
        if (file == null
                || this.mRefCountMap.containsKey(file.getAbsolutePath())) {
            FileLock fileLock = this.mRefCountMap.get(file
                    .getAbsolutePath()).mFileLock;
            if (fileLock != null && fileLock.isValid()) {
                try {
                    if (RefCntDec(file.getAbsolutePath()) <= 0) {
                        fileLock.release();
                        Log.i(TAG,
                                processName + " FileLock "
                                        + file.getAbsolutePath() + " SUC! ");
                    }
                } catch (IOException e) {
                }
            }
        }
    }
}
