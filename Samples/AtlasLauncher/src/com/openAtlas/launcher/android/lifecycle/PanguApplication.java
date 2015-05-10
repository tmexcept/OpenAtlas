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
package com.openAtlas.launcher.android.lifecycle;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.openAtlas.launcher.android.compat.ApplicationCompat;



public class PanguApplication extends ApplicationCompat {
    private static final Handler mAppHandler;
    private final AtomicInteger mCreationCount;
    private final List<CrossActivityLifecycleCallback> mCrossActivityLifecycleCallbacks;
    private final AtomicInteger mStartCount;
    private WeakReference<Activity> mWeakActivity;

    public static interface CrossActivityLifecycleCallback {
        void onCreated(Activity activity);

        void onDestroyed(Activity activity);

        void onStarted(Activity activity);

        void onStopped(Activity activity);
    }

    class CallbackRunable implements Runnable {
        final  PanguApplication mApplication;
        private CrossActivityLifecycleCallback mCrossActivityLifecycleCallback;
        private String name;

        public CallbackRunable(PanguApplication panguApplication, CrossActivityLifecycleCallback crossActivityLifecycleCallback, String str) {
            this.mApplication = panguApplication;
            this.mCrossActivityLifecycleCallback = crossActivityLifecycleCallback;
            this.name = str;
        }

        @Override
		public void run() {
            if (this.mApplication.mWeakActivity != null) {
                Activity activity = this.mApplication.mWeakActivity.get();
                if (!(activity == null || this.mCrossActivityLifecycleCallback == null)) {
                    if ("onCreated".equals(this.name)) {
                        this.mCrossActivityLifecycleCallback.onCreated(activity);
                    } else if ("onStarted".equals(this.name)) {
                        this.mCrossActivityLifecycleCallback.onStarted(activity);
                    }
                }
            }
            this.mCrossActivityLifecycleCallback = null;
            this.name = null;
        }
    }

    class ActivityLifecycleCallbacksCompatImpl implements ActivityLifecycleCallbacksCompat {
        final  PanguApplication mApplication;

        ActivityLifecycleCallbacksCompatImpl(PanguApplication panguApplication) {
            this.mApplication = panguApplication;
        }

        @Override
		public void onActivityCreated(Activity activity,  Bundle bundle) {
            this.mApplication.mWeakActivity = new WeakReference<Activity>(activity);
            if (this.mApplication.mCreationCount.getAndIncrement() == 0 && !this.mApplication.mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (CrossActivityLifecycleCallback onCreated : this.mApplication.mCrossActivityLifecycleCallbacks) {
                    onCreated.onCreated(activity);
                }
            }
        }

        @Override
		public void onActivityStarted(Activity activity) {
            if (this.mApplication.mStartCount.getAndIncrement() == 0 && !this.mApplication.mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (CrossActivityLifecycleCallback onStarted : this.mApplication.mCrossActivityLifecycleCallbacks) {
                    onStarted.onStarted(activity);
                }
            }
        }

        @Override
		public void onActivityStopped(Activity activity) {
            if (this.mApplication.mStartCount.decrementAndGet() == 0 && !this.mApplication.mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (CrossActivityLifecycleCallback onStopped : this.mApplication.mCrossActivityLifecycleCallbacks) {
                    onStopped.onStopped(activity);
                }
            }
        }

        @Override
		public void onActivityDestroyed(Activity activity) {
            if (this.mApplication.mCreationCount.decrementAndGet() == 0 && !this.mApplication.mCrossActivityLifecycleCallbacks.isEmpty()) {
                for (CrossActivityLifecycleCallback onDestroyed : this.mApplication.mCrossActivityLifecycleCallbacks) {
                    onDestroyed.onDestroyed(activity);
                }
            }
        }

        @Override
		public void onActivityResumed(Activity activity) {
        }

        @Override
		public void onActivityPaused(Activity activity) {
        }

        @Override
		public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }
    }

    public PanguApplication() {
        this.mCrossActivityLifecycleCallbacks = new CopyOnWriteArrayList<CrossActivityLifecycleCallback>();
        this.mCreationCount = new AtomicInteger();
        this.mStartCount = new AtomicInteger();
    }

    public void registerCrossActivityLifecycleCallback(CrossActivityLifecycleCallback crossActivityLifecycleCallback) {
        if (crossActivityLifecycleCallback == null) {
            new RuntimeException("registerCrossActivityLifecycleCallback must not be null").fillInStackTrace();
          
            return;
        }
        this.mCrossActivityLifecycleCallbacks.add(crossActivityLifecycleCallback);
        if (this.mCreationCount.get() > 0) {
            mAppHandler.post(new CallbackRunable(this, crossActivityLifecycleCallback, "onCreated"));
        }
        if (this.mStartCount.get() > 0) {
            mAppHandler.post(new CallbackRunable(this, crossActivityLifecycleCallback, "onStarted"));
        }
    }

    public void unregisterCrossActivityLifecycleCallback(CrossActivityLifecycleCallback crossActivityLifecycleCallback) {
        this.mCrossActivityLifecycleCallbacks.remove(crossActivityLifecycleCallback);
    }

    public static void runOnUiThread(Runnable runnable) {
        mAppHandler.post(runnable);
    }

    @Override
	public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksCompatImpl(this));
     
    }

    static {
        mAppHandler = new Handler();
    }
}