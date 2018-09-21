package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.IntentUtil;
import com.ziq.baselib.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class InstallApkActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.input_install)
    EditText inputInstall;
    @BindView(R.id.input_uninstall)
    EditText inputUninstall;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_install_apk;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this);
        inputInstall.setText(FileUtil.getInnerSDCardAppPath(this) + "/app.apk");
        inputUninstall.setText(getPackageName());
    }

    @OnClick({R.id.install, R.id.uninstall})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install:
                IntentUtil.installNormal(this, inputInstall.getText().toString());
                break;
            case R.id.uninstall:
                IntentUtil.uninstallNormal(this, inputUninstall.getText().toString());
                break;
        }
    }
}
