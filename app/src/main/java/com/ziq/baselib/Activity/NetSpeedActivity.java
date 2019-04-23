package com.ziq.baselib.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;

import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.service.NetSpeedService;
import com.ziq.base.utils.NetSpeedUtil;
import com.ziq.baselib.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class NetSpeedActivity extends MvpBaseActivity {
    @BindView(R.id.btn_download_speed)
    Button btn_download_speed;

    NetSpeedUtil netSpeedUtil;
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_net_speed;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        netSpeedUtil = new NetSpeedUtil(this);
    }

    @OnClick({R.id.btn_download_speed})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download_speed:
                Intent intent = new Intent(this, NetSpeedService.class);
                startService(intent);
                break;
        }
    }

}
