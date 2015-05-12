///**
// *  OpenAtlasForAndroid Project
//The MIT License (MIT) Copyright (OpenAtlasForAndroid) 2015 Bunny Blue,achellies
//
//Permission is hereby granted, free of charge, to any person obtaining a copy of this software
//and associated documentation files (the "Software"), to deal in the Software 
//without restriction, including without limitation the rights to use, copy, modify, 
//merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
//permit persons to whom the Software is furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in all copies 
//or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
//INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
//PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
//FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
//ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//@author BunnyBlue
// * **/
//package com.openAtlas.launcher;
//
//import java.io.File;
//import java.util.List;
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//
//import com.openAtlas.launcher.android.lifecycle.PanguActivity;
//
//
//public class BaseActivity extends PanguActivity {
//    @SuppressWarnings("unused")
//    private final String TAG;
//    @SuppressWarnings("unused")
//	private boolean disablefinishAnimation;
//    @SuppressWarnings("unused")
//	private boolean isNaviActivity;
//    private View mPanelTopView;
//    @SuppressWarnings("unused")
//    private long resumeUptime;
//    @SuppressWarnings("unused")
//    private List<Integer> sMenuItemId;
//
//    public BaseActivity() {
//        this.TAG = "BaseActivity";
//        this.mPanelTopView = null;
//        this.disablefinishAnimation = false;
//        this.isNaviActivity = false;
//    }
//
//    @Override
//	@TargetApi(14)
//    protected void onCreate(Bundle bundle) {
//    }
//
//    protected String getUTClassName() {
//        return getClass().getName();
//    }
//
//    public void disableFinishAnimationOnce() {
//        this.disablefinishAnimation = true;
//    }
//
//    @Override
//	protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//    }
//
//    public final Activity getActivity() {
//        return this;
//    }
//
//    @Override
//	public SharedPreferences getSharedPreferences(String str, int i) {
//        return getApplicationContext().getSharedPreferences(str, i);
//    }
//
//    @Override
//	public File getFilesDir() {
//        return getApplicationContext().getFilesDir();
//    }
//
//    @Override
//	public File getCacheDir() {
//        return getApplicationContext().getCacheDir();
//    }
//
//    @Override
//	public File getDir(String str, int i) {
//        return getApplicationContext().getDir(str, i);
//    }
//
//    @Override
//	public File getDatabasePath(String str) {
//        return getApplicationContext().getDatabasePath(str);
//    }
//
//    public View getTopView() {
//        return this.mPanelTopView;
//    }
//
//    protected boolean onPanelKeyDown(int i, KeyEvent keyEvent) {
//        return false;
//    }
//
//}
