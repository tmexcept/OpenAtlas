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
package com.openAtlas.boot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Application;
import android.content.pm.PackageManager.NameNotFoundException;

public class Globals {
    private static Application sApplication;
    private static ClassLoader sClassLoader;
    private static String sInstalledVersionName;

    public static synchronized Application getApplication() {
        Application application;
        synchronized (Globals.class) {
            if (sApplication == null) {
                sApplication = getSystemApp();
            }
            application = sApplication;
        }
        return application;
    }

    public static synchronized ClassLoader getClassLoader() {
        ClassLoader classLoader;
        synchronized (Globals.class) {
            if (sClassLoader == null) {
                classLoader = getApplication().getClassLoader();
            } else {
                classLoader = sClassLoader;
            }
        }
        return classLoader;
    }

    private static Application getSystemApp() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod(
                    "currentActivityThread", new Class[0]);
            Field declaredField = cls.getDeclaredField("mInitialApplication");
            declaredField.setAccessible(true);
            return (Application) declaredField.get(declaredMethod.invoke(null,
                    new Object[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getVersionName() {
        try {
            return getApplication().getPackageManager().getPackageInfo(
                    getApplication().getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "null";
        }
    }

    public static String getInstalledVersionName() {
        return sInstalledVersionName;
    }

    public static int getVersionCode() {
        int i = 0;
        try {
            return getApplication().getPackageManager().getPackageInfo(
                    getApplication().getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return i;
        }
    }





}
