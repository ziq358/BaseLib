package com.ziq.baselib.Activity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.baserx.dagger.module.LifecycleProviderModule;
import com.ziq.base.utils.NetSpeedUtil;
import com.ziq.baselib.R;
import com.ziq.baselib.adapter.RecommendAdapter;
import com.ziq.baselib.dagger.component.DaggerRetrofitComponent;
import com.ziq.baselib.dagger.module.RetrofitModule;
import com.ziq.baselib.model.LiveListItemBean;
import com.ziq.baselib.presenter.RetrofitActivityPresenter;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author wuyanqiang
 * @date 2018/10/12
 */
public class RetrofitActivity extends MvpBaseActivity<RetrofitActivityPresenter> implements RetrofitActivityPresenter.View{

    @BindView(R.id.btn_download_speed)
    Button btn_download_speed;
    @BindView(R.id.btn_upload_speed)
    Button btn_upload_speed;
    @Inject
    Application application;
    NetSpeedUtil netSpeedUtil;

    @BindView(R.id.smartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.recycleView)
    RecyclerView recycleView;
    RecommendAdapter adapter;
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_retrofit;
    }

    @Override
    public void initForInject(AppComponent appComponent) {
        DaggerRetrofitComponent
                .builder()
                .lifecycleProviderModule(new LifecycleProviderModule(this))
                .retrofitModule(new RetrofitModule(this))
                .appComponent(appComponent)
                .build()
                .inject(this);

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        netSpeedUtil = new NetSpeedUtil(this);

        adapter = new RecommendAdapter(new ArrayList());
        recycleView.setAdapter(adapter);
        recycleView.setLayoutManager(new GridLayoutManager(this, 2));

        mSmartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                mPresenter.getVideo(false);
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                mPresenter.getVideo(true);

            }
        });
        mSmartRefreshLayout.autoRefresh();
    }


    @OnClick({R.id.btn_test_lifecycle,R.id.btn_download_speed, R.id.btn_upload_speed})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test_lifecycle:
                mPresenter.testLifecycle();
                break;
            case R.id.btn_download_speed:
                btn_download_speed.setText("下载 "+ netSpeedUtil.getIntervalRxKB(netSpeedUtil.getUid()) + "kb");
                break;
            case R.id.btn_upload_speed:
                btn_upload_speed.setText("上传 "+ netSpeedUtil.getIntervalTxKB(netSpeedUtil.getUid()) + "kb");
                break;
        }
    }


    @Override
    public void update(ArrayList<LiveListItemBean> dataList , boolean isRefresh) {
        if (isRefresh) {
            adapter.getData().clear();
            mSmartRefreshLayout.finishRefresh();
        } else {
            mSmartRefreshLayout.finishLoadMore();
        }
        mSmartRefreshLayout.setEnableLoadMore(!dataList.isEmpty());
        adapter.getData().addAll(dataList);
        adapter.notifyDataSetChanged();
    }
}
