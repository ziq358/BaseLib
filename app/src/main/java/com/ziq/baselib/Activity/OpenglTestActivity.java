package com.ziq.baselib.Activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.opengl.CubeRenderer;
import com.ziq.base.opengl.FullViewRenderer;
import com.ziq.base.opengl.TriangleBufferRenderer;
import com.ziq.base.utils.PictureUtil;
import com.ziq.baselib.R;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.OnClick;

public class OpenglTestActivity extends MvpBaseActivity{

    @BindView(R.id.glSurfaceView)
    GLSurfaceView glSurfaceView;
    private String filePath="images/texture_360_n.jpg";
    CubeRenderer myRenderer;
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_opengl_test;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        myRenderer = new CubeRenderer(OpenglTestActivity.this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(myRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onDestroy() {
        myRenderer.onDestry();
        super.onDestroy();
    }
}
