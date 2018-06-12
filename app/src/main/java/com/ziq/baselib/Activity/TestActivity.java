package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.DeviceInfoUtil;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.IntentUtil;
import com.ziq.baselib.R;
import com.ziq.baselib.widget.TranslateLoadingView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class TestActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tlv)
    TranslateLoadingView mTranslateLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        Log.e("ziq", "系统版本: "+DeviceInfoUtil.getSystemVersion());
        Log.e("ziq", "手机型号: "+DeviceInfoUtil.getSystemModel());
        Log.e("ziq", "手机号: "+DeviceInfoUtil.getPhoneNumber(this));
        Log.e("ziq", "IMEI: "+DeviceInfoUtil.getIMEI(this));
        Log.e("ziq", "IMSI: "+DeviceInfoUtil.getIMSI(this));
        Log.e("ziq", "CPU: "+DeviceInfoUtil.getCpuInfo());
//        Log.e("ziq", "rom: "+ DeviceInfoUtil.getTotalMemory());
        DeviceInfoUtil.getTotalMemory();
    }

    @OnClick({R.id.btn1,R.id.btn2,R.id.btn3})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                mTranslateLoadingView.setStatus(0);
                break;
            case R.id.btn2:
                mTranslateLoadingView.setStatus(1);
                break;
            case R.id.btn3:
                mTranslateLoadingView.setStatus(2);
                break;
        }
    }
}
