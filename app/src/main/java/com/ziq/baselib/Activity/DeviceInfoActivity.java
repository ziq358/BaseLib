package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.utils.DeviceInfoUtil;
import com.ziq.baselib.R;

import butterknife.BindView;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class DeviceInfoActivity extends MvpBaseActivity {
    @BindView(R.id.tlv)
    TextView textView;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_device_info;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("\n系统版本: " + DeviceInfoUtil.getSystemVersion());
        stringBuilder.append("\n手机型号: " + DeviceInfoUtil.getSystemModel());
        stringBuilder.append("\n手机号: " + DeviceInfoUtil.getPhoneNumber(this));
        stringBuilder.append("\nIMEI: " + DeviceInfoUtil.getIMEI(this));
        stringBuilder.append("\nIMSI: " + DeviceInfoUtil.getIMSI(this));
        stringBuilder.append("\nCPU: " + DeviceInfoUtil.getCpuInfo());
        DeviceInfoUtil.getTotalMemory();
        textView.setText(stringBuilder.toString());
    }

}
