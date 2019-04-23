package com.ziq.baselib.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.utils.DeviceInfoUtil;
import com.ziq.base.utils.LogUtil;
import com.ziq.baselib.R;
import com.ziq.baselib.widget.TranslateLoadingView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class TestActivity extends MvpBaseActivity implements View.OnClickListener {
    @BindView(R.id.tlv)
    TranslateLoadingView mTranslateLoadingView;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_test;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        LogUtil.i("ziq", "系统版本: " + DeviceInfoUtil.getSystemVersion());
        LogUtil.i("ziq", "手机型号: " + DeviceInfoUtil.getSystemModel());
        LogUtil.i("ziq", "手机号: " + DeviceInfoUtil.getPhoneNumber(this));
        LogUtil.i("ziq", "IMEI: " + DeviceInfoUtil.getIMEI(this));
        LogUtil.i("ziq", "IMSI: " + DeviceInfoUtil.getIMSI(this));
        LogUtil.i("ziq", "CPU: " + DeviceInfoUtil.getCpuInfo());
//        LogUtil.i("ziq", "rom: "+ DeviceInfoUtil.getTotalMemory());
        DeviceInfoUtil.getTotalMemory();
    }

    @OnClick({R.id.btn1, R.id.btn2, R.id.btn3})
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
