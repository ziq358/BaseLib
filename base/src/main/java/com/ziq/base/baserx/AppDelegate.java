package com.ziq.base.baserx;

import android.app.Application;
import android.content.Context;

import com.ziq.base.baserx.dagger.App;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.baserx.dagger.component.DaggerAppComponent;
import com.ziq.base.baserx.dagger.module.ConfigModule;
import com.ziq.base.baserx.dagger.module.GlobalConfigModule;

import java.util.List;

import androidx.annotation.NonNull;

public class AppDelegate implements App {

    private AppComponent mAppComponent;
    private List<ConfigModule> mModules;

    public AppDelegate(Context context) {
        this.mModules = new ManifestParser(context).parse();
    }

    public void attachBaseContext(@NonNull Context base){

    }

    public void onCreate(@NonNull Application application){
        mAppComponent = DaggerAppComponent
                .builder()
                .application(application)//提供application
                .globalConfigModule(getGlobalConfigModule(application, mModules))
                .build();
        //异常捕捉
        CrashHandler.getInstance().init(application);
    }

    public void onTerminate(@NonNull Application application){

    }


    private GlobalConfigModule getGlobalConfigModule(Context context, List<ConfigModule> modules) {
        GlobalConfigModule.Builder builder = GlobalConfigModule.builder();
        //遍历 ConfigModule 集合, 给全局配置 GlobalConfigModule 添加参数
        for (ConfigModule module : modules) {
            module.applyCustomConfig(context, builder);
        }
        return builder.build();
    }

    @Override
    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
