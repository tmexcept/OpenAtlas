/**OpenAtlasForAndroid Project

The MIT License (MIT) 
Copyright (c) 2015 Bunny Blue

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
/**
 * @author BunnyBlue
 */
package com.openAtlas.launcher.Atlaswrapper;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.openAtlas.boot.PlatformConfigure;



public class Utils {

    static final String[] DELAY;
    static final String[] AUTO;
    static final String[] STORE;

    static {
    	DELAY = new String[]{"com.openatlas.qrcode"};
        AUTO = new String[]{"com.openatlas.homelauncher","com.openatlas.qrcode","com.taobao.android.game20x7a","com.taobao.universalimageloader.sample0x6a"};
        STORE = new String[]{"com.taobao.android.game20x7a","com.taobao.android.gamecenter","com.taobao.universalimageloader.sample0x6a"};
    }

    public static String getFileNameFromEntryName(String str) {
        return str.substring(str.indexOf("lib/armeabi/") + "lib/armeabi/".length());
    }

    public static String getPackageNameFromEntryName(String str) {
    	//return str.replace("_so", ".so");
    return str.substring(str.indexOf("lib/armeabi/lib") + "lib/armeabi/lib".length(), str.indexOf(".so")).replace("_", ".");
    }

    public static String getPackageNameFromSoName(String str) {
        return str.substring(str.indexOf("lib") + "lib".length(), str.indexOf(".so")).replace("_", ".");
    }

    public static String getBaseFileName(String str) {
        int lastIndexOf = str.lastIndexOf(".");
        if (lastIndexOf > 0) {
            return str.substring(0, lastIndexOf);
        }
        return str;
    }

    public static PackageInfo getPackageInfo(Application application) {
        try {
            return application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
        } catch (Throwable e) {
            Log.e("Utils", "Error to get PackageInfo >>>", e);
            return new PackageInfo();
        }
    }

    public static void saveAtlasInfoBySharedPreferences(Application application) {
        Map<String,String> concurrentHashMap = new ConcurrentHashMap<String,String>();
        concurrentHashMap.put(getPackageInfo(application).versionName, "dexopt");
        SharedPreferences sharedPreferences = application.getSharedPreferences("atlas_configs", 0);
        if (sharedPreferences == null) {
            sharedPreferences = application.getSharedPreferences("atlas_configs", 0);
        }
        Editor edit = sharedPreferences.edit();
        for (String str : concurrentHashMap.keySet()) {
            edit.putString(str, concurrentHashMap.get(str));
        }
        edit.commit();
    }

    public static void UpdatePackageVersion(Application application) {
        PackageInfo packageInfo = getPackageInfo(application);
        Editor edit = application.getSharedPreferences("atlas_configs", 0).edit();
        edit.putInt("last_version_code", packageInfo.versionCode);
        edit.putString("last_version_name", packageInfo.versionName);
        edit.putString(packageInfo.versionName, "dexopt");
        edit.commit();
    }

    public static void notifyBundleInstalled(Application application) {
        System.setProperty("BUNDLES_INSTALLED", "true");
        application.sendBroadcast(new Intent(PlatformConfigure.ACTION_BROADCAST_BUNDLES_INSTALLED));
    }

    public static boolean searchFile(String str, String str2) {
        if (str == null || str2 == null) {
            Log.e("Utils", "error in search File, direcoty or keyword is null");
            return false;
        }
        File file = new File(str);
        if (file == null || !file.exists()) {
            Log.e("Utils", "error in search File, can not open directory " + str);
            return false;
        }
        File[] listFiles = new File(str).listFiles();
        if (listFiles == null || listFiles.length <= 0) {
            return false;
        }
        for (File file2 : listFiles) {
            if (file2.getName().indexOf(str2) >= 0) {
               Log.d("Util",  "the file search success " + file2.getName() + " keyword is " + str2);
                return true;
            }
        }
    Log.e("Util",    "the file search failed directory is " + str + " keyword is " + str2);
        return false;
    }
}