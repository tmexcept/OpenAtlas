/**
 *  OpenAtlasForAndroid
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
package com.openAtlas.bundleInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

public class BundleInfoList {
    private static BundleInfoList singleton;
    private final String TAG;
    private List<BundleInfo> mBundleInfoList;

    public static class BundleInfo {
        public List<String> Components;
        public List<String> DependentBundles;
        public String bundleName;
        public boolean hasSO;
    }

    private BundleInfoList() {
        this.TAG = BundleInfoList.class.getSimpleName();
    }

    public static synchronized BundleInfoList getInstance() {
        BundleInfoList bundleInfoList;
        synchronized (BundleInfoList.class) {
            if (singleton == null) {
                singleton = new BundleInfoList();
            }
            bundleInfoList = singleton;
        }
        return bundleInfoList;
    }

    public synchronized boolean init(ArrayList<BundleInfo> linkedList) {
        boolean initilized;
      
        if (this.mBundleInfoList != null || linkedList == null) {
      
            Log.i(TAG, "XXXXXBundleInfoList initialization failed.");
            initilized = false;
        } else {
            this.mBundleInfoList = linkedList;
            initilized = true;
        }
        return initilized;
    }


    public List<String> getDependencyForBundle(String mBundleName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.size() == 0) {
            return null;
        }
        for (BundleInfo bundleInfo : this.mBundleInfoList) {
            if (bundleInfo.bundleName.equals(mBundleName)) {
                List<String> arrayList = new ArrayList<String>();
                if (!(bundleInfo == null || bundleInfo.DependentBundles == null)) {
                    for (int i = 0; i < bundleInfo.DependentBundles.size(); i++) {
                        if (!TextUtils.isEmpty(bundleInfo.DependentBundles.get(i))) {
                            arrayList.add(bundleInfo.DependentBundles.get(i));
                        }
                    }
                }
                return arrayList;
            }
        }
        return null;
    }

    public boolean getHasSO(String mBundleName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.isEmpty()) {
            return false;
        }
        for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
        	BundleInfo bundleInfo = this.mBundleInfoList.get(index);
            if (bundleInfo.bundleName.equals(mBundleName)) {
                return bundleInfo.hasSO;
            }
        }
        return false;
    }

    public String getBundleNameForComponet(String mComponentName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.size() == 0) {
            return null;
        }
        for (BundleInfo bundleInfo : this.mBundleInfoList) {
            for (String equals : bundleInfo.Components) {
                if (equals.equals(mComponentName)) {
                    return bundleInfo.bundleName;
                }
            }
        }
        return null;
    }


    public List<String> getAllBundleNames() {
        if (this.mBundleInfoList == null || this.mBundleInfoList.isEmpty()) {
            return null;
        }
        LinkedList<String> linkedList = new LinkedList<String>();
        for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
        	BundleInfo bundleInfo = this.mBundleInfoList.get(index);
            linkedList.add(bundleInfo.bundleName);
        }
        return linkedList;
    }

    public BundleInfo getBundleInfo(String mBundleName) {
        if (this.mBundleInfoList == null || this.mBundleInfoList.isEmpty()) {
            return null;
        }
        for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
        	BundleInfo bundleInfo = this.mBundleInfoList.get(index);
            if (bundleInfo.bundleName.equals(mBundleName)) {
                return bundleInfo;
            }
        }
        return null;
    }

    public void print() {
    	System.out.println("BundleInfoList.print()");
        if (this.mBundleInfoList != null && this.mBundleInfoList.isEmpty()) {
            for (int index = 0; index < this.mBundleInfoList.size(); ++index) {
            	BundleInfo bundleInfo = this.mBundleInfoList.get(index);
                Log.i(TAG, "BundleName: " + bundleInfo.bundleName);
                for (String str : bundleInfo.Components) {
                    Log.i(TAG, "****components: " + str);
                }
                for (String str2 : bundleInfo.DependentBundles) {
                    Log.i(TAG, "****dependancy: " + str2);
                }
            }
        }
    }
}
