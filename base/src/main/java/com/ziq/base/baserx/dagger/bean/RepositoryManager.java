package com.ziq.base.baserx.dagger.bean;

import javax.inject.Inject;

import dagger.Lazy;
import retrofit2.Retrofit;

public class RepositoryManager implements IRepositoryManager {

    @Inject
    Lazy<Retrofit> mRetrofit;

    @Inject
    public RepositoryManager() {
    }

    @Override
    public <T> T createService(Class<T> service) {
        return mRetrofit.get().create(service);
    }
}
