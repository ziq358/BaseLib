package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.IntentUtil;
import com.ziq.baselib.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class InstallApkActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.input)
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_apk);
        ButterKnife.bind(this);
        input.setText(FileUtil.getInnerSDCardAppPath(this) + "/app.apk");
    }

    @OnClick({R.id.install})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.install:
                IntentUtil.installNormal(this, input.getText().toString());
                break;
        }
    }
}
