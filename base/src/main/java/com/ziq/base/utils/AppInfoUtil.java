package com.ziq.base.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class AppInfoUtil {

    private static final String TAG = "AppInfoUtil";
    private Context mContext;

    public AppInfoUtil(Context context) {
        this.mContext = context;
    }

    public static boolean checkInstall(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "checkInstall: " + e.getMessage());
        }
        return packageInfo != null;
    }

    public static boolean isAppOnBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getAppVersionName() {
        String appVersion = "";
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            appVersion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "getAppVersion: " + e.getMessage());
        }
        return appVersion;
    }

    public int getAppVersionCode() {
        int appVersionCode = 0;
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            appVersionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "getAppVersionCode: " + e.getMessage());
        }
        return appVersionCode;
    }


}
