package com.ziq.base.mvp;

import android.app.Application;

import com.ziq.base.dagger.App;
import com.ziq.base.dagger.component.AppComponent;
import com.ziq.base.dagger.component.DaggerAppComponent;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class BaseApplication extends Application implements App {

    AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent
                .builder()
                .application(this)//提供application
                .build();
    }

    @Override
    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
