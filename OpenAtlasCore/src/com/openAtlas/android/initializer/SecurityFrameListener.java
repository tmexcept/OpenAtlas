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
/**
 * @author BunnyBlue
 */
package com.openAtlas.android.initializer;

import java.io.File;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;

import android.annotation.SuppressLint;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.openAtlas.framework.Atlas;
import com.openAtlas.runtime.RuntimeVariables;
import com.openAtlas.util.ApkUtils;
import com.openAtlas.util.PackageValidate;
import com.openAtlas.util.StringUtils;


public class SecurityFrameListener implements FrameworkListener {//PUT Your Public Key here
	public static final String PUBLIC_KEY = "30819f300d06092a864886f70d010101050003818d003081890281810092392b1b4c16e6a29a7b37a3c8fb9829be812b07019d692f8160c40f4aed8cce72387052a372df984cf0cd7b8aee1c52984864caf68c7a3b85006621fb085e44e7573f952698cf9e052af1ae4627e7986b475f7ed5d277446323e716fe664a3f86546656520be2900199382f64df70ac8b10c5e45585cf51a66e470e670e5ddf0203010001";
	//  public static final String PUBLIC_K2Y = "30819f300d06092a864886f70d010101050003818d003081890281810092392b1b4c16e6a29a7b37a3c8fb9829be812b07019d692f8160c40f4aed8cce72387052a372df984cf0cd7b8aee1c52984864caf68c7a3b85006621fb085e44e7573f952698cf9e052af1ae4627e7986b475f7ed5d277446323e716fe664a3f86546656520be2900199382f64df70ac8b10c5e45585cf51a66e470e670e5ddf0203010001";
	ProcessHandler mHandler;


	private class SecurityFrameAsyncTask extends AsyncTask<String, Void, Boolean> {
		final  SecurityFrameListener mSecurityFrameListener;

		private SecurityFrameAsyncTask(SecurityFrameListener mSecurityFrameListener) {
			this.mSecurityFrameListener = mSecurityFrameListener;
		}

		@Override
		protected  Boolean doInBackground(String ... args) {
			return process(args);
		}

		@Override
		protected  void onPostExecute(Boolean obj) {
			postResult(obj);
		}
		/******验证签名和公钥是否有效*******/
		protected Boolean process(String... args) {
			if (SecurityFrameListener.PUBLIC_KEY == null || SecurityFrameListener.PUBLIC_KEY.isEmpty()) {
				return Boolean.valueOf(true);
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
			List<Bundle> bundles = Atlas.getInstance().getBundles();
			if (bundles != null) {
				for (Bundle bundle : bundles) {
					File bundleFile = Atlas.getInstance().getBundleFile(bundle.getLocation());
					if (!this.mSecurityFrameListener.validBundleCert(bundleFile.getAbsolutePath())) {
						return Boolean.valueOf(false);
					}
					String[] apkPublicKey = ApkUtils.getApkPublicKey(bundleFile.getAbsolutePath());
					if (StringUtils.contains(apkPublicKey, SecurityFrameListener.PUBLIC_KEY)) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e2) {
						}
					} else {
						Log.e("SecurityFrameListener", "Security check failed. " + bundle.getLocation());
						if (apkPublicKey == null || apkPublicKey.length == 0) {
							this.mSecurityFrameListener.storeBadSIG(bundle.getLocation() + ": NULL");
						} else {
							this.mSecurityFrameListener.storeBadSIG(bundle.getLocation() + ": " + apkPublicKey[0]);
						}
						return Boolean.valueOf(false);
					}
				}
			}
			return Boolean.valueOf(true);
		}

		protected void postResult(Boolean bool) {
			if (bool != null && !bool.booleanValue()) {
				Toast.makeText(RuntimeVariables.androidApplication, "Public Key error，PLZ update your  public key", 1).show();
				this.mSecurityFrameListener.mHandler.sendEmptyMessageDelayed(0, 5000);
			}
		}
	}


	public static class ProcessHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			Process.killProcess(Process.myPid());
		}
	}

	public SecurityFrameListener() {
		this.mHandler = new ProcessHandler();
	}

	@Override
	@SuppressLint({"NewApi"})
	public void frameworkEvent(FrameworkEvent frameworkEvent) {
		switch (frameworkEvent.getType()) {
		case FrameworkEvent.STARTED:
			if (VERSION.SDK_INT >= 11) {
				new SecurityFrameAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[0]);
			} else {
				new SecurityFrameAsyncTask(this).execute(new String[0]);
			}
		default:
		}
	}
	/*****程序公钥不匹配******/
	private void storeBadSIG(String errPublicKey) {
		Editor edit = RuntimeVariables.androidApplication.getSharedPreferences("atlas_configs", 0).edit();
		edit.putString("BadSignature", errPublicKey);
		edit.commit();
	}
	/*****验证apk的签名是否有效****/
	private boolean validBundleCert(String archiveSourcePath) {
		PackageValidate packageValidate=new PackageValidate(archiveSourcePath);
		return packageValidate.collectCertificates();

		// return true;
	}
}