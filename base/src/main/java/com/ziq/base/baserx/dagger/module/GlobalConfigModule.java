package com.ziq.base.baserx.dagger.module;

import android.text.TextUtils;

import com.ziq.base.baserx.dagger.bean.BaseUrl;
import com.ziq.base.common.Constant;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;

@Module
public class GlobalConfigModule {

    private BaseUrl mBaseUrl;

    private GlobalConfigModule(Builder builder) {
        this.mBaseUrl = builder.baseUrl;
    }

    @Singleton
    @Provides
    BaseUrl provideBaseUrl() {
        if(mBaseUrl == null){
            mBaseUrl = new BaseUrl(HttpUrl.parse(Constant.ERROR_URL));
        }
        return mBaseUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private BaseUrl baseUrl;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public GlobalConfigModule build() {
            return new GlobalConfigModule(this);
        }

        public Builder setBaseurl(String baseUrl) {//基础url
            if (TextUtils.isEmpty(baseUrl)) {
                throw new NullPointerException("BaseUrl can not be empty");
            }
            this.baseUrl = new BaseUrl(HttpUrl.parse(baseUrl));
            return this;
        }
    }

}
