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
package com.openAtlas.framework.bundlestorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.res.AssetManager;
import android.os.Build;
import android.text.TextUtils;

import com.openAtlas.bundleInfo.BundleInfoList;
import com.openAtlas.dexopt.InitExecutor;
import com.openAtlas.framework.Framework;
import com.openAtlas.hack.AtlasHacks;
import com.openAtlas.log.Logger;
import com.openAtlas.log.LoggerFactory;
import com.openAtlas.runtime.RuntimeVariables;
import com.openAtlas.util.ApkUtils;
import com.openAtlas.util.AtlasFileLock;
import com.openAtlas.util.StringUtils;

import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;

public class BundleArchiveRevision {
    static final String BUNDLE_FILE_NAME = "bundle.zip";
    static final String BUNDLE_LEX_FILE = "bundle.lex";
    static final String BUNDLE_ODEX_FILE = "bundle.dex";
    static final String FILE_PROTOCOL = "file:";
    static final String REFERENCE_PROTOCOL = "reference:";
    static final Logger log;
    private final File bundleFile;
    private ClassLoader dexClassLoader;
    private DexFile dexFile;
    private boolean isDexFileUsed;
    private Manifest manifest;
    private final File revisionDir;
    private final String revisionLocation;
    private final long revisionNum;
    private ZipFile zipFile;

    class AnonymousClass_1 extends DexClassLoader {
        AnonymousClass_1(String str, String str2, String str3,
                ClassLoader classLoader) {
            super(str, str2, str3, classLoader);
        }

        @Override
		public String findLibrary(String str) {
            String findLibrary = super.findLibrary(str);
            if (!TextUtils.isEmpty(findLibrary)) {
                return findLibrary;
            }
            File findSoLibrary = BundleArchiveRevision.this
                    .findSoLibrary(System.mapLibraryName(str));
            if (findSoLibrary != null && findSoLibrary.exists()) {
                return findSoLibrary.getAbsolutePath();
            }
            try {
                return (String) AtlasHacks.ClassLoader_findLibrary.invoke(
                        Framework.getSystemClassLoader(), str);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static class DexLoadException extends RuntimeException {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		DexLoadException(String str) {
            super(str);
        }
    }

    static {
        log = LoggerFactory.getInstance("BundleArchiveRevision");
    }

    BundleArchiveRevision(String location, long revisionNum, File revisionDir, InputStream inputStream)
            throws IOException {
    	System.out.println("BundleArchiveRevision.BundleArchiveRevision()");
        Object obj = 1;
        this.revisionNum = revisionNum;
        this.revisionDir = revisionDir;
        if (!this.revisionDir.exists()) {
            this.revisionDir.mkdirs();
        }
        this.revisionLocation = FILE_PROTOCOL;
        this.bundleFile = new File(revisionDir, BUNDLE_FILE_NAME);
        ApkUtils.copyInputStreamToFile(inputStream, this.bundleFile);
        BundleInfoList instance = BundleInfoList.getInstance();
        instance.print();
    

        if (instance == null || !instance.getHasSO(location)) {
            obj = null;
        }
        if (obj != null) {
            installSoLib(this.bundleFile);
        }
        updateMetadata();
    }

    BundleArchiveRevision(String packageName, long revisionNum, File revisionDir, File file2)
            throws IOException {
        boolean  hasSO=false;;
        this.revisionNum = revisionNum;
        this.revisionDir = revisionDir;
        BundleInfoList instance = BundleInfoList.getInstance();
        if (instance == null || !instance.getHasSO(packageName)) {
        	
        } else {
        	hasSO=true;
        }
        if (!this.revisionDir.exists()) {
            this.revisionDir.mkdirs();
        }
        if (file2.canWrite()) {
            if (isSameDriver(revisionDir, file2)) {
                this.revisionLocation = FILE_PROTOCOL;
                this.bundleFile = new File(revisionDir, BUNDLE_FILE_NAME);
                file2.renameTo(this.bundleFile);
            } else {
                this.revisionLocation = FILE_PROTOCOL;
                this.bundleFile = new File(revisionDir, BUNDLE_FILE_NAME);
                ApkUtils.copyInputStreamToFile(new FileInputStream(file2),
                        this.bundleFile);
            }
            if (hasSO) {
                installSoLib(this.bundleFile);
            }
        } else if (Build.HARDWARE.toLowerCase().contains("mt6592")
                && file2.getName().endsWith(".so")) {
            this.revisionLocation = FILE_PROTOCOL;
            this.bundleFile = new File(revisionDir, BUNDLE_FILE_NAME);
            Runtime.getRuntime().exec(
                    String.format("ln -s %s %s",
                            new Object[] { file2.getAbsolutePath(),
                                    this.bundleFile.getAbsolutePath() }));
            if (hasSO) {
                installSoLib(file2);
            }
        } else if (AtlasHacks.LexFile == null
                || AtlasHacks.LexFile.getmClass() == null) {
            this.revisionLocation = REFERENCE_PROTOCOL
                    + file2.getAbsolutePath();
            this.bundleFile = file2;
            //TODO  update project support so
//            if (this.revisionLocation.contains("qrcode")) {
//            	 installSoLib(file2);
//			}
            if (hasSO) {
                installSoLib(file2);
            }
        } else {
            this.revisionLocation = FILE_PROTOCOL;
            this.bundleFile = new File(revisionDir, BUNDLE_FILE_NAME);
            ApkUtils.copyInputStreamToFile(new FileInputStream(file2),
                    this.bundleFile);
            if (hasSO) {
                installSoLib(this.bundleFile);
            }
        }
        updateMetadata();
    }

    BundleArchiveRevision(String str, long j, File file) throws IOException {
        File file2 = new File(file, "meta");
        if (file2.exists()) {
            AtlasFileLock.getInstance().LockExclusive(file2);
            DataInputStream dataInputStream = new DataInputStream(
                    new FileInputStream(file2));
            this.revisionLocation = dataInputStream.readUTF();
            dataInputStream.close();
            AtlasFileLock.getInstance().unLock(file2);
            this.revisionNum = j;
            this.revisionDir = file;
            if (!this.revisionDir.exists()) {
                this.revisionDir.mkdirs();
            }
            if (StringUtils
                    .startWith(this.revisionLocation, REFERENCE_PROTOCOL)) {
                this.bundleFile = new File(StringUtils.substringAfter(
                        this.revisionLocation, REFERENCE_PROTOCOL));
                return;
            } else {
                this.bundleFile = new File(file, BUNDLE_FILE_NAME);
                return;
            }
        }
        throw new IOException("Could not find meta file in "
                + file.getAbsolutePath());
    }

    void updateMetadata() throws IOException {
        Throwable e;
        File file = new File(this.revisionDir, "meta");
        DataOutputStream dataOutputStream = null;
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (AtlasFileLock.getInstance().LockExclusive(file)) {
                DataOutputStream dataOutputStream2 = new DataOutputStream(
                        new FileOutputStream(file));
                try {
                    dataOutputStream2.writeUTF(this.revisionLocation);
                    dataOutputStream2.flush();
                    AtlasFileLock.getInstance().unLock(file);
                    {
                        try {
                            dataOutputStream2.close();
                            return;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            return;
                        }
                    }
                } catch (IOException e3) {
                    e = e3;
                    dataOutputStream = dataOutputStream2;
                    try {
                        throw new IOException("Could not save meta data "
                                + file.getAbsolutePath(), e);
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
            log.error("Failed to get fileLock for " + file.getAbsolutePath());
            AtlasFileLock.getInstance().unLock(file);
            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
        } catch (IOException e5) {
            e = e5;
            throw new IOException("Could not save meta data "
                    + file.getAbsolutePath(), e);
        }
    }

    public long getRevisionNum() {
        return this.revisionNum;
    }

    public File getRevisionDir() {
        return this.revisionDir;
    }

    public File getRevisionFile() {
        return this.bundleFile;
    }

    public File findSoLibrary(String str) {
        File file = new File(String.format("%s%s%s%s", new Object[] {
                this.revisionDir, File.separator, "lib", File.separator }), str);
        return (file.exists() && file.isFile()) ? file : null;
    }

    public boolean isDexOpted() {
        if (AtlasHacks.LexFile == null
                || AtlasHacks.LexFile.getmClass() == null) {
            return new File(this.revisionDir, BUNDLE_ODEX_FILE).exists();
        }
        return new File(this.revisionDir, BUNDLE_LEX_FILE).exists();
    }

    public synchronized void optDexFile() {
        if (!isDexOpted()) {
            if (AtlasHacks.LexFile == null
                    || AtlasHacks.LexFile.getmClass() == null) {
                File file = new File(this.revisionDir, BUNDLE_ODEX_FILE);
                long currentTimeMillis = System.currentTimeMillis();
                try {
                    if (!AtlasFileLock.getInstance().LockExclusive(file)) {
                        log.error("Failed to get file lock for "
                                + this.bundleFile.getAbsolutePath());
                    }
                    if (file.length() <= 0) {
                        InitExecutor.optDexFile(
                                this.bundleFile.getAbsolutePath(),
                                file.getAbsolutePath());
                        loadDex(file);
                        AtlasFileLock.getInstance().unLock(file);
                        // "bundle archieve dexopt bundle " +
                        // this.bundleFile.getAbsolutePath() + " cost time = " +
                        // (System.currentTimeMillis() - currentTimeMillis) +
                        // " ms";
                    }
                } catch (Throwable e) {
                    log.error(
                            "Failed optDexFile '"
                                    + this.bundleFile.getAbsolutePath()
                                    + "' >>> ", e);
                } finally {
                    AtlasFileLock mAtlasFileLock = AtlasFileLock.getInstance();
                    mAtlasFileLock.unLock(file);
                }
            } else {
                DexClassLoader dexClassLoader = new DexClassLoader(
                        this.bundleFile.getAbsolutePath(),
                        this.revisionDir.getAbsolutePath(), null,
                        ClassLoader.getSystemClassLoader());
            }
        }
    }

    private synchronized void loadDex(File file) throws IOException {
        if (this.dexFile == null) {
            this.dexFile = DexFile.loadDex(this.bundleFile.getAbsolutePath(),
                    file.getAbsolutePath(), 0);
        }
    }

    public void installSoLib(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                String name = zipEntry.getName();
                String str = "armeabi";
                if (Build.CPU_ABI.contains("x86")) {
                    str = "x86";
                }
                if (name.indexOf(String.format("%s%s", new Object[] { "lib/",
                        str })) != -1) {
                    str = String
                            .format("%s%s%s%s%s",
                                    new Object[] {
                                            this.revisionDir,
                                            File.separator,
                                            "lib",
                                            File.separator,
                                            name.substring(
                                                    name.lastIndexOf(File.separator) + 1,
                                                    name.length()) });
                    if (zipEntry.isDirectory()) {
                        File file2 = new File(str);
                        if (!file2.exists()) {
                            file2.mkdirs();
                        }
                    } else {
                        File file3 = new File(str.substring(0,
                                str.lastIndexOf("/")));
                        if (!file3.exists()) {
                            file3.mkdirs();
                        }
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                new FileOutputStream(str));
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(
                                zipFile.getInputStream(zipEntry));
                        byte[] bArr = new byte[4096];
                        for (int read = bufferedInputStream.read(bArr); read != -1; read = bufferedInputStream
                                .read(bArr)) {
                            bufferedOutputStream.write(bArr, 0, read);
                        }
                        bufferedOutputStream.close();
                    }
                }
            }
            zipFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public InputStream openAssetInputStream(String str) throws IOException {
        try {
            AssetManager assetManager = AssetManager.class
                    .newInstance();
            if (((Integer) AtlasHacks.AssetManager_addAssetPath.invoke(
                    assetManager, this.bundleFile.getAbsolutePath()))
                    .intValue() != 0) {
                return assetManager.open(str);
            }
        } catch (Throwable e) {
            log.error("Exception while openNonAssetInputStream >>>", e);
        }
        return null;
    }

    public InputStream openNonAssetInputStream(String str) throws IOException {
        try {
            AssetManager assetManager = AssetManager.class
                    .newInstance();
            int intValue = ((Integer) AtlasHacks.AssetManager_addAssetPath
                    .invoke(assetManager, this.bundleFile.getAbsolutePath()))
                    .intValue();
            if (intValue != 0) {
                return assetManager.openNonAssetFd(intValue, str)
                        .createInputStream();
            }
        } catch (Throwable e) {
            log.error("Exception while openNonAssetInputStream >>>", e);
        }
        return null;
    }

    // TODO impl
    public Manifest getManifest() throws IOException {
        InputStream open;
        InputStream inputStream = null;
        Throwable th;
        Exception e;
        InputStream inputStream2 = null;
        if (this.manifest != null) {
            return this.manifest;
        }
        try {
            AssetManager assetManager = AssetManager.class
                    .newInstance();
            if (((Integer) AtlasHacks.AssetManager_addAssetPath.invoke(
                    assetManager, this.bundleFile.getAbsolutePath()))
                    .intValue() != 0) {
                try {
                    open = assetManager.open("OSGI.MF");
                } catch (FileNotFoundException e2) {
                    inputStream = null;
                    try {
                        log.warn("Could not find OSGI.MF in "
                                + this.bundleFile.getAbsolutePath());
                    } catch (Throwable e3) {
                        Throwable th2 = e3;
                        open = inputStream;
                        th = th2;
                        log.error("Exception while parse OSGI.MF >>>", th);
                        return null;
                    }
                    return null;
                } catch (Exception e4) {
              
                    
                    try {
//                    	if (inputStream!=null) {
//							inputStream.close();
//						}
                    	 e4.printStackTrace();
                      
                    } catch (Exception e5) {
                        th = e5;
                        log.error("Exception while parse OSGI.MF >>>", th);
                        return null;
                    }
                    return null;
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
                try {
                    this.manifest = new Manifest(open);
                    Manifest manifest = this.manifest;
                    if (open == null) {
                        return manifest;
                    }
                    open.close();
                    return manifest;
                } catch (FileNotFoundException e6) {
                    inputStream = open;
                    log.warn("Could not find OSGI.MF in "
                            + this.bundleFile.getAbsolutePath());
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    return null;
                } catch (Exception e7) {
                    e = e7;
                    e.printStackTrace();
                    inputStream = open;
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    return null;
                }
            }
            inputStream = null;
            if (inputStream != null) {
                inputStream.close();
            }
            return null;
        } catch (Exception e8) {
            th = e8;
            open = null;
            try {
                log.error("Exception while parse OSGI.MF >>>", th);
                if (open != null) {
                    open.close();
                }
            } catch (Throwable th4) {
                th = th4;
                inputStream2 = open;
                if (inputStream2 != null) {
                    inputStream2.close();
                }
                // throw th;
            }
            return null;
        } catch (Throwable th32) {
            th = th32;
            if (inputStream2 != null) {
                inputStream2.close();
            }
            // throw th;
        }
        return manifest;
    }

    Class<?> findClass(String str, ClassLoader classLoader)
            throws ClassNotFoundException {
        try {
            if (AtlasHacks.LexFile == null
                    || AtlasHacks.LexFile.getmClass() == null) {
                if (!isDexOpted()) {
                    optDexFile();
                }
                if (this.dexFile == null) {
                    loadDex(new File(this.revisionDir, BUNDLE_ODEX_FILE));
                }
                Class<?> loadClass = this.dexFile.loadClass(str, classLoader);
                this.isDexFileUsed = true;
                return loadClass;
            }
            if (this.dexClassLoader == null) {
                File file = new File(RuntimeVariables.androidApplication
                        .getFilesDir().getParentFile(), "lib");
                this.dexClassLoader = new AnonymousClass_1(
                        this.bundleFile.getAbsolutePath(),
                        this.revisionDir.getAbsolutePath(),
                        file.getAbsolutePath(), classLoader);
            }
            return (Class) AtlasHacks.DexClassLoader_findClass.invoke(
                    this.dexClassLoader, str);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e2) {
            return null;
        } catch (Throwable e3) {
            if (!(e3 instanceof ClassNotFoundException)) {
                if (e3 instanceof DexLoadException) {
                    throw ((DexLoadException) e3);
                }
                log.error("Exception while find class in archive revision: "
                        + this.bundleFile.getAbsolutePath(), e3);
            }
            return null;
        }
    }

    List<URL> getResources(String str) throws IOException {
        List<URL> arrayList = new ArrayList();
        ensureZipFile();
        if (!(this.zipFile == null || this.zipFile.getEntry(str) == null)) {
            try {
                arrayList.add(new URL("jar:" + this.bundleFile.toURL() + "!/"
                        + str));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        return arrayList;
    }

    void close() throws Exception {
        if (this.zipFile != null) {
            this.zipFile.close();
        }
        if (this.dexFile != null) {
            this.dexFile.close();
        }
    }

    private boolean isSameDriver(File file, File file2) {
        return StringUtils
                .equals(StringUtils.substringBetween(file.getAbsolutePath(),
                        "/", "/"), StringUtils.substringBetween(
                        file2.getAbsolutePath(), "/", "/"));
    }

    private void ensureZipFile() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.bundleFile, 1);
        }
    }
}
