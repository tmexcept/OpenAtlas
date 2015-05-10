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

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;

import com.openAtlas.runtime.ClassNotFoundInterceptorCallback;


public class ClassNotFoundInterceptorCallbackImpl implements ClassNotFoundInterceptorCallback {
    public static final List<String> GO_H5_BUNDLES_IF_NOT_EXISTS;
    public final String TAG;

    public ClassNotFoundInterceptorCallbackImpl() {
        this.TAG = "ClassNotFundInterceptor";
    }

    static {
        GO_H5_BUNDLES_IF_NOT_EXISTS = new ArrayList<String>();
    }

    public static void addGoH5BundlesIfNotExists(String str) {
        if (!GO_H5_BUNDLES_IF_NOT_EXISTS.contains(str)) {
            GO_H5_BUNDLES_IF_NOT_EXISTS.add(str);
        }
    }

    public static void resetGoH5BundlesIfNotExists() {
        GO_H5_BUNDLES_IF_NOT_EXISTS.clear();
    }

    @Override
	public Intent returnIntent(Intent intent) {
        String className = intent.getComponent().getClassName();
        CharSequence dataString = intent.getDataString();
        //TODO implement 
        if (className == null
                || !className.equals("blue.stack.openAtlas.welcome.Welcome")) {
            // if (Globals.isMiniPackage()) {
            // Component findBundleByActivity =
            // BundlesInstaller.instance().findBundleByActivity(className);
            // if (!(findBundleByActivity == null ||
            // Atlas.getInstance().getBundle(findBundleByActivity.getPkgName())
            // != null ||
            // GO_H5_BUNDLES_IF_NOT_EXISTS.contains(findBundleByActivity.getPkgName())))
            // {
            // new Handler(Looper.getMainLooper()).post(new OptDexProcess(this,
            // intent, className, findBundleByActivity));
            // }
            // }
            // if (!TextUtils.isEmpty(dataString)) {
            // Nav.from(Globals.getApplication()).withCategory(ShopUrlFilter.BROWSER_ONLY_CATEGORY).withExtras(intent.getExtras()).toUri(intent.getData());
            // }
        }
        return intent;
    }
}
