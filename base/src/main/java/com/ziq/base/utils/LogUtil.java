package com.ziq.base.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class LogUtil {
    private static final String TAG = "LogUtil";
    public static boolean isDebug = false;

    public static void e(String msg) {
        if (isDebug && !TextUtils.isEmpty(msg)) {
            LogUtil.i(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (isDebug && !TextUtils.isEmpty(msg)) {
            Log.i(TAG, msg);
        }
    }


    public static void e(String tag, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg)) {
            LogUtil.i(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug && !TextUtils.isEmpty(msg)) {
            Log.i(tag, msg);
        }
    }

}
