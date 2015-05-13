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
package com.openAtlas.runtime;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Application;

import com.openAtlas.log.Logger;
import com.openAtlas.log.LoggerFactory;

public class DelegateComponent {
    static Map<String, Application> apkApplications;
    private static Map<String, PackageLite> apkPackages;
    static final Logger log;

    static {
        log = LoggerFactory.getInstance("DelegateComponent");
        apkPackages = new ConcurrentHashMap<String, PackageLite>();
        apkApplications = new HashMap<String, Application>();
    }

    public static PackageLite getPackage(String mLocation) {
        return apkPackages.get(mLocation);
    }

    public static void putPackage(String mLocation, PackageLite packageLite) {
        apkPackages.put(mLocation, packageLite);
    }

    public static void removePackage(String mLocation) {
        apkPackages.remove(mLocation);
    }

    public static String locateComponent(String mComponent) {
        for (Entry<String, PackageLite> entry : apkPackages.entrySet()) {
            if (entry.getValue().components.contains(mComponent)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
