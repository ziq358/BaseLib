package com.ziq.base.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jose4j.lang.StringUtil;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class RetrofitUtil {

    private Retrofit mRetrofit;


    @Inject
    public RetrofitUtil() {
    }

    public void init(String baseUrl){
        // 初始化okhttp
        if(!TextUtils.isEmpty(baseUrl)){
            if(mRetrofit == null){
                HttpLoggingInterceptor loggerInterceptor = new HttpLoggingInterceptor();//打印 信息
                loggerInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(loggerInterceptor).build();
                // 初始化Retrofit
                mRetrofit = new Retrofit.Builder()
                        .client(client)
                        .baseUrl(baseUrl)
                        .addConverterFactory(GsonConverterFactory.create(buildGson()))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();
            }else{
                throw new RuntimeException("RetrofitUtil 初始化 不能重复初始化");
            }
        }else{
            throw new RuntimeException("RetrofitUtil 初始化 baseUrl 不能为空");
        }
    }

    public Retrofit getRetrofit(){
        if(mRetrofit == null){
            throw new RuntimeException("RetrofitUtil 必须先 调用 init（）");
        }
        return mRetrofit;
    }


    public Gson buildGson() {
        Gson gson = new GsonBuilder().create();
        return gson;
    }

}
