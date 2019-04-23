package com.ziq.base.mvvm.demo;

import android.os.Bundle;
import android.util.Log;

import com.ziq.base.R;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvvm.MvvmBaseActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;

public class LoginActivity extends MvvmBaseActivity {

    @Inject
    ILoginViewModel loginViewModel;//ILoginViewModel 会走 factory， 直接实例会 走实例的创建

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public void initForInject(AppComponent appComponent) {
        DaggerLoginComponent
                .builder()
                .appComponent(appComponent)
                .loginModule(new LoginModule(this))
                .build().inject(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        Log.e("ziq", "initData %d  "+ loginViewModel );
    }
}
