package com.ziq.base.baserx.dagger.component;

import android.app.Application;

import com.ziq.base.baserx.dagger.bean.IRepositoryManager;
import com.ziq.base.baserx.dagger.module.AppModule;
import com.ziq.base.baserx.dagger.module.GlobalConfigModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
@Singleton
@Component(modules = {AppModule.class, GlobalConfigModule.class})
public interface AppComponent {
    Application application();

    IRepositoryManager repositoryManager();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);//在 builder 之前 传入Application，作为可注射源，是 module提供源得另一种 方法

        Builder globalConfigModule(GlobalConfigModule globalConfigModule);

        AppComponent build();
    }
}
