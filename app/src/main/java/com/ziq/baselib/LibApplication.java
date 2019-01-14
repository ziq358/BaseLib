package com.ziq.baselib;

import android.app.Application;
import android.widget.Toast;

import com.ziq.base.mvp.BaseApplication;
import com.ziq.base.utils.RetrofitUtil;
import com.ziq.base.utils.performance.BlockDetectByChoreographer;
import com.ziq.base.utils.performance.UIBlockMonitor;

/**
 * author: wuyanqiang
 * 2018/11/19
 */
public class LibApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitUtil.getInstance().init(Constants.BASE_URL);
        //UI 卡顿 监控
        BlockDetectByChoreographer.start();
        UIBlockMonitor.setListener(new UIBlockMonitor.UIBlockListener() {
            @Override
            public void onUIBlock() {
                Toast.makeText(LibApplication.this, "UI 卡顿", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
