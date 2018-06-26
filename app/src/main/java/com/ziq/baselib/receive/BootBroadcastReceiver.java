package com.ziq.baselib.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ziq.base.manager.BaseBluetoothManager;

public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ziq";
    private static final String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_BOOT)) { //开机启动完成后，要做的事情
            Log.i(TAG, "开机 广播");
        }
    }
}
