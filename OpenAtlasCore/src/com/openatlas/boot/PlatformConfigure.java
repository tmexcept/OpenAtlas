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
package com.openatlas.boot;


/**
 * App  Platform configuration
 * @author BunnyBlue
 *
 */
public class PlatformConfigure {
	/****闪屏activity****/
	public static final String BOOT_ACTIVITY="com.openAtlas.welcome.Welcome";
	public static final String BOOT_ACTIVITY_DEFAULT="com.openAtlas.launcher.welcome";
	public static final String ACTION_BROADCAST_BUNDLES_INSTALLED="com.openAtlas.action.BUNDLES_INSTALLED";
	public static final String ATLAS_APP_DIRECTORY="com.openAtlas.AppDirectory";
	public static final String INSTALL_LOACTION="com.openAtlas.storage";
	public static final String COM_OPENATLAS_DEBUG_BUNDLES="com.openAtlas.debug.bundles";
	public static final String  OPENATLAS_PUBLIC_KEY="om.openAtlas.publickey";
	public static final String  OPENATLAS_BASEDIR="com.openAtlas.basedir";
	public static final String  OPENATLAS_BUNDLE_LOCATION="com.openAtlas.jars";
	public static final String  OPENATLAS_CLASSLOADER_BUFFER_SIZE="com.openAtlas.classloader.buffersize";
	public static final String  OPENATLAS_LOG_LEVEL="com.openAtlas.log.level";
	public static final String  OPENATLAS_DEBUG_BUNDLES="com.openAtlas.debug.bundles";
	public static final String  OPENATLAS_DEBUG_PACKAGES="com.openAtlas.debug.packages";
	public static final String  OPENATLAS_DEBUG_SERVICES="com.openAtlas.debug.services";
	public static final String  OPENATLAS_DEBUG_CLASSLOADING="com.openAtlas.debug.classloading";
	public static final String  OPENATLAS_DEBUG="com.openAtlas.debug";
	public static final String OPENATLAS_FRAMEWORK_PACKAGE="com.openAtlas.framework";

	public static final String OPENATLAS_STRICT_STARTUP="com.openAtlas.strictStartup";
	public static final String OPENATLAS_AUTO_LOAD="com.openAtlas.auto.load";
	public static Class<?> BundleNotFoundActivity=null;
	/***延时启动配*****/
	public static final String[] DELAY;
	/****随宿主程序启动的Bundle，核心Bundle需要在这里添加，其余的不建议在这儿加，不然影响初始化速度***/
	public static final String[] AUTO;
	public static final String[] STORE;

	static {
		DELAY = new String[]{"com.openatlas.qrcode"};
		AUTO = new String[]{"com.openatlas.homelauncher","com.openatlas.qrcode","com.taobao.android.game20x7a","com.taobao.universalimageloader.sample0x6a"};
		STORE = new String[]{"com.taobao.android.game20x7a","com.taobao.android.gamecenter","com.taobao.universalimageloader.sample0x6a"};
	}
	
	

    public static final String BUNDLE_ACTIVATOR = "Bundle-Activator";
    public static final String BUNDLE_CATEGORY = "Bundle-Category";
    public static final String BUNDLE_CLASSPATH = "Bundle-ClassPath";
    public static final String BUNDLE_CONTACTADDRESS = "Bundle-ContactAddress";
    public static final String BUNDLE_COPYRIGHT = "Bundle-Copyright";
    public static final String BUNDLE_DESCRIPTION = "Bundle-Description";
    public static final String BUNDLE_DOCURL = "Bundle-DocURL";
    public static final String BUNDLE_NAME = "Bundle-Name";
    public static final String BUNDLE_NATIVECODE = "Bundle-NativeCode";
    public static final String BUNDLE_NATIVECODE_LANGUAGE = "language";
    public static final String BUNDLE_NATIVECODE_OSNAME = "osname";
    public static final String BUNDLE_NATIVECODE_OSVERSION = "osversion";
    public static final String BUNDLE_NATIVECODE_PROCESSOR = "processor";
    public static final String BUNDLE_REQUIREDEXECUTIONENVIRONMENT = "Bundle-RequiredExecutionEnvironment";
    public static final String BUNDLE_UPDATELOCATION = "Bundle-UpdateLocation";
    public static final String BUNDLE_VENDOR = "Bundle-Vendor";
    public static final String BUNDLE_VERSION = "Bundle-Version";
    public static final String BUNDLE_VERSION_ATTRIBUTE = "bundle-version";
    public static final String DYNAMICIMPORT_PACKAGE = "DynamicImport-Package";
    public static final String EXPORT_PACKAGE = "Export-Package";
    public static final String EXPORT_SERVICE = "Export-Service";
    public static final String FRAMEWORK_EXECUTIONENVIRONMENT = "org.osgi.framework.executionenvironment";
    public static final String FRAMEWORK_LANGUAGE = "org.osgi.framework.language";
    public static final String FRAMEWORK_OS_NAME = "org.osgi.framework.os.name";
    public static final String FRAMEWORK_OS_VERSION = "org.osgi.framework.os.version";
    public static final String FRAMEWORK_PROCESSOR = "org.osgi.framework.processor";
    public static final String FRAMEWORK_VENDOR = "org.osgi.framework.vendor";
    public static final String FRAMEWORK_VERSION = "org.osgi.framework.version";
    public static final String IMPORT_PACKAGE = "Import-Package";
    public static final String IMPORT_SERVICE = "Import-Service";
    public static final String OBJECTCLASS = "objectClass";
    public static final String PACKAGE_SPECIFICATION_VERSION = "specification-version";
    public static final String REQUIRE_BUNDLE = "Require-Bundle";
    public static final String SERVICE_DESCRIPTION = "service.description";
    public static final String SERVICE_ID = "service.id";
    public static final String SERVICE_PID = "service.pid";
    public static final String SERVICE_RANKING = "service.ranking";
    public static final String SERVICE_VENDOR = "service.vendor";
    public static final String SYSTEM_BUNDLE_LOCATION = "System Bundle";
}
