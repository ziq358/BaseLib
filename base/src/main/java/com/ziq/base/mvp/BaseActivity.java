package com.ziq.base.mvp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.ziq.base.mvp.dagger.App;
import com.ziq.base.mvp.dagger.component.AppComponent;
import com.ziq.base.utils.LogUtil;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public abstract class BaseActivity<P extends IBasePresenter> extends BaseRxActivity implements IBaseView{
//    生命周期
//    onCreate –> onContentChanged –> onStart –> onPostCreate –> onResume –> onPostResume –> onAttachedToWindow
//    onPause -> onSaveInstanceState -> onStop 显示后台任务按钮时
//    onDestroy -> onDetachedFromWindow 在后台时候， 手动关掉
//    onPause -> onStop -> onDestroy -> onDetachedFromWindow 返回键退出
//    onRestart -> onStart -> onResume -> onPostResume 回到前台
//    旋转 重构实例：第一个实例的生命周期走完 ，在新建 一个实例走生命周期
//       onPause ->  onSaveInstanceState -> onStop -> onDestroy -> onDetachedFromWindow
//              -> onCreate -> onCreateView -> onContentChanged -> onStart -> onRestoreInstanceState -> onPostCreate
//              -> onResume -> onPostResume -> onAttachedToWindow

//    启动新的Activity：
//          onPause ->
//              onCreate –> onContentChanged –> onStart –> onPostCreate –> onResume –> onPostResume –> onAttachedToWindow
//          onSaveInstanceState ->
//          onStop
//    新Activity 返回退出：
//              onPause ->
//          onRestart -> onStart ->  onResume -> onPostResume ->
//              onStop -> onDestroy -> onDetachedFromWindow

//    设置了 android:configChanges="orientation|screenSize|keyboardHidden"
//      只会回调  ：  onConfigurationChanged
//    新Activity 返回退出：
//              onPause ->
//          onConfigurationChanged -> onRestart -> onStart ->  onResume -> onPostResume ->
//              onStop -> onDestroy -> onDetachedFromWindow

    protected FragmentManager mFragmentManager;
    private Unbinder mUnbinder;
    protected KProgressHUD pd;

    @Inject
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.isDebug = true;
        super.onCreate(savedInstanceState);
        setContentView(initLayoutResourceId());
        mUnbinder = ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        Context applicationContext = getApplicationContext();
        if(applicationContext instanceof App){
            initForInject(((App) applicationContext).getAppComponent());
        }
        initData(savedInstanceState);
    }

    public abstract @LayoutRes int initLayoutResourceId();
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
        if (pd == null) {
            pd = KProgressHUD.create(this)
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
    protected void onDestroy() {
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
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.add(contentId, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void replaceFragment(@IdRes int contentId, Fragment fragment, String tag, boolean isAddToBackStack) {
        if (mFragmentManager != null) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(contentId, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

}
