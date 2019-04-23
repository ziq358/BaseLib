package com.ziq.base.baserx.dagger.bean;

import okhttp3.HttpUrl;

public class BaseUrl {

    private HttpUrl url;

    public BaseUrl(HttpUrl url) {
        this.url = url;
    }

    public HttpUrl getUrl() {
        return url;
    }
}