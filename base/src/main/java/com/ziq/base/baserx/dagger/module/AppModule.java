package com.ziq.base.baserx.dagger.module;

import com.ziq.base.baserx.dagger.bean.BaseUrl;
import com.ziq.base.baserx.dagger.bean.IRepositoryManager;
import com.ziq.base.baserx.dagger.bean.RepositoryManager;
import com.ziq.base.common.Constant;
import com.ziq.base.utils.RetrofitUtil;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public abstract class AppModule {

    @Binds     //@Binds 也是提供 实现的一种凡是 与@Provides 类似， 把入参的实现直接 返回
    abstract IRepositoryManager provideRepositoryManager(RepositoryManager repositoryManager);

    @Singleton
    @Provides
    static Retrofit provideRetrofit(RetrofitUtil retrofitUtil, BaseUrl baseUrl) {
        retrofitUtil.init(baseUrl.getUrl().toString());
        return retrofitUtil.getRetrofit();
    }

}
