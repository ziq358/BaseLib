package com.ziq.base.baserx;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.ziq.base.baserx.dagger.App;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.baserx.dagger.component.DaggerAppComponent;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class BaseApplication extends Application implements App {

    private AppDelegate mAppDelegate;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (mAppDelegate == null){
            this.mAppDelegate = new AppDelegate(base);
            this.mAppDelegate.attachBaseContext(base);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (mAppDelegate != null){
            this.mAppDelegate.onCreate(this);
        }
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mAppDelegate != null){
            this.mAppDelegate.onTerminate(this);
        }
    }

    @Override
    public AppComponent getAppComponent() {
        return this.mAppDelegate.getAppComponent();
    }

    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }
}
