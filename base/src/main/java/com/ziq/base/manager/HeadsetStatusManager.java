package com.ziq.base.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.ziq.base.event.HeadsetStatusChangeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author john
 * @since 2017/12/22
 * Des:
 */

public class HeadsetStatusManager {

    private static HeadsetStatusManager mInstance;

    private boolean mIsViable;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_HEADSET_PLUG.equals(action)) {
                if (intent.hasExtra("state")) {
                    if (intent.getIntExtra("state", 0) == 0) {
                        mIsViable = false;
                    } else if (intent.getIntExtra("state", 0) == 1) {
                        mIsViable = true;
                    }
                }
                HeadsetStatusChangeEvent headsetStatusChangeEvent = new HeadsetStatusChangeEvent();
                headsetStatusChangeEvent.isViable = mIsViable;
                EventBus.getDefault().post(headsetStatusChangeEvent);
            }
        }
    };

    private HeadsetStatusManager() {
    }

    public static HeadsetStatusManager getInstance() {
        if (mInstance == null) {
            mInstance = new HeadsetStatusManager();
        }
        return mInstance;
    }

    public boolean isViable() {
        return mIsViable;
    }

    public void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        context.registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }
}
