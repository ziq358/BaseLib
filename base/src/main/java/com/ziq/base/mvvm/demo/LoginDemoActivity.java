package com.ziq.base.mvvm.demo;

import android.os.Bundle;
import android.util.Log;

import com.ziq.base.R;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.databinding.DemoActivityLoginBinding;
import com.ziq.base.mvvm.MvvmBaseActivity;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class LoginDemoActivity extends MvvmBaseActivity {

    @Inject
    ILoginDemoViewModel loginViewModel;//ILoginDemoViewModel 会走 factory， 直接实例会 走实例的创建

    DemoActivityLoginBinding binding;

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, R.layout.demo_activity_login);
    }

    @Override
    public void initForInject(AppComponent appComponent) {
        DaggerLoginDemoComponent
                .builder()
                .appComponent(appComponent)
                .loginDemoModule(new LoginDemoModule(this))
                .build().inject(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        binding.setViewmodel(loginViewModel);
        Log.e("ziq", "activity initData %d  "+ loginViewModel );
    }
}
