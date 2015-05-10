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
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.openAtlas.boot.Globals;




/**
 * @author BunnyBlue
 *
 */
public class BaselineInfoProvider {
	private static BaselineInfoProvider mInfoProvider;
	private String baselineVersion;
	private String mainVersionName;
	private int mainVersionCode;
	private String bundleList;

	private BaselineInfoProvider() {
		this.baselineVersion ="";
		this.mainVersionName = "";
		this.mainVersionCode = 0;
		this.bundleList = "";
	}

	public static synchronized BaselineInfoProvider getInstance() {
		BaselineInfoProvider mBaselineInfoProvider;
		synchronized (BaselineInfoProvider.class) {
			if (mInfoProvider == null) {
				mInfoProvider = new BaselineInfoProvider();
			}
			mInfoProvider.init();
			mBaselineInfoProvider = mInfoProvider;
		}
		return mBaselineInfoProvider;
	}

	private void init() {
		File filesDir = Globals.getApplication().getFilesDir();
		if (filesDir == null) {
			filesDir = Globals.getApplication().getFilesDir();
		}
		File file = new File(filesDir.getAbsolutePath() + File.separatorChar + "bundleBaseline" + File.separatorChar, "baselineInfo");
		if (file.exists()) {
			try {
				DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
				this.mainVersionName = dataInputStream.readUTF();
				this.mainVersionCode = dataInputStream.readInt();
				this.baselineVersion = dataInputStream.readUTF();
				this.bundleList = dataInputStream.readUTF();
				dataInputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	public String getBaselineVersion() {
		return this.baselineVersion;
	}

	public String getBundleList() {
		return this.bundleList;
	}

	public int getMainVersionCode() {
		return this.mainVersionCode;
	}

	public String getMainVersionName() {
		return this.mainVersionName;
	}
}