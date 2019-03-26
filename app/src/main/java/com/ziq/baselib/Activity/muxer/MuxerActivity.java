package com.ziq.baselib.Activity.muxer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ziq.base.dagger.component.AppComponent;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.CameraUtils;
import com.ziq.baselib.Constants;
import com.ziq.baselib.R;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class MuxerActivity extends BaseActivity implements Camera.PreviewCallback {

    public static final String TAG = "Muxer";
    private static final long TIMEOUT_US = 10000;
    @BindView(R.id.action)
    Button mAction;
    @BindView(R.id.path)
    TextView mTvPath;
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    Camera mCamera;
    SurfaceHolder mSurfaceHolder;

    MuxerThread mMuxerThread;

    private boolean isStarted;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_muxer;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
            // 申请 相机 麦克风权限
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;

                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                CameraUtils.setCameraDisplayOrientation(MuxerActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
                CameraUtils.initPreviewSize(MuxerActivity.this, mCamera, mSurfaceView.getWidth(), mSurfaceView.getHeight());
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.setPreviewCallback(MuxerActivity.this);
                    mCamera.startPreview();
                } catch (IOException e) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mSurfaceHolder = holder;
                mCamera.stopPreview();
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.setPreviewCallback(MuxerActivity.this);
                    mCamera.startPreview();
                } catch (IOException e) {
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CameraUtils.doFocus(mCamera, event, mSurfaceView.getWidth(), mSurfaceView.getHeight(), MuxerActivity.this);
                return false;
            }
        });
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Android camera preview默认格式为NV21的, 要 转为 I420（即YUV标准格式4：2：0）再进行下一步
        Log.d(TAG, "onPreviewFrame: " + data.length);
        if (mMuxerThread != null) {
            mMuxerThread.addVideoFrameData(data);
        }
    }

    @OnClick({R.id.action})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                if (isStarted) {
                    isStarted = false;
                    mAction.setText("开始");
                    stop();
                } else {
                    isStarted = true;
                    mAction.setText("停止");
                    start();
                }
                break;
        }
    }

    private void start() {
        String filePath = Constants.getDataDirPath(this, "muxer") + File.separator + System.currentTimeMillis() + ".mp4";
        Log.e(TAG, "filePath: " + filePath);
        mTvPath.setText(filePath);
        if (mCamera != null) {
            mMuxerThread = new MuxerThread(filePath, mCamera.getParameters().getPreviewSize().width, mCamera.getParameters().getPreviewSize().height);
            mMuxerThread.startMuxer();
        }
    }

    private void stop() {
        if (mMuxerThread != null) {
            mMuxerThread.stopMuxer();
            mMuxerThread = null;
        }
    }

}
