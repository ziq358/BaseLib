package com.ziq.base.mvp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.ziq.base.utils.LogUtil;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";


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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.isDebug = true;
        LogUtil.i(TAG, "activity onCreate: 1 " + this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        LogUtil.i(TAG, "activity onCreate: 2 " + this);// 没有调用
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        LogUtil.i(TAG, "activity onCreateView: name " + this);//多次调用
        return super.onCreateView(name, context, attrs);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//        LogUtil.i(TAG, "activity onCreateView: parent name " + this);//多次调用
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogUtil.i(TAG, "activity onNewIntent: " + this);
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        LogUtil.i(TAG, "activity onRestart: " + this);
        super.onRestart();
    }

    @Override
    public void onContentChanged() {
        LogUtil.i(TAG, "activity onContentChanged: " + this);

        super.onContentChanged();
    }

    @Override
    protected void onStart() {
        LogUtil.i(TAG, "activity onStart: " + this);
        super.onStart();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        LogUtil.i(TAG, "activity onPostCreate: 1" + this);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        LogUtil.i(TAG, "activity onResume: " + this);
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        LogUtil.i(TAG, "activity onPostResume: " + this);
        super.onPostResume();
    }

    @Override
    public void onAttachedToWindow() {
        LogUtil.i(TAG, "activity onAttachedToWindow: " + this);
        super.onAttachedToWindow();
    }

    @Nullable
    @Override
    public View onCreatePanelView(int featureId) {
        LogUtil.i(TAG, "activity onCreatePanelView: " + this);
        return super.onCreatePanelView(featureId);
    }

    @Override
    protected void onPause() {
        LogUtil.i(TAG, "activity onPause: " + this);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        LogUtil.i(TAG, "activity onSaveInstanceState: " + this);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        LogUtil.i(TAG, "activity onStop: " + this);
        super.onStop();
    }


    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        LogUtil.i(TAG, "activity onPostCreate: 2" + this);//没有调用
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        LogUtil.i(TAG, "activity onRestoreInstanceState: " + this);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        LogUtil.i(TAG, "activity onDestroy: " + this);
        super.onDestroy();
    }

    @Override
    public void onDetachedFromWindow() {
        LogUtil.i(TAG, "activity onDetachedFromWindow: " + this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.i(TAG, "activity onConfigurationChanged: " + this);

        super.onConfigurationChanged(newConfig);
    }

}
