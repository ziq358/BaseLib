package com.ziq.baselib.Activity;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ziq.base.mvp.dagger.component.AppComponent;
import com.ziq.base.event.BluetoothFoundEvent;
import com.ziq.base.event.BluetoothSearchFinishedEvent;
import com.ziq.base.event.BluetoothSearchStartEvent;
import com.ziq.base.manager.BaseBluetoothManager;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.LogUtil;
import com.ziq.baselib.R;
import com.ziq.baselib.adapter.BluetoothRecycleViewAdapter;
import com.ziq.baselib.receive.BluetoothMusicButtonReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class BluetoothActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BluetoothActivity";
    BluetoothDevice selectDevice;
    @BindView(R.id.rv_result)
    RecyclerView mRvResult;
    BluetoothRecycleViewAdapter mBluetoothRecycleViewAdapter;
    @BindView(R.id.tv_selected_device)
    TextView mTvSelect;
    private BaseBluetoothManager mBaseBluetoothManager;

    public static String parseKeyCode(int keyCode) {
        String ret = "";
        switch (keyCode) {
            case KeyEvent.KEYCODE_POWER:
                // 监控/拦截/屏蔽电源键 这里拦截不了
                ret = "KEYCODE_POWER(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_RIGHT_BRACKET:
                // 监控/拦截/屏蔽返回键
                ret = "KEYCODE_RIGHT_BRACKET";
                break;
            case KeyEvent.KEYCODE_MENU:
                // 监控/拦截菜单键
                ret = "KEYCODE_MENU";
                break;
            case KeyEvent.KEYCODE_HOME:
                // 由于Home键为系统键，此处不能捕获
                ret = "KEYCODE_HOME";
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                // 监控/拦截/屏蔽上方向键
                ret = "KEYCODE_DPAD_UP";
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                // 监控/拦截/屏蔽左方向键
                ret = "KEYCODE_DPAD_LEFT";
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // 监控/拦截/屏蔽右方向键
                ret = "KEYCODE_DPAD_RIGHT";
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // 监控/拦截/屏蔽下方向键
                ret = "KEYCODE_DPAD_DOWN";
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                // 监控/拦截/屏蔽中方向键
                ret = "KEYCODE_DPAD_CENTER";
                break;
            case KeyEvent.FLAG_KEEP_TOUCH_MODE:
                // 监控/拦截/屏蔽长按
                ret = "FLAG_KEEP_TOUCH_MODE";
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                // 监控/拦截/屏蔽下方向键
                ret = "KEYCODE_VOLUME_DOWN(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                // 监控/拦截/屏蔽中方向键
                ret = "KEYCODE_VOLUME_UP(KeyCode:" + keyCode + ")";
                break;
            case 220:
                // case KeyEvent.KEYCODE_BRIGHTNESS_DOWN:
                // 监控/拦截/屏蔽亮度减键
                ret = "KEYCODE_BRIGHTNESS_DOWN(KeyCode:" + keyCode + ")";
                break;
            case 221:
                // case KeyEvent.KEYCODE_BRIGHTNESS_UP:
                // 监控/拦截/屏蔽亮度加键
                ret = "KEYCODE_BRIGHTNESS_UP(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                ret = "KEYCODE_MEDIA_PLAY(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                ret = "KEYCODE_MEDIA_PAUSE(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                ret = "KEYCODE_MEDIA_PREVIOUS(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                ret = "KEYCODE_MEDIA_PLAY_PAUSE(KeyCode:" + keyCode + ")";
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                ret = "KEYCODE_MEDIA_NEXT(KeyCode:" + keyCode + ")";
                break;
            default:
                ret = "keyCode: "
                        + keyCode;
                break;
        }
        return ret;
    }

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_blue_tooth;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        mBaseBluetoothManager = new BaseBluetoothManager(this);
        mBaseBluetoothManager.registerReceiver(this);
        mBaseBluetoothManager.initBluetoothA2DP(this);

        //使得只有一个 能接受
        ((AudioManager) getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(new ComponentName(
                this,
                BluetoothMusicButtonReceiver.class));


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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i(TAG, "onKeyDown: " + parseKeyCode(keyCode));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //使得只有一个 能接受
        ((AudioManager) getSystemService(AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(new ComponentName(
                this,
                BluetoothMusicButtonReceiver.class));
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
                LogUtil.i(TAG, "蓝牙: " + result);
                break;
            case R.id.open_blue_tooth_intent:
                mBaseBluetoothManager.openBluetoothIntent(this);
                break;
            case R.id.search_start:
                if (!mBaseBluetoothManager.startDiscovery(this)) {
                    Uri packageURI = Uri.parse("package:" + this.getPackageName());
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                    startActivity(intent);
                }
                break;
            case R.id.search_stop:
                LogUtil.i(TAG, "停止搜索: " + mBaseBluetoothManager.stopDiscovery());
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
            LogUtil.i(TAG, "打开蓝牙: " + mBaseBluetoothManager.isBluetoothOpen());
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
