package com.ziq.base.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class DeviceInfoUtil {

    private Context mContext;
    private DisplayMetrics mDisplayMetrics;

    public DeviceInfoUtil(Context context) {
        this.mContext = context;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
    }

    public int getDisplayHeight() {
        return mDisplayMetrics.heightPixels;
    }

    public int getDisplayWidth() {
        return mDisplayMetrics.widthPixels;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getNavigationBarHeight() {
        Resources resources = mContext.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

}
