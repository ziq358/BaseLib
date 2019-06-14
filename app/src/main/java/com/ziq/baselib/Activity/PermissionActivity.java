package com.ziq.baselib.Activity;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.utils.PermissionUtil;
import com.ziq.baselib.R;

import java.util.List;

import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class PermissionActivity extends MvpBaseActivity implements View.OnClickListener {

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_ui_permission;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
    }

    @OnClick({R.id.btn_request_permission})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request_permission:
                requestPermission();
                break;
        }
    }

    public void requestPermission() {
        PermissionUtil.requestPermission(
                new PermissionUtil.RequestPermission() {
                    @Override
                    public void onRequestPermissionSuccess() {

                    }

                    @Override
                    public void onRequestPermissionFailure(List<String> permissions) {
                        Log.e("ziq", "onRequestPermissionFailure: \n"+permissions);
                    }

                    @Override
                    public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
                        Log.e("ziq", "onRequestPermissionFailureWithAskNeverAgain: \n"+permissions);
                    }
                }, new RxPermissions(this),
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        );
    }
}
