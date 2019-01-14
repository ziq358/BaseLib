package com.ziq.base.mvp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.ziq.base.dagger.App;
import com.ziq.base.dagger.component.AppComponent;
import com.ziq.base.dagger.component.DaggerAppComponent;
import com.ziq.base.utils.performance.BlockDetectByChoreographer;
import com.ziq.base.utils.performance.UIBlockMonitor;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class BaseApplication extends Application implements App {

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent
                .builder()
                .application(this)//提供application
                .build();
    }

    @Override
    public AppComponent getAppComponent() {
        return mAppComponent;
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
