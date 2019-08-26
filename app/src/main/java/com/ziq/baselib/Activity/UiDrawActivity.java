package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.baselib.R;
import com.ziq.baselib.widget.UiDrawView;

import butterknife.BindView;

public class UiDrawActivity extends MvpBaseActivity {

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_ui_draw;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

}
