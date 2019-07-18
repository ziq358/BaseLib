package com.ziq.baselib.Activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.baselib.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;

public class CameraActivity extends MvpBaseActivity {

    @BindView(R.id.glSurfaceView)
    GLSurfaceView glSurfaceView;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_camera;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //一定要设置renderer， GLThread 才会建立，不如会出错
        glSurfaceView.setRenderer(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override
            public void onDrawFrame(GL10 gl) {

            }
        });
    }
}
