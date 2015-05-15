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
package com.openatlas.framework.bundlestorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.Manifest;

import com.openatlas.framework.Framework;
import com.openatlas.util.StringUtils;

public class BundleArchive implements Archive {
    public static final String REVISION_DIRECTORY = "version";
    private File bundleDir;
    private final BundleArchiveRevision currentRevision;
    private final SortedMap<Long, BundleArchiveRevision> revisions;

    public BundleArchive(String location, File bundleDir) throws IOException {
        this.revisions = new TreeMap<Long, BundleArchiveRevision>();
        String[] list = bundleDir.list();
        if (list != null) {
            for (String str2 : list) {
                if (str2.startsWith(REVISION_DIRECTORY)) {
                    long parseLong = Long.parseLong(StringUtils.substringAfter(
                            str2, "."));
                    if (parseLong > 0) {
                        this.revisions.put(Long.valueOf(parseLong), null);
                    }
                }
            }
        }
        if (this.revisions.isEmpty()) {
            throw new IOException(
                    "No valid revisions in bundle archive directory: " + bundleDir);
        }
        this.bundleDir = bundleDir;
        long longValue = this.revisions.lastKey().longValue();
        BundleArchiveRevision bundleArchiveRevision = new BundleArchiveRevision(
                location, longValue, new File(bundleDir, "version."
                        + String.valueOf(longValue)));
        this.revisions.put(Long.valueOf(longValue), bundleArchiveRevision);
        this.currentRevision = bundleArchiveRevision;
    }

    public BundleArchive(String location, File bundleDir, InputStream inputStream)
            throws IOException {
        this.revisions = new TreeMap<Long, BundleArchiveRevision>();
        this.bundleDir = bundleDir;
        BundleArchiveRevision bundleArchiveRevision = new BundleArchiveRevision(
                location, 1, new File(bundleDir, "version." + String.valueOf(1)),
                inputStream);
        this.revisions.put(Long.valueOf(1), bundleArchiveRevision);
        this.currentRevision = bundleArchiveRevision;
    }

    public BundleArchive(String location, File bundleDir, File archiveFile) throws IOException {
        this.revisions = new TreeMap<Long, BundleArchiveRevision>();
        this.bundleDir = bundleDir;
        BundleArchiveRevision bundleArchiveRevision = new BundleArchiveRevision(
                location, 1, new File(bundleDir, "version." + String.valueOf(1)), archiveFile);
        this.revisions.put(Long.valueOf(1), bundleArchiveRevision);
        this.currentRevision = bundleArchiveRevision;
    }

    @Override
	public BundleArchiveRevision newRevision(String location, File bundleDir,
            InputStream inputStream) throws IOException {
        long longValue = 1 + this.revisions.lastKey().longValue();
        BundleArchiveRevision bundleArchiveRevision = new BundleArchiveRevision(
                location, longValue, new File(bundleDir, "version."
                        + String.valueOf(longValue)), inputStream);
        this.revisions.put(Long.valueOf(longValue), bundleArchiveRevision);
        return bundleArchiveRevision;
    }

    @Override
	public BundleArchiveRevision newRevision(String packageName, File bundleDir, File archiveFile)
            throws IOException {
        long revision = 1 + this.revisions.lastKey().longValue();
        BundleArchiveRevision bundleArchiveRevision = new BundleArchiveRevision(
                packageName, revision, new File(bundleDir, "version."
                        + String.valueOf(revision)), archiveFile);
        this.revisions.put(Long.valueOf(revision), bundleArchiveRevision);
        return bundleArchiveRevision;
    }

    @Override
	public BundleArchiveRevision getCurrentRevision() {
        return this.currentRevision;
    }

    @Override
	public File getArchiveFile() {
        return this.currentRevision.getRevisionFile();
    }

    public File getBundleDir() {
        return this.bundleDir;
    }

    @Override
	public boolean isDexOpted() {
        return this.currentRevision.isDexOpted();
    }

    @Override
	public void optDexFile() {
        this.currentRevision.optDexFile();
    }

    @Override
	public InputStream openAssetInputStream(String name) throws IOException {
        return this.currentRevision.openAssetInputStream(name);
    }

    @Override
	public InputStream openNonAssetInputStream(String name) throws IOException {
        return this.currentRevision.openNonAssetInputStream(name);
    }

    @Override
	public Manifest getManifest() throws IOException {
        return this.currentRevision.getManifest();
    }

    @Override
	public Class<?> findClass(String clazz, ClassLoader classLoader)
            throws ClassNotFoundException {
        return this.currentRevision.findClass(clazz, classLoader);
    }

    @Override
	public File findLibrary(String name) {
        return this.currentRevision.findSoLibrary(name);
    }

    @Override
	public List<URL> getResources(String name) throws IOException {
        return this.currentRevision.getResources(name);
    }

    @Override
	public void purge() throws Exception {
        if (this.revisions.size() > 1) {
            long revisionNum = this.currentRevision.getRevisionNum();
            for (Long longValue : this.revisions.keySet()) {
                long longValue2 = longValue.longValue();
                if (longValue2 != revisionNum) {
                    File file = new File(this.bundleDir, "version."
                            + String.valueOf(longValue2));
                    if (file.exists()) {
                        Framework.deleteDirectory(file);
                    }
                }
            }
            this.revisions.clear();
            this.revisions.put(Long.valueOf(revisionNum), this.currentRevision);
        }
    }

    @Override
	public void close() {
    }
}
