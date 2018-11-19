package com.ziq.baselib.dagger.module;

import com.ziq.baselib.presenter.RetrofitActivityPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
@Module
public class RetrofitModule {
    RetrofitActivityPresenter.View view;

    public RetrofitModule(RetrofitActivityPresenter.View view) {
        this.view = view;
    }

    @Provides
    public RetrofitActivityPresenter.View getView(){
        return view;
    }

}
