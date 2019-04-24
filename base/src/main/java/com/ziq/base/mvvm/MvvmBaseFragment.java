package com.ziq.base.mvvm;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.ziq.base.baserx.BaseRxFragment;
import com.ziq.base.baserx.dagger.App;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.IBasePresenter;
import com.ziq.base.mvp.IBaseView;

import javax.inject.Inject;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author wuyanqiang
 */
public abstract class MvvmBaseFragment extends BaseRxFragment implements IBaseView{

    protected FragmentManager mChildFragmentManager;

    protected KProgressHUD pd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView(inflater, container, savedInstanceState);
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
        initData(savedInstanceState);
    }

    public abstract View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
    public abstract void initForInject(AppComponent appComponent);
    public abstract void initData(@Nullable Bundle savedInstanceState);


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
