package com.ziq.base.mvp;

import com.trello.rxlifecycle2.LifecycleTransformer;

import javax.inject.Inject;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public abstract class BasePresenter implements IBasePresenter{
    @Inject
    protected LifecycleTransformer lifecycleTransformer;

    public LifecycleTransformer getLifecycleTransformer() {
        if(lifecycleTransformer == null){
            throw new RuntimeException("lifecycleTransformer 为空");
        }
        return lifecycleTransformer;
    }
}
