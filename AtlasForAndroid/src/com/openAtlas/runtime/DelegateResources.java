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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Bundle;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.openAtlas.framework.BundleImpl;
import com.openAtlas.framework.Framework;
import com.openAtlas.hack.AndroidHack;
import com.openAtlas.hack.AtlasHacks;
import com.openAtlas.log.Logger;
import com.openAtlas.log.LoggerFactory;


public class DelegateResources extends Resources {
	private static final String WebViewGoogleAssetPath = "/system/app/WebViewGoogle/WebViewGoogle.apk";
	private static Set<String> assetPathsHistory;
	private static Object lock;
	static final Logger log;
	private static List<String> mOriginAssetsPath;
	private Map<String, Integer> resIdentifierMap;

	static class DelegateResourcesGetter implements Runnable {
		Application application;
		String newPath;
		Resources res;

		public DelegateResourcesGetter(Application application, Resources resources, String str) {
			this.application = application;
			this.res = resources;
			this.newPath = str;
		}

		@Override
		public void run() {
			try {
				DelegateResources.newDelegateResourcesInternal(this.application, this.res, this.newPath);
				synchronized (DelegateResources.lock) {
					DelegateResources.lock.notify();
				}
			} catch (Exception e) {
				e.printStackTrace();
				synchronized (DelegateResources.lock) {
				}
				DelegateResources.lock.notify();
			} catch (Throwable th) {
				synchronized (DelegateResources.lock) {
				}
				DelegateResources.lock.notify();
			}
		}
	}

	static {
		log = LoggerFactory.getInstance("DelegateResources");
		lock = new Object();
		mOriginAssetsPath = null;
	}

	public DelegateResources(AssetManager assetManager, Resources resources) {
		super(assetManager, resources.getDisplayMetrics(), resources.getConfiguration());
		this.resIdentifierMap = new ConcurrentHashMap<String, Integer>();
	}

	public static void newDelegateResources(Application application, Resources resources, String str) throws Exception {
		if (Thread.currentThread().getId() == Looper.getMainLooper().getThread().getId()) {
			newDelegateResourcesInternal(application, resources, str);
			return;
		}
		synchronized (lock) {
			new Handler(Looper.getMainLooper()).post(new DelegateResourcesGetter(application, resources, str));
			lock.wait();
		}
	}

	private static void newDelegateResourcesInternal(Application application, Resources resources, String str) throws Exception {
		Set<String> generateNewAssetPaths = generateNewAssetPaths(application, str);
		if (generateNewAssetPaths != null) {
			Resources delegateResources;
			AssetManager assetManager = AssetManager.class.newInstance();
			for (String str2 : generateNewAssetPaths) {
				AtlasHacks.AssetManager_addAssetPath.invoke(assetManager, str2);
			}
			if (resources == null || !resources.getClass().getName().equals("android.content.res.MiuiResources")) {
				delegateResources = new DelegateResources(assetManager, resources);
			} else {
				Constructor<?> declaredConstructor = Class.forName("android.content.res.MiuiResources").getDeclaredConstructor(new Class[]{AssetManager.class, DisplayMetrics.class, Configuration.class});
				declaredConstructor.setAccessible(true);
				delegateResources = (Resources) declaredConstructor.newInstance(new Object[]{assetManager, resources.getDisplayMetrics(), resources.getConfiguration()});
			}
			RuntimeVariables.setDelegateResources(delegateResources);
			AndroidHack.injectResources(application, delegateResources);
			assetPathsHistory = generateNewAssetPaths;
			if (log.isDebugEnabled()) {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("newDelegateResources [");
				for (String append : generateNewAssetPaths) {
					stringBuffer.append(append).append(",");
				}
				stringBuffer.append("]");
				if (str != null) {
					stringBuffer.append("Add new path:" + str);
				}
				log.debug(stringBuffer.toString());
			}
		}
	}

	public static List<String> getOriginAssetsPath(AssetManager assetManager) {
		List<String> arrayList = new ArrayList<String>();
		try {
			Method declaredMethod = assetManager.getClass().getDeclaredMethod("getStringBlockCount", new Class[0]);
			declaredMethod.setAccessible(true);
			int intValue = ((Integer) declaredMethod.invoke(assetManager, new Object[0])).intValue();
			for (int i = 0; i < intValue; i++) {
				String str = (String) assetManager.getClass().getMethod("getCookieName", new Class[]{Integer.TYPE}).invoke(assetManager, new Object[]{Integer.valueOf(i + 1)});
				if (!TextUtils.isEmpty(str)) {
					arrayList.add(str);
				}
			}
			return arrayList;
		} catch (Throwable th) {
			th.printStackTrace();
			
			arrayList.clear();
			return arrayList;
		}
	}

	public static String getAssetHistoryPaths() {
		if (assetPathsHistory == null) {
			return "";
		}
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("newDelegateResources [");
		for (String append : assetPathsHistory) {
			stringBuffer.append(append).append(",");
		}
		stringBuffer.append("]");
		return stringBuffer.toString();
	}

	private static Set<String> generateNewAssetPaths(Application application, String str) {
		//Set<String> linkedHashSet = null;
		if (str != null && assetPathsHistory != null && assetPathsHistory.contains(str)) {
			return null;
		}
		Set<String> linkedHashSet = new LinkedHashSet<String>();
		linkedHashSet.add(application.getApplicationInfo().sourceDir);
		if (VERSION.SDK_INT > 20) {
			try {
				if (mOriginAssetsPath == null || mOriginAssetsPath.indexOf(WebViewGoogleAssetPath) == -1) {
					mOriginAssetsPath = getOriginAssetsPath(application.getResources().getAssets());
				}
				if (!(mOriginAssetsPath == null || mOriginAssetsPath.indexOf(WebViewGoogleAssetPath) == -1)) {
					linkedHashSet.add(WebViewGoogleAssetPath);
				}
				if (assetPathsHistory != null) {
					linkedHashSet.addAll(assetPathsHistory);
				}
				if (str == null) {
					return linkedHashSet;
				}
				linkedHashSet.add(str);
				return linkedHashSet;
			} catch (Throwable th) {
				
				if (assetPathsHistory != null) {
					linkedHashSet.addAll(assetPathsHistory);
				}
				if (str != null) {
					return linkedHashSet;
				}
				linkedHashSet.add(str);
				return linkedHashSet;
			}
		}
		if (assetPathsHistory != null) {
			linkedHashSet.addAll(assetPathsHistory);
		}
		if (str == null) {
			return linkedHashSet;
		}
		linkedHashSet.add(str);
		return linkedHashSet;
	}

	@Override
	public int getIdentifier(String str, String str2, String str3) {
		int identifier = super.getIdentifier(str, str2, str3);
		if (identifier != 0) {
			return identifier;
		}
		if (VERSION.SDK_INT <= 19) {
			return 0;
		}
		if (str2 == null && str3 == null) {
			String substring = str.substring(str.indexOf("/") + 1);
			str2 = str.substring(str.indexOf(":") + 1, str.indexOf("/"));
			str = substring;
		}
		if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
			return 0;
		}
		List<?> bundles = Framework.getBundles();
		if (!(bundles == null || bundles.isEmpty())) {
			for (Bundle bundle : Framework.getBundles()) {
				String location = bundle.getLocation();
				String str4 = location + ":" + str;
				if (!this.resIdentifierMap.isEmpty() && this.resIdentifierMap.containsKey(str4)) {
					int intValue = this.resIdentifierMap.get(str4).intValue();
					if (intValue != 0) {
						return intValue;
					}
				}
				BundleImpl bundleImpl = (BundleImpl) bundle;
				if (bundleImpl.getArchive().isDexOpted()) {
					ClassLoader classLoader = bundleImpl.getClassLoader();
					if (classLoader != null) {
						try {
							StringBuilder stringBuilder = new StringBuilder(location);
							stringBuilder.append(".R$");
							stringBuilder.append(str2);
							identifier = getFieldValueOfR(classLoader.loadClass(stringBuilder.toString()), str);
							if (identifier != 0) {
								this.resIdentifierMap.put(str4, Integer.valueOf(identifier));
								return identifier;
							}
						} catch (ClassNotFoundException e) {
						}
					} else {
						continue;
					}
				}
			}
		}
		return 0;
	}

	@Override
	public String getString(int i) throws NotFoundException {
		if (VERSION.SDK_INT < 21 || (i != 33816578 && i != 262146 && i != 50593794)) {
			return super.getString(i);
		}
		return "Web View";
	}

	private static int getFieldValueOfR(Class<?> cls, String str) {
		if (cls != null) {
			try {
				Field declaredField = cls.getDeclaredField(str);
				if (declaredField != null) {
					if (!declaredField.isAccessible()) {
						declaredField.setAccessible(true);
					}
					return ((Integer) declaredField.get(null)).intValue();
				}
			} catch (NoSuchFieldException e) {
			} catch (IllegalAccessException e2) {
			} catch (IllegalArgumentException e3) {
			}
		}
		return 0;
	}
}