package com.ziq.baselib;

import android.widget.Toast;

import com.ziq.base.baserx.BaseApplication;
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
