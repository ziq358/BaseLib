package com.ziq.baselib.Activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ziq.base.event.BluetoothFoundEvent;
import com.ziq.base.event.BluetoothSearchFinishedEvent;
import com.ziq.base.event.BluetoothSearchStartEvent;
import com.ziq.base.manager.BaseBluetoothManager;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.baselib.R;
import com.ziq.baselib.adapter.BluetoothRecycleViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    BluetoothDevice selectDevice;
    @Bind(R.id.rv_result)
    RecyclerView mRvResult;
    BluetoothRecycleViewAdapter mBluetoothRecycleViewAdapter;

    @Bind(R.id.tv_selected_device)
    TextView mTvSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        mBaseBluetoothManager = new BaseBluetoothManager(this);
        mBaseBluetoothManager.registerReceiver(this);
        mBaseBluetoothManager.initBluetoothA2DP(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvResult.setLayoutManager(linearLayoutManager);
        mBluetoothRecycleViewAdapter = new BluetoothRecycleViewAdapter(this);
        mRvResult.setAdapter(mBluetoothRecycleViewAdapter);
        mBluetoothRecycleViewAdapter.setItemClickListerer(new BluetoothRecycleViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(BluetoothDevice item) {
                selectDevice = item;
                mTvSelect.setText("" + item.getName() + " " + item.getAddress());
            }
        });
    }

    @Override
    protected void onDestroy() {
        mBaseBluetoothManager.unregisterReceiver(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.open_blue_tooth, R.id.open_blue_tooth_intent, R.id.search_start,
            R.id.search_stop, R.id.connect_a2dp, R.id.disconnect_a2dp})
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
            case R.id.search_start:
                Log.e(TAG, "搜索: " + mBaseBluetoothManager.startDiscovery(this));
                break;
            case R.id.search_stop:
                Log.e(TAG, "停止搜索: " + mBaseBluetoothManager.stopDiscovery());
                break;
            case R.id.connect_a2dp:
                mBaseBluetoothManager.stopDiscovery();
                mBaseBluetoothManager.connectA2dp(selectDevice);
                break;
            case R.id.disconnect_a2dp:
                mBaseBluetoothManager.disconnectA2dp(selectDevice);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BaseBluetoothManager.OPEN_BLUE_TOOTH_CODE) {
            Log.e(TAG, "打开蓝牙: " + mBaseBluetoothManager.isBluetoothOpen());
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BluetoothSearchStartEvent event) {
        mBluetoothRecycleViewAdapter.setData(mBaseBluetoothManager.getBluetoothDeviceList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BluetoothFoundEvent event) {
        mBluetoothRecycleViewAdapter.setData(mBaseBluetoothManager.getBluetoothDeviceList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BluetoothSearchFinishedEvent event) {
        mBluetoothRecycleViewAdapter.setData(mBaseBluetoothManager.getBluetoothDeviceList());
    }


}
