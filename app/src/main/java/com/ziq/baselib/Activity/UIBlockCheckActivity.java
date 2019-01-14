package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.ziq.base.dagger.component.AppComponent;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.IntentUtil;
import com.ziq.base.utils.performance.UIBlockMonitor;
import com.ziq.baselib.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class UIBlockCheckActivity extends BaseActivity implements View.OnClickListener {

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_ui_block;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
    }

    @OnClick({R.id.sleep})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sleep:
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                break;
        }
    }
}
