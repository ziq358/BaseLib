package com.ziq.baselib.dagger.component;

import com.ziq.base.dagger.component.AppComponent;
import com.ziq.base.dagger.module.LifecycleProviderModule;
import com.ziq.baselib.Activity.RetrofitActivity;
import com.ziq.baselib.dagger.module.RetrofitModule;

import dagger.Component;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
@Component(modules = {RetrofitModule.class, LifecycleProviderModule.class},dependencies = {AppComponent.class})
public interface RetrofitComponent {
    void inject(RetrofitActivity activity);
}
