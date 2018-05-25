package com.ziq.base.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.ziq.base.event.BatteryStatusChangeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author john
 * @since 2017/12/22
 * Des:
 */

public class BatteryStatusManager {

    private static BatteryStatusManager mInstance;

    private int mLevelPercent;
    private boolean mIsCharging;
    private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int level = arg1.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = arg1.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            mLevelPercent = (int) (((float) level / scale) * 100);
            int status = arg1.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            mIsCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
            BatteryStatusChangeEvent batteryStatusChangeEvent = new BatteryStatusChangeEvent();
            batteryStatusChangeEvent.levelPercent = mLevelPercent;
            batteryStatusChangeEvent.isCharging = mIsCharging;
            EventBus.getDefault().post(batteryStatusChangeEvent);
        }
    };

    private BatteryStatusManager() {
    }

    public static BatteryStatusManager getInstance() {
        if (mInstance == null) {
            mInstance = new BatteryStatusManager();
        }
        return mInstance;
    }

    public int getLevelPercent() {
        return mLevelPercent;
    }

    public boolean isCharging() {
        return mIsCharging;
    }

    public void registerReceiver(Context context) {
        context.registerReceiver(this.mBatteryReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mBatteryReceiver);
    }
}
