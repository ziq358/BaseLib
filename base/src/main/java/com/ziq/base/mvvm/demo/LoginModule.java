package com.ziq.base.mvvm.demo;

import com.ziq.base.baserx.dagger.ActivityScope;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import dagger.Module;
import dagger.Provides;

@Module
public class LoginModule {

    ViewModelStoreOwner mViewModelStoreOwner;

    public LoginModule(ViewModelStoreOwner mViewModelStoreOwner) {
        this.mViewModelStoreOwner = mViewModelStoreOwner;
    }

    @Provides
    public ILoginViewModel provideLoginViewModel(LoginViewModelFactory factory) {
        //多了这一步， 主要是为了利用 ViewModelStore 保存实例
        return new ViewModelProvider(mViewModelStoreOwner, factory).get(LoginViewModel.class);
    }

}
