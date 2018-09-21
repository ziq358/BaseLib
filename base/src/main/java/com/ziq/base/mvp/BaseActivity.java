package com.ziq.base.mvp;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ziq.base.utils.LogUtil;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public abstract class BaseActivity extends AppCompatActivity {
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

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.isDebug = true;
        super.onCreate(savedInstanceState);
        setContentView(initLayoutResourceId());
        mFragmentManager = getSupportFragmentManager();
        initData(savedInstanceState);
    }

    public abstract @LayoutRes int initLayoutResourceId();
    public abstract void initData(@Nullable Bundle savedInstanceState);

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
