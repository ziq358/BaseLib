package com.ziq.baselib;

import android.app.Application;

import com.ziq.base.mvp.BaseApplication;
import com.ziq.base.utils.RetrofitUtil;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class LibApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitUtil.getInstance().init(Constants.BASE_URL);
    }
}
