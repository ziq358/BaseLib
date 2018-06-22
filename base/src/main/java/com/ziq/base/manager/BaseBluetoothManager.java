package com.ziq.base.manager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.ziq.base.event.BluetoothFoundEvent;
import com.ziq.base.event.BluetoothSearchFinishedEvent;
import com.ziq.base.event.BluetoothSearchStartEvent;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jj on 2018/6/2.
 */

public class BaseBluetoothManager {
    private static final String TAG = "BaseBluetoothManager";
    public static final int OPEN_BLUE_TOOTH_CODE = 10;
    private BluetoothManager bluetoothManager;
    private List<BluetoothDevice> bluetoothDeviceList = new ArrayList<>();

    public BaseBluetoothManager(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public List<BluetoothDevice> getBluetoothDeviceList() {
        return bluetoothDeviceList;
    }


    public boolean isBluetoothOpen() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.isEnabled();
    }

    //需要开启、关闭 蓝牙权限
    public boolean openBluetooth() {
        boolean result = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            result = bluetoothAdapter.enable();
        } else {
            result = true;
        }
        return result;
    }

    //需要开启、关闭 蓝牙权限
    public void openBluetoothIntent(Activity activity) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, OPEN_BLUE_TOOTH_CODE);
    }

    public boolean startDiscovery(Context context) {
        boolean result = false;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "需要获取定位权限", Toast.LENGTH_SHORT).show();
        } else {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            result = bluetoothAdapter.startDiscovery();
        }
        return result;
    }

    public boolean stopDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.cancelDiscovery();
    }


    public void registerReceiver(Context context) {
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//搜索开始
        intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索结束
        context.registerReceiver(mBluetoothReceiver, intent);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mBluetoothReceiver);
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "蓝牙Action : " + action);
            BluetoothDevice device;
            if (action != null) {
                switch (action) {
                    case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                        Log.d(TAG, "开始搜索......");
                        bluetoothDeviceList.clear();
                        EventBus.getDefault().post(new BluetoothSearchStartEvent());
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        Log.d(TAG, "搜索结束......");
                        EventBus.getDefault().post(new BluetoothSearchFinishedEvent());
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        boolean exit = false;
                        for (BluetoothDevice temp : bluetoothDeviceList) {
                            if (temp.getAddress().equalsIgnoreCase(device.getAddress())) {
                                exit = true;
                                break;
                            }
                        }
                        if (!exit) {
                            bluetoothDeviceList.add(device);
                            EventBus.getDefault().post(new BluetoothFoundEvent());
                            Log.d(TAG, "device: " + device.getName() + " " + device.getAddress());
                        }
                        break;
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                        switch (blueState) {
                            case BluetoothAdapter.STATE_TURNING_ON:
                                Log.d(TAG, "正在打开蓝牙......");
                                break;
                            case BluetoothAdapter.STATE_ON:
                                Log.d(TAG, "蓝牙打开......");
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                Log.d(TAG, "正在关闭蓝牙......");
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                Log.d(TAG, "蓝牙关闭......");
                                break;
                        }
                        break;
                    case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        switch (device.getBondState()) {
                            case BluetoothDevice.BOND_BONDING://正在配对
                                Log.d(TAG, "正在配对......");
                                break;
                            case BluetoothDevice.BOND_BONDED://配对结束
                                Log.d(TAG, "完成配对");
                                break;
                            case BluetoothDevice.BOND_NONE://取消配对/未配对
                                Log.d(TAG, "取消配对");
                            default:
                                break;
                        }
                        break;
                }
            }

        }
    };


    // a2dp
    private BluetoothA2dp mBluetoothA2dp;

    public void initBluetoothA2DP(final Context context) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }

        mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    //Service连接成功，获得BluetoothA2DP
                    mBluetoothA2dp = (BluetoothA2dp) proxy;
                    Log.d(TAG, "mBluetoothA2dp 连接成功:");
                    Toast.makeText(context, "mBluetoothA2dp 连接成功:", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {

            }
        }, BluetoothProfile.A2DP);
    }

    public void connectA2dp(BluetoothDevice mBluetoothDevice) {
        //连接前先停止搜索
        if (mBluetoothA2dp == null) {
            return;
        }
        if (mBluetoothDevice == null) {
            return;
        }
        Log.d(TAG, "A2dp 开始连接 :");
        setPriority(mBluetoothDevice, 100);
        try {
            Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
            connect.setAccessible(true);
            connect.invoke(mBluetoothA2dp, mBluetoothDevice);
        } catch (Exception e) {
            Log.d(TAG, "A2dp 连接出错:" + e);
        }
        Log.d(TAG, "A2dp 连接结束 :");
    }

    public void disconnectA2dp(BluetoothDevice device) {
        Log.d(TAG, "A2dp 断开连接 :");
        setPriority(device, 0);
        try {
            //通过反射获取BluetoothA2dp中connect方法（hide的），断开连接。
            Method connectMethod = BluetoothA2dp.class.getMethod("disconnect", BluetoothDevice.class);
            connectMethod.invoke(mBluetoothA2dp, device);
        } catch (Exception e) {
            Log.d(TAG, "A2dp 断开连接出错:" + e);
        }
        Log.d(TAG, "A2dp 断开连接结束 :");
    }


    public void setPriority(BluetoothDevice device, int priority) {
        if (mBluetoothA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod = BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class, int.class);
            connectMethod.invoke(mBluetoothA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
