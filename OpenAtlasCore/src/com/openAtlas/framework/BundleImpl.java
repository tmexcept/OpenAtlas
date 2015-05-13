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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import com.openAtlas.framework.bundlestorage.Archive;
import com.openAtlas.framework.bundlestorage.BundleArchive;
import com.openAtlas.log.Logger;
import com.openAtlas.log.LoggerFactory;
import com.openAtlas.util.AtlasFileLock;
import com.openAtlas.util.Constants;
import com.openAtlas.util.StringUtils;

public final class BundleImpl implements Bundle {
    static final Logger log;
    Archive archive;
    final File bundleDir;
    BundleClassLoader classloader;
    private final BundleContextImpl context;
    int currentStartlevel;
    ProtectionDomain domain;
    Hashtable<String, String> headers = new Hashtable<String, String>();
    final String location;
    boolean persistently;
    List<BundleListener> registeredBundleListeners;
    List<FrameworkListener> registeredFrameworkListeners;
    List<ServiceListener> registeredServiceListeners;
    List<ServiceReference> registeredServices;
    Package[] staleExportedPackages;
    int state=0;

    static {
        log = LoggerFactory.getInstance("BundleImpl");
    }

    BundleImpl(File bundleDir, String location, BundleContextImpl bundleContextImpl,
            InputStream archiveInputStream, File archiveFile, boolean isInstall)
            throws BundleException {
        this.persistently = false;
        this.domain = null;
        this.registeredServices = null;
        this.registeredFrameworkListeners = null;
        this.registeredBundleListeners = null;
        this.registeredServiceListeners = null;
        this.staleExportedPackages = null;
        long currentTimeMillis = System.currentTimeMillis();
        this.location = location;
        bundleContextImpl.bundle = this;
        this.context = bundleContextImpl;
        this.currentStartlevel = Framework.initStartlevel;
        this.bundleDir = bundleDir;
        if (archiveInputStream != null) {
            try {
                this.archive = new BundleArchive(location, bundleDir, archiveInputStream);
            } catch (Throwable e) {
                Framework.deleteDirectory(bundleDir);
                throw new BundleException("Could not install bundle " + location, e);
            }
        } else if (archiveFile != null) {
            try {
                this.archive = new BundleArchive(location, bundleDir, archiveFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.state = BundleEvent.STARTED;
        Framework.notifyBundleListeners(1, this);
        updateMetadata();
        if (isInstall) {
            Framework.bundles.put(location, this);
            resolveBundle(false);
        }
        if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
            log.info("Framework: Bundle " + toString() + " created. "
                    + (System.currentTimeMillis() - currentTimeMillis) + " ms");
        }
    }

    BundleImpl(File file, BundleContextImpl bundleContextImpl) throws Exception {
        this.persistently = false;
        this.domain = null;
        this.registeredServices = null;
        this.registeredFrameworkListeners = null;
        this.registeredBundleListeners = null;
        this.registeredServiceListeners = null;
        this.staleExportedPackages = null;
        long currentTimeMillis = System.currentTimeMillis();
        File file2 = new File(file, "meta");
        if (AtlasFileLock.getInstance().LockExclusive(file2)) {
            DataInputStream dataInputStream = new DataInputStream(
                    new FileInputStream(file2));
            this.location = dataInputStream.readUTF();
            this.currentStartlevel = dataInputStream.readInt();
            this.state = BundleEvent.STARTED;
            this.persistently = dataInputStream.readBoolean();
            dataInputStream.close();
            AtlasFileLock.getInstance().unLock(file2);
            bundleContextImpl.bundle = this;
            this.context = bundleContextImpl;
            this.bundleDir = file;
            try {
                this.archive = new BundleArchive(this.location, file);
                Framework.bundles.put(this.location, this);
                resolveBundle(false);
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " loaded. "
                            + (System.currentTimeMillis() - currentTimeMillis)
                            + " ms");
                    return;
                }
                return;
            } catch (Exception e) {
                throw new BundleException("Could not load bundle "
                        + this.location, e.getCause());
            }
        }
        throw new BundleException("FileLock failed " + file2.getAbsolutePath());
    }

    private synchronized void resolveBundle(boolean recursive) throws BundleException {
        if (this.state != 4) {
            if (this.classloader == null) {
                this.classloader = new BundleClassLoader(this);
            }
            if (recursive) {
                this.classloader.resolveBundle(true, new HashSet(0));
                this.state = 4;
            } else if (this.classloader.resolveBundle(false, null)) {
                this.state = 4;
            }
            Framework.notifyBundleListeners(0, this);
        }
    }

    @Override
	public long getBundleId() {
        return 0;
    }

    @Override
	public Dictionary<String, String> getHeaders() {
        return this.headers;
    }

    @Override
	public String getLocation() {
        return this.location;
    }

    public Archive getArchive() {
        return this.archive;
    }

    public ClassLoader getClassLoader() {
        return this.classloader;
    }

    @Override
	public ServiceReference[] getRegisteredServices() {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString()
                    + "has been unregistered.");
        } else if (this.registeredServices == null) {
            return null;
        } else {
            return this.registeredServices
                    .toArray(new ServiceReference[this.registeredServices
                            .size()]);
        }
    }

    @Override
	public URL getResource(String name) {
        if (this.state != BundleEvent.INSTALLED) {
            return this.classloader.getResource(name);
        }
        throw new IllegalStateException("Bundle " + toString()
                + " has been uninstalled");
    }

    @Override
	public ServiceReference[] getServicesInUse() {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString()
                    + "has been unregistered.");
        }
        ArrayList<ServiceReferenceImpl> arrayList = new ArrayList<ServiceReferenceImpl>();
        ServiceReferenceImpl[] serviceReferenceImplArr = Framework.services
                .toArray(new ServiceReferenceImpl[Framework.services.size()]);
        int i = 0;
        while (i < serviceReferenceImplArr.length) {
            synchronized (serviceReferenceImplArr[i].useCounters) {
                if (serviceReferenceImplArr[i].useCounters.get(this) != null) {
                    arrayList.add(serviceReferenceImplArr[i]);
                }
            }
            i++;
        }
        return arrayList
                .toArray(new ServiceReference[arrayList.size()]);
    }

    @Override
	public int getState() {
        return this.state;
    }

    @Override
	public boolean hasPermission(Object permission) {
        if (this.state != BundleEvent.INSTALLED) {
            return true;
        }
        throw new IllegalStateException("Bundle " + toString()
                + "has been unregistered.");
    }

    @Override
	public synchronized void start() throws BundleException {
        this.persistently = true;
        updateMetadata();
        if (this.currentStartlevel <= Framework.startlevel) {
            startBundle();
        }
    }

    public synchronized void startBundle() throws BundleException {
        state = 0;// TODO
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot start uninstalled bundle "
                    + toString());
        } else if (this.state != BundleEvent.RESOLVED) {
            if (this.state == BundleEvent.STARTED) {
                resolveBundle(true);
            }
            this.state =BundleEvent.UPDATED;
            try {
     
                this.context.isValid = true;
                if (!(this.classloader.activatorClassName == null || StringUtils
                        .isBlank(this.classloader.activatorClassName))) {
                    Class<?> loadClass = this.classloader
                            .loadClass(this.classloader.activatorClassName);
                    if (loadClass == null) {
                        throw new ClassNotFoundException(
                                this.classloader.activatorClassName);
                    }
                    this.classloader.activator = (BundleActivator) loadClass
                            .newInstance();
                    this.classloader.activator.start(this.context);

                }
                this.state = BundleEvent.RESOLVED;
                Framework.notifyBundleListeners(BundleEvent.STARTED, this);
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " started.");
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                String str = "Error starting bundle " + toString();
                if (th2.getCause() != null) {
                    th2 = th2.getCause();
                }
                BundleException bundleException = new BundleException(str, th2);
            }
        }
    }

    @Override
	public synchronized void stop() throws BundleException {
        this.persistently = false;
        updateMetadata();
        stopBundle();
    }

    public synchronized void stopBundle() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot stop uninstalled bundle "
                    + toString());
        } else if (this.state == BundleEvent.RESOLVED) {
            this.state = BundleEvent.UNINSTALLED;
            try {
                if (this.classloader.activator != null) {
                    this.classloader.activator.stop(this.context);
                }
                if (Framework.DEBUG_BUNDLES && log.isInfoEnabled()) {
                    log.info("Framework: Bundle " + toString() + " stopped.");
                }
                this.classloader.activator = null;
                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                Framework.notifyBundleListeners(BundleEvent.STOPPED, this);
                this.context.isValid = false;
            } catch (Throwable th) {
                this.classloader.activator = null;
                Framework.clearBundleTrace(this);
                this.state = BundleEvent.STOPPED;
                Framework.notifyBundleListeners(BundleEvent.STOPPED, this);
                this.context.isValid = false;
            }
        }
    }

    @Override
	public synchronized void uninstall() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Bundle " + toString()
                    + " is already uninstalled.");
        }
        if (this.state == BundleEvent.RESOLVED) {
            try {
                stopBundle();
            } catch (Throwable th) {
                Framework.notifyFrameworkListeners(BundleEvent.STARTED, this, th);
            }
        }
        this.state = BundleEvent.INSTALLED;
        File file = new File(this.bundleDir, "meta");
        if (AtlasFileLock.getInstance().LockExclusive(file)) {
            file.delete();
            AtlasFileLock.getInstance().unLock(file);
            if (this.classloader.originalExporter != null) {
                this.classloader.originalExporter.cleanup(true);
                this.classloader.originalExporter = null;
            }
            this.classloader.cleanup(true);
            this.classloader = null;
            Framework.bundles.remove(this);
            Framework.notifyBundleListeners(16, this);
            this.context.isValid = false;
            this.context.bundle = null;
        } else {
            throw new BundleException("FileLock failed "
                    + file.getAbsolutePath());
        }
    }

    @Override
	public synchronized void update() throws BundleException {
        String str = this.headers.get(Constants.BUNDLE_UPDATELOCATION);
        try {
            String str2;
            if (str == null) {
                str2 = this.location;
            } else {
                str2 = str;
            }
            update(new URL(str2).openConnection().getInputStream());
        } catch (Throwable e) {
            throw new BundleException("Could not update " + toString()
                    + " from " + str, e);
        }
    }

    @Override
	public synchronized void update(InputStream inputStream)
            throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot update uninstalled bundle "
                    + toString());
        }
        try {
            this.archive
                    .newRevision(this.location, this.bundleDir, inputStream);
        } catch (Throwable e) {
            throw new BundleException("Could not update bundle " + toString(),
                    e);
        }
    }

    @Override
	public synchronized void update(File bundleFile) throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException("Cannot update uninstalled bundle "
                    + toString());
        }
        try {
            this.archive.newRevision(this.location, this.bundleDir, bundleFile);
        } catch (Throwable e) {
            throw new BundleException("Could not update bundle " + toString(),
                    e);
        }
    }

    public synchronized void refresh() throws BundleException {
        if (this.state == BundleEvent.INSTALLED) {
            throw new IllegalStateException(
                    "Cannot refresh uninstalled bundle " + toString());
        }
        Object obj;
        if (this.state == BundleEvent.RESOLVED) {
            stopBundle();
            obj = 1;
        } else {
            obj = null;
        }
        try {
            this.archive = new BundleArchive(this.location, this.bundleDir);
            BundleClassLoader bundleClassLoader = new BundleClassLoader(this);
            String[] strArr = this.classloader.exports;
            if (strArr.length > 0) {
                int i = 0;
                Object obj2 = null;
                while (i < strArr.length) {
                    Object obj3;
                    Package packageR = Framework.exportedPackages
                            .get(new Package(strArr[i], null, false));
                    if (packageR.importingBundles == null
                            || packageR.classloader != this.classloader) {
                        obj3 = obj2;
                    } else {
                        packageR.removalPending = true;
                        obj3 = 1;
                    }
                    i++;
                    obj2 = obj3;
                }
                if (obj2 != null) {
                    if (this.classloader.originalExporter != null) {
                        bundleClassLoader.originalExporter = this.classloader.originalExporter;
                    } else {
                        bundleClassLoader.originalExporter = this.classloader;
                    }
                }
            }
            this.classloader.cleanup(true);
            this.classloader = bundleClassLoader;
            if (this.classloader.resolveBundle(false, null)) {
                this.state = BundleEvent.STOPPED;
            } else {
                this.state = BundleEvent.STARTED;
            }
            Framework.notifyBundleListeners(BundleEvent.UPDATED, this);
            if (obj != null) {
                startBundle();
            }
        } catch (BundleException e) {
            throw e;
        } catch (Throwable e2) {
            throw new BundleException("Could not refresh bundle " + toString(),
                    e2);
        }
    }

    public synchronized void optDexFile() {
        getArchive().optDexFile();
    }

    public synchronized void purge() throws BundleException {
        try {
            getArchive().purge();
        } catch (Throwable e) {
            throw new BundleException("Could not purge bundle " + toString(), e);
        }
    }

    void updateMetadata() {
        Throwable e;
        File file = new File(this.bundleDir, "meta");
        DataOutputStream dataOutputStream = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (AtlasFileLock.getInstance().LockExclusive(file)) {
                OutputStream fileOutputStream = new FileOutputStream(file);
                DataOutputStream dataOutputStream2 = new DataOutputStream(
                        fileOutputStream);
                try {
                    dataOutputStream2.writeUTF(this.location);
                    dataOutputStream2.writeInt(this.currentStartlevel);
                    dataOutputStream2.writeBoolean(this.persistently);
                    dataOutputStream2.flush();
                    ((FileOutputStream) fileOutputStream).getFD().sync();
                    AtlasFileLock.getInstance().unLock(file);
                    if (dataOutputStream2 != null) {
                        try {
                            dataOutputStream2.close();
                            return;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            return;
                        }
                    }
                    return;
                } catch (IOException e3) {
                    e = e3;
                    dataOutputStream = dataOutputStream2;
                    try {
                        log.error(
                                "Could not save meta data "
                                        + file.getAbsolutePath(), e);
                        AtlasFileLock.getInstance().unLock(file);
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                                return;
                            }
                        }
                    } catch (Throwable th) {
                        e = th;
                        AtlasFileLock.getInstance().unLock(file);
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (IOException e4) {
                                e4.printStackTrace();
                            }
                        }

                    }
                } catch (Throwable th2) {
                    e = th2;
                    dataOutputStream = dataOutputStream2;
                    AtlasFileLock.getInstance().unLock(file);
                    if (dataOutputStream != null) {
                        dataOutputStream.close();
                    }

                }
            }
            log.error("Failed to get file lock for " + file.getAbsolutePath());
            AtlasFileLock.getInstance().unLock(file);
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e222) {
                    e222.printStackTrace();
                }
            }
        } catch (IOException e5) {
            e = e5;
            log.error("Could not save meta data " + file.getAbsolutePath(), e);
            AtlasFileLock.getInstance().unLock(file);
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
	public String toString() {
        return this.location;
    }
}
