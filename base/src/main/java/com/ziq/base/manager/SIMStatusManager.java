package com.ziq.base.manager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

import com.ziq.base.event.SIMStatusChangeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * @author john
 * @since 2017/12/22
 * Des:
 */

public class SIMStatusManager {
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private static SIMStatusManager mInstance;

    private boolean mIsViable;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
                mIsViable = hasSimCard(context);
                SIMStatusChangeEvent simStatusChangeEvent = new SIMStatusChangeEvent();
                simStatusChangeEvent.isViable = mIsViable;
                EventBus.getDefault().post(simStatusChangeEvent);
            }
        }
    };

    private SIMStatusManager(Context context) {
        mIsViable = hasSimCard(context);
    }

    public static SIMStatusManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SIMStatusManager(context);
        }
        return mInstance;
    }

    private boolean hasSimCard(Context context) {
        boolean result = true;
        TelephonyManager tm = (TelephonyManager) context.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        if (tm != null) {
            int state = tm.getSimState();
            switch (state) {
                case TelephonyManager.SIM_STATE_READY:
                    result = true;
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                case TelephonyManager.SIM_STATE_ABSENT:
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                default:
                    result = false;
                    break;
            }
        }
        return result;
    }

    public boolean isViable() {
        return mIsViable;
    }

    public void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SIM_STATE_CHANGED);
        context.registerReceiver(mReceiver, intentFilter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }
}
