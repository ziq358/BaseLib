package com.ziq.base.dagger.module;

import com.trello.rxlifecycle2.LifecycleTransformer;

import dagger.Module;
import dagger.Provides;

/**
 * author: wuyanqiang
 * 2018/11/19
 */

@Module
public class RxLifecycleModule {

    private LifecycleTransformer lifecycleTransformer;

    public RxLifecycleModule(LifecycleTransformer lifecycleTransformer) {
        this.lifecycleTransformer = lifecycleTransformer;
    }

    @Provides
    public LifecycleTransformer getRxLifecycle(){
        return lifecycleTransformer;
    }
}
