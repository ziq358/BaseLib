package com.ziq.baselib.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;

import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.baselib.R;

import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class UIBlockCheckActivity extends MvpBaseActivity implements View.OnClickListener {

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
