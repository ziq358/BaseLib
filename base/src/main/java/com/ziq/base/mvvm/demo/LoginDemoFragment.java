package com.ziq.base.mvvm.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.databinding.DemoFragmentLoginBinding;
import com.ziq.base.mvvm.MvvmBaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoginDemoFragment extends MvvmBaseFragment {

    private DemoFragmentLoginBinding binding;

    @Inject
    ILoginDemoViewModel loginViewModel;//ILoginDemoViewModel 会走 factory， 直接实例会 走实例的创建


    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DemoFragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void initForInject(AppComponent appComponent) {
        DaggerLoginFragmentDemoComponent
                .builder()
                .appComponent(appComponent)
                .loginDemoModule(new LoginDemoModule(this))
                //使用activity  会得到activity 的viewmodel
//                .loginDemoModule(new LoginDemoModule(getActivity()))
                .build().inject(this);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        binding.setViewmodel(loginViewModel);
        Log.e("ziq", "fragment initData %d  "+ loginViewModel );
    }
}
