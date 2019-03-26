package com.ziq.base.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class DeviceInfoUtil {


    public static int getDisplayHeight(Context ctx) {
        return ctx.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getDisplayWidth(Context ctx) {
        return ctx.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getNavigationBarHeight(Context ctx) {
        Resources resources = ctx.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }


    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     * @param ctx s
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            return tm.getDeviceId();
        }
        return null;
    }

    /**
     *  @param context s
     *  @return 字符
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi ;
    }

    /**
     * @param context s
     *  @return 字符
     * 获取手机号
     */
    public static String getPhoneNumber(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        String phoneNumber = mTelephonyMgr.getLine1Number();
        return phoneNumber ;
    }

    public static String[] getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2="";
        String[] cpuInfo={"",""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return cpuInfo;
    }

    public static void getTotalMemory() {
        String str1 = "/proc/meminfo";
        String str2="";
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((str2 = localBufferedReader.readLine()) != null) {
                LogUtil.e( "---" + str2);
            }
        } catch (IOException e) {
        }
    }

}
