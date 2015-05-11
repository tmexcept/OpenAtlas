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

import java.util.List;
import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;

final class Package implements ExportedPackage {
    final BundleClassLoader classloader;
    List<Bundle> importingBundles;
    final String pkg;
    boolean removalPending;
    boolean resolved;
    private final short[] version;

    Package(String packageString, BundleClassLoader bundleClassLoader, boolean resolved) {
        this.importingBundles = null;
        this.removalPending = false;
        this.resolved = false;
        String[] parsePackageString = parsePackageString(packageString);
        this.pkg = parsePackageString[0];
        this.version = getVersionNumber(parsePackageString[1]);
        this.classloader = bundleClassLoader;
        this.resolved = resolved;
    }

    @Override
	public Bundle getExportingBundle() {
        return this.classloader.bundle;
    }

    @Override
	public Bundle[] getImportingBundles() {
        if (this.importingBundles == null) {
            return new Bundle[] { this.classloader.bundle };
        }
        Bundle[] bundleArr = new Bundle[(this.importingBundles.size() + 1)];
        this.importingBundles.toArray(bundleArr);
        bundleArr[this.importingBundles.size()] = this.classloader.bundle;
        return bundleArr;
    }

    @Override
	public String getName() {
        return this.pkg;
    }

    @Override
	public String getSpecificationVersion() {
        return this.version == null ? null : this.version[0] + "."
                + this.version[1] + "." + this.version[2];
    }

    @Override
	public boolean isRemovalPending() {
        return this.removalPending;
    }

    
    @Override
	public boolean equals(Object obj) {
        if (!(obj instanceof Package)) {
            return false;
        }
        Package packageR = (Package) obj;
        if (this.classloader == null) {
            return matches(this.pkg, this.version, packageR.pkg,
                    packageR.version);
        }
        return obj.hashCode() == hashCode();
    }

    @Override
	public String toString() {
        if (this.version == null) {
            return this.pkg;
        }
        return this.pkg + "; specification-version="
                + getSpecificationVersion()
                + (this.resolved ? "" : " (UNRESOLVED)");
    }

    @Override
	public int hashCode() {
        return this.pkg.hashCode();
    }

    static String[] parsePackageString(String str) {
        if (str.indexOf(";") > -1) {
            return new String[] { str.substring(0, str.indexOf(";")).trim(),
                    str.substring(str.indexOf(";") + 1).trim() };
        }
        return new String[] { str.trim(), "" };
    }

    boolean matches(String str) {
        String[] parsePackageString = parsePackageString(str);
        return matches(this.pkg, this.version, parsePackageString[0],
                getVersionNumber(parsePackageString[1]));
    }

    private static boolean matches(String str, short[] sArr, String str2,
            short[] sArr2) {
        int indexOf = str2.indexOf(42);
        if (indexOf > -1) {
            if (indexOf == 0) {
                return true;
            }
            String substring = str2.substring(0, indexOf);
            if (!substring.endsWith(".")) {
                return false;
            }
            if (!str.startsWith(substring.substring(0, substring.length() - 1))) {
                return false;
            }
        } else if (!str.equals(str2)) {
            return false;
        }
        if (sArr == null || sArr2 == null) {
            return true;
        }
        for (indexOf = 0; indexOf < 3; indexOf++) {
            if (sArr[indexOf] > sArr2[indexOf]) {
                return false;
            }
        }
        return true;
    }

    boolean updates(Package packageR) {
        if (this.version == null || packageR.version == null) {
            return true;
        }
        for (int i = 0; i < 3; i++) {
            if (this.version[i] < packageR.version[i]) {
                return false;
            }
        }
        return true;
    }

    static boolean matches(String str, String str2) {
        String[] parsePackageString = parsePackageString(str);
        String[] parsePackageString2 = parsePackageString(str2);
        return matches(parsePackageString[0],
                getVersionNumber(parsePackageString[1]),
                parsePackageString2[0],
                getVersionNumber(parsePackageString2[1]));
    }

    private static short[] getVersionNumber(String str) {
        if (!str.startsWith("specification-version=")) {
            return null;
        }
        String trim = str.substring(22).trim();
        if (trim.startsWith("\"")) {
            trim = trim.substring(1);
        }
        if (trim.endsWith("\"")) {
            trim = trim.substring(0, trim.length() - 1);
        }
        StringTokenizer stringTokenizer = new StringTokenizer(trim, ".");
        short[] sArr = new short[] { (short) 0, (short) 0, (short) 0 };
        int i = 0;
        while (stringTokenizer.hasMoreTokens() && i <= 2) {
            sArr[i] = Short.parseShort(stringTokenizer.nextToken());
            i++;
        }
        return sArr;
    }
}
