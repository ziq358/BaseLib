package com.ziq.base.manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.ziq.base.event.BatteryStatusChangeEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jj on 2018/6/2.
 */

public class BaseBluetoothManager {

    public static final int OPEN_BLUE_TOOTH_CODE = 10;
    private BluetoothManager bluetoothManager;

    public BaseBluetoothManager(Context context) {
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
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

    public boolean startScan() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter.startLeScan(new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
                    Log.e("BaseBluetoothManager", "onLeScan --- " + bluetoothDevice.getName());
                }
            });
        }
        return bluetoothAdapter.startDiscovery();
    }

    public boolean startDiscovery() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.startDiscovery();
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
            Bundle b = intent.getExtras();
            Object[] lstName = new Object[0];
            if (b != null) {
                lstName = b.keySet().toArray();
            }
            Log.e("BaseBluetoothManager", "----------" + action);
            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e("BaseBluetoothManager", keyName + ">>>" + String.valueOf(b.get(keyName)));
            }
            BluetoothDevice device;
            // 搜索发现设备时，取得设备的信息；注意，这里有可能重复搜索同一设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("BaseBluetoothManager", "device: " + device.toString());
            }
            //状态改变时
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING://正在配对
                        Log.d("BaseBluetoothManager", "正在配对......");
                        break;
                    case BluetoothDevice.BOND_BONDED://配对结束
                        Log.d("BaseBluetoothManager", "完成配对");
                        break;
                    case BluetoothDevice.BOND_NONE://取消配对/未配对
                        Log.d("BaseBluetoothManager", "取消配对");
                    default:
                        break;
                }
            }
        }
    };
}
