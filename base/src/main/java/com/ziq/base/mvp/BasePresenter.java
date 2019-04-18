package com.ziq.base.mvp;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.ziq.base.utils.LifecycleUtil;

import javax.inject.Inject;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public abstract class BasePresenter implements IBasePresenter{
    @Inject
    protected LifecycleProvider lifecycleProvider;

    public <T> LifecycleTransformer<T> getDestroyLifecycleTransformer() {
        return LifecycleUtil.bindToDestroy(lifecycleProvider);
    }
}
