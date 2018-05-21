package com.ziq.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class NetworkUtils {



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
    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

}
