package com.ziq.baselib.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.ziq.base.opengl.SphereRenderer;
import com.ziq.base.opengl.TriangleBufferRenderer;
import com.ziq.base.utils.PictureUtil;
import com.ziq.baselib.R;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import butterknife.BindView;
import butterknife.OnClick;

public class OpenglTestActivity extends MvpBaseActivity implements SensorEventListener {

    @BindView(R.id.glSurfaceView)
    GLSurfaceView glSurfaceView;
    String filePath="images/cinema.jpg";
    SphereRenderer myRenderer;
    GestureDetector gestureDetector;
    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;//阻尼

    SensorManager sensorManager;
    Sensor gyroscopeSensor;

    private final float NS2S = 1.0f / 1000000000.0f;
    long timestamp;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_opengl_test;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        myRenderer = new SphereRenderer(OpenglTestActivity.this, filePath);
        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setRenderer(myRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.e("ziq", "度数1: "+distanceX / sDensity * sDamping + " "+ distanceY / sDensity * sDamping);
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

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);// 陀螺仪传感器，单位是rad/s，测量设备x、y、z三轴的角加速度

    }

    @Override
    protected void onResume() {
        super.onResume();
        //SensorManager.SENSOR_DELAY_GAME 影响回调次数， 敏感度够了才不会卡顿
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                float deltaX = (float) Math.toDegrees(event.values[0] * dT);
                float deltaY = (float) Math.toDegrees(event.values[1] * dT);
                float deltaZ = (float) Math.toDegrees(event.values[2] * dT);
                Log.e("ziq", "度数2: "+deltaX + " "+ deltaY+ " "+ deltaZ );
                //方向相反 减法
                myRenderer.mDeltaX = (myRenderer.mDeltaX - deltaY);
                myRenderer.mDeltaY = (myRenderer.mDeltaY - deltaX);

            }
            timestamp = event.timestamp;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onDestroy() {
        myRenderer.onDestry();
        super.onDestroy();
    }


}
