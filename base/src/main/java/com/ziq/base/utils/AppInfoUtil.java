package com.ziq.base.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;

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
            packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
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

    /**
     * 获取指定包名所对应的应用的名字
     *
     * @param context 上下文
     * @param pkgName 指定应用的包名
     * @return 指定应用程序的包名
     */
    public static String getApplicationName(Context context, String pkgName) {
        synchronized (context) {
            PackageManager pm = context.getPackageManager();
            String name = "";
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(pkgName, 0);
                if (appInfo != null) {
                    name = (String) pm.getApplicationLabel(appInfo);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            return name;
        }
    }

    /**
     * 获取指定包名所对应的应用的图标
     *
     * @param context 上下文
     * @param pkgName 指定应用的包名
     * @return 应用所对应的包名
     */
    public static BitmapDrawable getApplicationIcon(Context context, String pkgName) {
        synchronized (context) {
            PackageManager pm = context.getPackageManager();
            BitmapDrawable icon = null;
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(pkgName, 0);
                if (appInfo != null) {
                    icon = (BitmapDrawable) appInfo.loadIcon(context.getPackageManager());
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            return icon;
        }
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
