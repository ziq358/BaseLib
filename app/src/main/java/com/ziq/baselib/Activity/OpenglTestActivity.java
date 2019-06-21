package com.ziq.baselib.Activity;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.opengl.CubeRenderer;
import com.ziq.base.opengl.FullViewRenderer;
import com.ziq.base.opengl.MultipleCubeRenderer;
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
    MultipleCubeRenderer myRenderer;
    GestureDetector gestureDetector;
    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;//阻尼
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_opengl_test;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        myRenderer = new MultipleCubeRenderer(OpenglTestActivity.this);
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(myRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                myRenderer.mDeltaX = (myRenderer.mDeltaX + distanceX / sDensity * sDamping);
                myRenderer.mDeltaY = (myRenderer.mDeltaY + distanceY / sDensity * sDamping);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        //使得glSurfaceView的onTouch能够监听ACTION_DOWN以外的事件
        glSurfaceView.setClickable(true);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        myRenderer.onDestry();
        super.onDestroy();
    }
}
