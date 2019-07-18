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

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.utils.CameraUtils;
import com.ziq.base.utils.FileUtil;
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

public class MuxerActivity extends MvpBaseActivity implements Camera.PreviewCallback {

    public static final String TAG = "Muxer";
    private static final long TIMEOUT_US = 10000;
    @BindView(R.id.action)
    Button mAction;
    @BindView(R.id.path)
    TextView mTvPath;
    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

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
                try {
                    CameraUtils.openCamera(MuxerActivity.this, false, mSurfaceView.getWidth(), mSurfaceView.getHeight());
                    CameraUtils.setCameraDisplayOrientation(MuxerActivity.this);
                    CameraUtils.startPreviewDisplay(holder, MuxerActivity.this);
                } catch (Exception e) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mSurfaceHolder = holder;
                try {
                    CameraUtils.stopPreview();
                    CameraUtils.setCameraDisplayOrientation(MuxerActivity.this);
                    CameraUtils.startPreviewDisplay(holder, MuxerActivity.this);
                } catch (Exception e) {
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    CameraUtils.doFocus(event, new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean success, Camera camera) {
                            Toast.makeText(MuxerActivity.this, "onAutoFocus:\n" + success, Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        String filePath = FileUtil.getInnerSDCardAppPath(this) + File.separator + System.currentTimeMillis() + ".mp4";
        Log.e(TAG, "filePath: " + filePath);
        mTvPath.setText(filePath);
        if (CameraUtils.getCamera() != null) {
            mMuxerThread = new MuxerThread(filePath, CameraUtils.getCamera().getParameters().getPreviewSize().width, CameraUtils.getCamera().getParameters().getPreviewSize().height);
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
