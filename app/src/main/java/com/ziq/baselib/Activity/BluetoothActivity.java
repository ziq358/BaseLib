package com.ziq.baselib.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ziq.base.manager.BaseBluetoothManager;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.ShellUtil;
import com.ziq.baselib.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class BluetoothActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BluetoothActivity";
    private BaseBluetoothManager mBaseBluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        ButterKnife.bind(this);
        mBaseBluetoothManager = new BaseBluetoothManager(this);
        mBaseBluetoothManager.registerReceiver(this);
    }

    @Override
    protected void onDestroy() {
        mBaseBluetoothManager.unregisterReceiver(this);
        super.onDestroy();
    }

    @OnClick({R.id.open_blue_tooth, R.id.open_blue_tooth_intent, R.id.search_start,
            R.id.search_stop, R.id.scan_start})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_blue_tooth:
                boolean result = mBaseBluetoothManager.openBluetooth();
                Log.e(TAG, "蓝牙: " + result);
                break;
            case R.id.open_blue_tooth_intent:
                mBaseBluetoothManager.openBluetoothIntent(this);
                break;
            case R.id.scan_start:
                mBaseBluetoothManager.startScan();
                break;
            case R.id.search_start:
                Log.e(TAG, "搜索: " + mBaseBluetoothManager.startDiscovery());
                break;
            case R.id.search_stop:
                Log.e(TAG, "停止搜索: " + mBaseBluetoothManager.stopDiscovery());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BaseBluetoothManager.OPEN_BLUE_TOOTH_CODE){
            Log.e(TAG, "打开蓝牙: " + mBaseBluetoothManager.isBluetoothOpen());
        }
    }
}
