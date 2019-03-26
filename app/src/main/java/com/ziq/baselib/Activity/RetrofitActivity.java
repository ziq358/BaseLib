package com.ziq.baselib.Activity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ziq.base.dagger.component.AppComponent;
import com.ziq.base.dagger.module.LifecycleProviderModule;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.NetSpeedUtil;
import com.ziq.baselib.R;
import com.ziq.baselib.dagger.component.DaggerRetrofitComponent;
import com.ziq.baselib.dagger.module.RetrofitModule;
import com.ziq.baselib.presenter.RetrofitActivityPresenter;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author wuyanqiang
 * @date 2018/10/12
 */
public class RetrofitActivity extends BaseActivity<RetrofitActivityPresenter> implements RetrofitActivityPresenter.View{

    @BindView(R.id.btn_get_video)
    Button mBtn;
    @BindView(R.id.btn_download_speed)
    Button btn_download_speed;
    @BindView(R.id.btn_upload_speed)
    Button btn_upload_speed;
    @Inject
    Application application;
    NetSpeedUtil netSpeedUtil;
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
        Log.e("ziq", "initData: "+application.getPackageName());
    }

    @Override
    public void onCancelProgress() {
        super.onCancelProgress();
        Log.e("ziq", "onCancelProgress: ");
    }

    @OnClick({R.id.btn_get_video, R.id.btn_test_lifecycle,R.id.btn_download_speed, R.id.btn_upload_speed})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_video:
                mPresenter.getVideo();
                break;
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
    public void update() {
        mBtn.setText("数据返回");
    }
}
