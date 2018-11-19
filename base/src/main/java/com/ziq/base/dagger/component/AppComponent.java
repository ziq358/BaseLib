package com.ziq.base.dagger.component;

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
@Component()
public interface AppComponent {
    Application application();
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);//在 builder 之前 传入Application，作为可注射源，是 module提供源得另一种 方法
        AppComponent build();
    }
}
