package com.ziq.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class NetworkUtil {
    /**
     * 判断网络是否处于已连接状态
     *
     * @param context 上下文
     * @return 当前的连接状态
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo ni = getNetworkInfo(context);
        return ni != null && ni.isConnected();
    }

    /**
     * 判断 WIFI 是否处于已连接状态
     *
     * @param context 上下文
     * @return 当前的连接状态
     */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo ni = getNetworkInfo(context);
        return ni != null && ni.isConnected() && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }


    /**
     * 获取当前网络的信息
     *
     * @param context 上下文
     * @return 当前网络的信息
     */
    @SuppressLint("MissingPermission")
    @Nullable
    public static NetworkInfo getNetworkInfo(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "需要获取网络状态权限", Toast.LENGTH_SHORT).show();
        } else {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                return cm.getActiveNetworkInfo();
            }
        }
        return null;
    }

}
