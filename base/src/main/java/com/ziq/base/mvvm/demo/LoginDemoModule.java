package com.ziq.base.mvvm.demo;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import dagger.Module;
import dagger.Provides;

@Module
public class LoginDemoModule {

    ViewModelStoreOwner mViewModelStoreOwner;

    public LoginDemoModule(ViewModelStoreOwner mViewModelStoreOwner) {
        this.mViewModelStoreOwner = mViewModelStoreOwner;
    }

    @Provides
    public ILoginDemoViewModel provideLoginViewModel(LoginDemoViewModelFactory factory) {
        //多了这一步， 主要是为了利用 ViewModelStore 保存实例
        return new ViewModelProvider(mViewModelStoreOwner, factory).get(LoginDemoViewModel.class);
    }

}
