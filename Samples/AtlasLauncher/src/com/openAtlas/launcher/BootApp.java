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
package com.openAtlas.launcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.os.Build.VERSION;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.openAtlas.boot.Globals;
import com.openAtlas.launcher.Atlaswrapper.AtlasInitializer;
import com.openAtlas.launcher.Atlaswrapper.BundleParser;
import com.openAtlas.launcher.android.lifecycle.PanguApplication;
import com.openAtlas.runtime.ContextImplHook;
import com.openAtlas.runtime.RuntimeVariables;
import com.openAtlas.util.Utils;


public class BootApp extends PanguApplication {


	static final String TAG = "TestApp";

	private String processName;


	private Context mBaseContext;
	AtlasInitializer mAtlasInitializer;
	public static BootApp instaceApp;
    private PackageManager mPackageManager;
    private InvocationHandlerImpl mPackageManagerProxyhandler;
    private PackageInfo mPackageInfo;
	@Override
	protected void attachBaseContext(Context context) {
		super.attachBaseContext(context);
		this.mBaseContext = context;
		try {
			Field declaredField = Globals.class
					.getDeclaredField("sInstalledVersionName");
			declaredField.setAccessible(true);
			declaredField.set(null, this.mBaseContext.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0).versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int myPid = Process.myPid();
		for (RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
				.getRunningAppProcesses()) {
			if (runningAppProcessInfo.pid == myPid) {
				this.processName = runningAppProcessInfo.processName;
				break;
			}
		}
		this.mAtlasInitializer = new AtlasInitializer(this,getPackageName(), getApplicationContext());
		// this.mAtlasInitializer.injectApplication();
		// initCrashHandlerAndSafeMode(this.mBaseContext);
		this.mAtlasInitializer.init();
		BundleParser.parser(getBaseContext());
	}

	@Override
	public void onCreate() {

		super.onCreate();
		instaceApp = this;

		this.mAtlasInitializer.startUp();
		RuntimeVariables.setDelegateResources(getResources());

	}

	@Override
	public boolean bindService(Intent intent,
			ServiceConnection serviceConnection, int i) {
		return new ContextImplHook(getBaseContext(), null).bindService(intent,
				serviceConnection, i);
	}

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		// super.startActivity(intent);
		new ContextImplHook(getBaseContext(), getClassLoader())
		.startActivity(intent);
	}



	@Override
	public ComponentName startService(Intent intent) {
		return new ContextImplHook(getBaseContext(), null).startService(intent);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String str, int i, CursorFactory cursorFactory) {
		String processName =Utils.getProcessName();
		if (!TextUtils.isEmpty(processName)) {
			Log.i("SQLiteDatabase", processName);
			if (!processName.equals(getPackageName())) {
				String[] split = processName.split(":");
				if (split != null && split.length > 1) {
					processName = split[1] + "_" + str;
					Log.i("SQLiteDatabase", "openOrCreateDatabase:" + processName);
					return hookDatabase(processName, i, cursorFactory);
				}
			}
		}
		return hookDatabase(str, i, cursorFactory);
	}
	public SQLiteDatabase hookDatabase(String name, int mode, CursorFactory cursorFactory) {
		if (VERSION.SDK_INT >= 11) {
			return super.openOrCreateDatabase(name, mode, cursorFactory);
		}
		SQLiteDatabase sQLiteDatabase = null;
		try {
			return super.openOrCreateDatabase(name, mode, cursorFactory);
		} catch (SQLiteException e) {
			e.printStackTrace();
			if (Globals.getApplication().deleteDatabase(name)) {
				return super.openOrCreateDatabase(name, mode, cursorFactory);
			}
			return sQLiteDatabase;
		}
	}

    @Override
	public PackageManager getPackageManager() {
        if (this.mPackageManager != null) {
            return this.mPackageManager;
        }
        try {
            Class<?> clsIPackageManager = Class.forName("android.content.pm.IPackageManager");
            Class<?> clsActivityThread = Class.forName("android.app.ActivityThread");
            Method declaredMethod = clsActivityThread.getDeclaredMethod("getPackageManager", new Class[0]);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(clsActivityThread, new Object[0]);
            if (invoke != null) {
                if (this.mPackageManagerProxyhandler == null) {
                    this.mPackageManagerProxyhandler = new InvocationHandlerImpl(this, invoke);
                }
                invoke = Proxy.newProxyInstance(getClassLoader(), new Class[]{clsIPackageManager}, this.mPackageManagerProxyhandler);
                Constructor<?> declaredConstructor = Class.forName("android.app.ApplicationPackageManager").getDeclaredConstructor(new Class[]{Class.forName("android.app.ContextImpl"), clsIPackageManager});
                declaredConstructor.setAccessible(true);
                this.mPackageManager = (PackageManager) declaredConstructor.newInstance(new Object[]{this.mBaseContext, invoke});
                return this.mPackageManager;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getPackageManager();
    }


    public class InvocationHandlerImpl implements InvocationHandler {
        final  BootApp mApp;
        private Object obj;

        public InvocationHandlerImpl(BootApp taobaoApplication, Object obj) {
            this.mApp = taobaoApplication;
            this.obj = obj;
        }

        @Override
		public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
            Object invoke = method.invoke(this.obj, objArr);
            if (!method.getName().equals("getPackageInfo") || objArr[0] == null || !objArr[0].equals(this.mApp.getPackageName())) {
                return invoke;
            }
            PackageInfo packageInfo = (PackageInfo) invoke;
            String str = packageInfo.versionName;
            if (packageInfo.versionCode > BaselineInfoProvider.getInstance().getMainVersionCode()) {
                this.mApp.mPackageInfo = packageInfo;
                return this.mApp.mPackageInfo;
            }
            BaselineInfoProvider.getInstance().getMainVersionName();
            str = BaselineInfoProvider.getInstance().getBaselineVersion();
            if (TextUtils.isEmpty(str)) {
                return this.mApp.mPackageInfo;
            }
            packageInfo.versionName = str;
            this.mApp.mPackageInfo = packageInfo;
            return this.mApp.mPackageInfo;
        }
    }


}
