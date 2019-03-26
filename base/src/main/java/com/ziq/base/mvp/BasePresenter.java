package com.ziq.base.mvp;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public abstract class BasePresenter implements IBasePresenter{
    @Inject
    protected LifecycleProvider lifecycleProvider;

    public LifecycleProvider getLifecycleProvider() {
        if(lifecycleProvider == null){
            throw new RuntimeException("LifecycleProvider 为空");
        }
        return lifecycleProvider;
    }
}
