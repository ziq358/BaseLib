package com.ziq.base.manager;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.ziq.base.event.NetworkChangeEvent;
import com.ziq.base.utils.NetworkUtil;
import com.ziq.base.utils.SignalStrengthUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @author john
 * @since 2017/12/22
 * Des:
 */

public class NetworkStatusManager {

    private static NetworkStatusManager mInstance;
    private Context mContext;
    private WifiManager mWifiManager;
    private TelephonyManager mTelephonyManager;
    private int mLevel = 3;
    private String mNetworkClass = "";
    private String mSignalStrength = "";
    private boolean mIsNetworkConnect = false;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mIsNetworkConnect = NetworkUtil.isNetworkConnected(context);
            if (NetworkUtil.isWifiConnected(context)) {
                WifiInfo wifiInfo = NetworkStatusManager.getInstance(context).getWifiInfo();
                mSignalStrength = wifiInfo.getRssi() + "db";
            }
            NetworkChangeEvent networkChangeEvent = new NetworkChangeEvent();
            networkChangeEvent.isNetworkConnect = mIsNetworkConnect;
            networkChangeEvent.level = mLevel;
            networkChangeEvent.networkClass = mNetworkClass;
            EventBus.getDefault().post(networkChangeEvent);
        }
    };
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            mIsNetworkConnect = NetworkUtil.isNetworkConnected(mContext);
            String[] arraySignalStrength = new String[1];
            mLevel = SignalStrengthUtil.getLevel(signalStrength, arraySignalStrength);
            mSignalStrength = arraySignalStrength[0] + "db";
            if (NetworkUtil.isWifiConnected(mContext)) {
                mNetworkClass = "wifi";
                WifiInfo wifiInfo = getWifiInfo();
                mSignalStrength = wifiInfo.getRssi() + "db";
            } else {
                mNetworkClass = SignalStrengthUtil.getNetworkClass(mTelephonyManager.getNetworkType());
            }
            NetworkChangeEvent networkChangeEvent = new NetworkChangeEvent();
            networkChangeEvent.isNetworkConnect = mIsNetworkConnect;
            networkChangeEvent.level = mLevel;
            networkChangeEvent.networkClass = mNetworkClass;
            EventBus.getDefault().post(networkChangeEvent);
        }
    };

    private NetworkStatusManager(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mTelephonyManager = (TelephonyManager) mContext.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
        if (mTelephonyManager != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    public static NetworkStatusManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkStatusManager(context);
        }
        return mInstance;
    }

    public String getSignalStrength() {
        return mSignalStrength;
    }

    public int getLevel() {
        return mLevel;
    }

    public String getNetworkClass() {
        return mNetworkClass;
    }

    @SuppressLint("MissingPermission")
    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, intentFilter);

    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }

}
