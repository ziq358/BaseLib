package com.ziq.base.mvp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.ziq.base.mvp.dagger.App;
import com.ziq.base.mvp.dagger.component.AppComponent;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author wuyanqiang
 */
public abstract class BaseFragment<P extends IBasePresenter> extends BaseRxFragment implements IBaseView{

    protected View mContentView;
    protected FragmentManager mChildFragmentManager;

    private Unbinder mUnbinder;
    protected KProgressHUD pd;
    @Inject
    protected P mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(initLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view;
        mUnbinder = ButterKnife.bind(this, mContentView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChildFragmentManager = getChildFragmentManager();
        Context context = getContext();
        if(context != null){
            Context applicationContext = context.getApplicationContext();
            if(applicationContext instanceof App){
                initForInject(((App) applicationContext).getAppComponent());
            }
        }
        initData(mContentView, savedInstanceState);
    }

    public abstract @LayoutRes int initLayoutResourceId();
    public abstract void initForInject(AppComponent appComponent);
    public abstract void initData(@NonNull View view, @Nullable Bundle savedInstanceState);


    @Override
    public void showLoading() {
        initLoadingDialog();
        pd.setLabel("正在加载中...");
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    @Override
    public void showLoading(String msg) {
        initLoadingDialog();
        pd.setLabel(msg);
        if (!pd.isShowing()) {
            pd.show();
        }
    }

    @Override
    public void hideLoading() {
        if (pd != null) {
            pd.dismiss();
        }
    }

    private void initLoadingDialog() {
        Context context = getContext();
        if (pd == null && context != null) {
            pd = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("正在加载中...")
                    .setCancellable(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            onCancelProgress();
                        }
                    });
        }
    }

    public void onCancelProgress() {

    }

    @Override
    public void onDestroy() {
        if(mUnbinder != null){
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if(mPresenter != null){
            mPresenter.destroy();
            mPresenter = null;
        }
        super.onDestroy();
    }

    public void addFragment(@IdRes int contentId, Fragment fragment, String tag, boolean isAddToBackStack) {
        if (mChildFragmentManager != null) {
            FragmentTransaction transaction = mChildFragmentManager.beginTransaction();
            transaction.add(contentId, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void replaceFragment(@IdRes int contentId, Fragment fragment, String tag, boolean isAddToBackStack) {
        if (mChildFragmentManager != null) {
            FragmentTransaction transaction = mChildFragmentManager.beginTransaction();
            transaction.replace(contentId, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

}
