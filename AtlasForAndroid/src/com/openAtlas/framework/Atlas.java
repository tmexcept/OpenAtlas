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
package com.openAtlas.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.openAtlas.hack.AndroidHack;
import com.openAtlas.hack.AssertionArrayException;
import com.openAtlas.hack.AtlasHacks;
import com.openAtlas.log.Logger;
import com.openAtlas.log.LoggerFactory;
import com.openAtlas.runtime.BundleLifecycleHandler;
import com.openAtlas.runtime.ClassLoadFromBundle;
import com.openAtlas.runtime.ClassNotFoundInterceptorCallback;
import com.openAtlas.runtime.DelegateClassLoader;
import com.openAtlas.runtime.DelegateComponent;
import com.openAtlas.runtime.FrameworkLifecycleHandler;
import com.openAtlas.runtime.InstrumentationHook;
import com.openAtlas.runtime.PackageLite;
import com.openAtlas.runtime.RuntimeVariables;

public class Atlas {
    protected static Atlas instance;
    static final Logger log;
    private BundleLifecycleHandler bundleLifecycleHandler;
    private FrameworkLifecycleHandler frameworkLifecycleHandler;

    static {
        log = LoggerFactory.getInstance("Atlas");
    }

    private Atlas() {
    }

    public static  Atlas getInstance() {
      
    	
    	if (instance!=null) {
			return instance;
		}
        synchronized (Atlas.class) {
            if (instance == null) {
                instance = new Atlas();
            }
            
        }
        return instance;
    }

    public void init(Application application)
            throws AssertionArrayException, Exception {
        String packageName = application.getPackageName();
        AtlasHacks.defineAndVerify();
        ClassLoader classLoader = Atlas.class.getClassLoader();
        DelegateClassLoader delegateClassLoader = new DelegateClassLoader(
                classLoader);
        Framework.systemClassLoader = classLoader;
        RuntimeVariables.delegateClassLoader = delegateClassLoader;
        RuntimeVariables.setDelegateResources(application.getResources());
        RuntimeVariables.androidApplication = application;
        AndroidHack.injectClassLoader(packageName, delegateClassLoader);
        AndroidHack
                .injectInstrumentationHook(new InstrumentationHook(AndroidHack
                        .getInstrumentation(), application.getBaseContext()));
        injectApplication(application, packageName);
        this.bundleLifecycleHandler = new BundleLifecycleHandler();
        Framework.syncBundleListeners.add(this.bundleLifecycleHandler);
        this.frameworkLifecycleHandler = new FrameworkLifecycleHandler();
        Framework.frameworkListeners.add(this.frameworkLifecycleHandler);
        AndroidHack.hackH();
       // Framework.initialize(properties);
    }

    public void injectApplication(Application application, String packageName)
            throws Exception {
        AtlasHacks.defineAndVerify();
        AndroidHack.injectApplication(packageName, application);
    }
    public void startup(Properties properties) throws BundleException {
        Framework.startup(properties);
    }

//    public void startup() throws BundleException {
//        Framework.startup();
//    }

    public void shutdown() throws BundleException {
        Framework.shutdown(false);
    }

    public Bundle getBundle(String pkgName) {
        return Framework.getBundle(pkgName);
    }
    public Bundle getBundleOnDemand(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (Framework.getBundle(str) == null) {
            ClassLoadFromBundle.checkInstallBundleAndDependency(str);
        }
        return Framework.getBundle(str);
    }

    public Bundle installBundle(String str, InputStream inputStream)
            throws BundleException {
        return Framework.installNewBundle(str, inputStream);
    }

    public Bundle installBundle(String str, File file) throws BundleException {
        return Framework.installNewBundle(str, file);
    }

    public void updateBundle(String pkgName, InputStream inputStream)
            throws BundleException {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            bundle.update(inputStream);
            return;
        }
        throw new BundleException("Could not update bundle " + pkgName
                + ", because could not find it");
    }

    public void updateBundle(String pkgName, File file) throws BundleException {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            bundle.update(file);
            return;
        }
        throw new BundleException("Could not update bundle " + pkgName
                + ", because could not find it");
    }

    public void installOrUpdate(String[] strArr, File[] fileArr)
            throws BundleException {
        Framework.installOrUpdate(strArr, fileArr);
    }

    public void uninstallBundle(String pkgName) throws BundleException {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            BundleImpl bundleImpl = (BundleImpl) bundle;
            try {
                File archiveFile = bundleImpl.getArchive().getArchiveFile();
                if (archiveFile.canWrite()) {
                    archiveFile.delete();
                }
                bundleImpl.getArchive().purge();
                File revisionDir = bundleImpl.getArchive().getCurrentRevision()
                        .getRevisionDir();
                bundle.uninstall();
                if (revisionDir != null) {
                    Framework.deleteDirectory(revisionDir);
                    return;
                }
                return;
            } catch (Exception e) {
                log.error("uninstall bundle error: " + pkgName + e.getMessage());
                return;
            }
        }
        throw new BundleException("Could not uninstall bundle " + pkgName
                + ", because could not find it");
    }

    public List<Bundle> getBundles() {
        return Framework.getBundles();
    }

    public Resources getDelegateResources() {
        return RuntimeVariables.getDelegateResources();
    }

    public ClassLoader getDelegateClassLoader() {
        return RuntimeVariables.delegateClassLoader;
    }

    public Class<?> getComponentClass(String pkgName) throws ClassNotFoundException {
        return RuntimeVariables.delegateClassLoader.loadClass(pkgName);
    }

    public ClassLoader getBundleClassLoader(String pkgName) {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            return ((BundleImpl) bundle).getClassLoader();
        }
        return null;
    }

    public PackageLite getBundlePackageLite(String pkgName) {
        return DelegateComponent.getPackage(pkgName);
    }

    public File getBundleFile(String pkgName) {
        Bundle bundle = Framework.getBundle(pkgName);
        if (bundle != null) {
            return ((BundleImpl) bundle).archive.getArchiveFile();
        }
        return null;
    }

    public InputStream openAssetInputStream(String str, String str2)
            throws IOException {
        Bundle bundle = Framework.getBundle(str);
        if (bundle != null) {
            return ((BundleImpl) bundle).archive.openAssetInputStream(str2);
        }
        return null;
    }

    public InputStream openNonAssetInputStream(String str, String str2)
            throws IOException {
        Bundle bundle = Framework.getBundle(str);
        if (bundle != null) {
            return ((BundleImpl) bundle).archive.openNonAssetInputStream(str2);
        }
        return null;
    }

    public void addFrameworkListener(FrameworkListener frameworkListener) {
        Framework.addFrameworkListener(frameworkListener);
    }

    public void removeFrameworkListener(FrameworkListener frameworkListener) {
        Framework.removeFrameworkListener(frameworkListener);
    }

    public void addBundleListener(BundleListener bundleListener) {
        Framework.addBundleListener(bundleListener);
    }

    public void removeBundleListener(BundleListener bundleListener) {
        Framework.removeBundleListener(bundleListener);
    }

    public void onLowMemory() {
        this.bundleLifecycleHandler.handleLowMemory();
    }

    public void enableComponent(String str) {
        PackageLite packageLite = DelegateComponent.getPackage(str);
        if (packageLite != null && packageLite.disableComponents != null) {
            for (String str2 : packageLite.disableComponents) {
                PackageManager packageManager = RuntimeVariables.androidApplication
                        .getPackageManager();
                ComponentName componentName = new ComponentName(
                        RuntimeVariables.androidApplication.getPackageName(),
                        str2);
                try {
                    packageManager.setComponentEnabledSetting(componentName, 1,
                            1);
                    log.debug("enableComponent: "
                            + componentName.getClassName());
                } catch (Exception e) {
                    log.error("enableComponent error: "
                            + componentName.getClassName() + e.getMessage());
                }
            }
        }
    }

    public void setClassNotFoundInterceptorCallback(
            ClassNotFoundInterceptorCallback classNotFoundInterceptorCallback) {
        Framework.setClassNotFoundCallback(classNotFoundInterceptorCallback);
    }
}
