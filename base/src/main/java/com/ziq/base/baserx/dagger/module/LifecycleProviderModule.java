package com.ziq.base.baserx.dagger.module;

import com.trello.rxlifecycle3.LifecycleProvider;

import dagger.Module;
import dagger.Provides;

/**
 * author: wuyanqiang
 * 2018/11/19
 */

@Module
public class LifecycleProviderModule {

    private LifecycleProvider lifecycleProvider;

    public LifecycleProviderModule(LifecycleProvider lifecycleProvider) {
        this.lifecycleProvider = lifecycleProvider;
    }

    @Provides
    public LifecycleProvider getLifecycleProvider(){
        return lifecycleProvider;
    }

}
