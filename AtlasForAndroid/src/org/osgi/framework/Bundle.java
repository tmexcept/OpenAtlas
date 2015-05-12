package org.osgi.framework;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Dictionary;

public interface Bundle {
    public static final int ACTIVE = 32;
    public static final int INSTALLED = 2;
    public static final int RESOLVED = 4;
    public static final int STARTING = 8;
    public static final int STOPPING = 16;
    public static final int UNINSTALLED = 1;

    long getBundleId();

    Dictionary<String, String> getHeaders();

    String getLocation();

    ServiceReference[] getRegisteredServices();
	/**
	 * Find the specified resource from this bundle's class loader.
	 * 
	 * This bundle's class loader is called to search for the specified
	 * resource. If this bundle's state is <code>INSTALLED</code>, this method
	 * must attempt to resolve this bundle before attempting to get the
	 * specified resource. If this bundle cannot be resolved, then only this
	 * bundle must be searched for the specified resource. Imported packages
	 * cannot be searched when this bundle has not been resolved. If this bundle
	 * is a fragment bundle then <code>null</code> is returned.
	 * <p>
	 * Note: Jar and zip files are not required to include directory entries.
	 * URLs to directory entries will not be returned if the bundle contents do
	 * not contain directory entries.
	 * 
	 * @param name The name of the resource. See
	 *        <code>ClassLoader.getResource</code> for a description of the
	 *        format of a resource name.
	 * @return A URL to the named resource, or <code>null</code> if the resource
	 *         could not be found or if this bundle is a fragment bundle or if
	 *         the caller does not have the appropriate
	 *         <code>AdminPermission[this,RESOURCE]</code>, and the Java Runtime
	 *         Environment supports permissions.
	 * @throws IllegalStateException If this bundle has been uninstalled.
	 * @see #getEntry
	 * @see #findEntries
	 * @since 1.1
	 */
    URL getResource(String name);

    ServiceReference[] getServicesInUse();

    int getState();

    boolean hasPermission(Object permission);

    void start() throws BundleException;

    void stop() throws BundleException;

    void uninstall() throws BundleException;

    void update() throws BundleException;

    void update(File bundleFile) throws BundleException;

    void update(InputStream inputStream) throws BundleException;
}
