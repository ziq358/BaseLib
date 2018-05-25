package com.ziq.baselib;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.LogUtil;
import com.ziq.base.utils.NetworkUtil;
import com.ziq.baselib.Activity.InstallApkActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    @Bind(R.id.recycle_view)
    RecyclerView mRecyclerView;

    MainRecycleViewAdapter mainRecycleViewAdapter;

    List<DemoListItem> dataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //打开 log
        LogUtil.isDebug = true;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        initData();
        mainRecycleViewAdapter = new MainRecycleViewAdapter(this, dataList);
        mRecyclerView.setAdapter(mainRecycleViewAdapter);
        NetworkInfo networkInfo = NetworkUtil.getNetworkInfo(this);
        LogUtil.i(TAG, " " + networkInfo);
    }

    private void initData() {
        dataList.add(new DemoListItem("DEMO-安装apk", InstallApkActivity.class));
    }

    public static class DemoListItem{
        public String name;
        public Class cls;

        public DemoListItem(String name, Class cls) {
            this.name = name;
            this.cls = cls;
        }
    }

}
