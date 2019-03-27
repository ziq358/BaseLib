package com.ziq.base.mvp.dagger.module;

import com.trello.rxlifecycle2.LifecycleProvider;

import dagger.Module;
import dagger.Provides;

/**
 * author: wuyanqiang
 * 2018/11/19
 */

@Module
public class LifecycleProviderModule {

    private LifecycleProvider lifecycleProvider;

    public LifecycleProviderModule(LifecycleProvider lifecycleTransformer) {
        this.lifecycleProvider = lifecycleTransformer;
    }

    @Provides
    public LifecycleProvider getLifecycleProvider(){
        return lifecycleProvider;
    }

}
