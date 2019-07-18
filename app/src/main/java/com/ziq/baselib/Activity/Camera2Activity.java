package com.ziq.baselib.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.baselib.R;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class Camera2Activity extends MvpBaseActivity {

    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    SurfaceHolder mSurfaceHolder;
    Handler mainHandler;
    CameraDevice mCameraDevice;
    CaptureRequest.Builder mPreviewRequestBuilder;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_camera;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mainHandler = new Handler(getMainLooper());
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceHolder = holder;
                openCamera();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mSurfaceHolder = holder;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }


    @SuppressLint("MissingPermission")
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            //获取可用摄像头列表
            String targetCameraId = null;
            for (String cameraId : manager.getCameraIdList()) {
                //获取相机的相关参数
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {//前置摄像头 跳过
                    continue;
                }
                // 检查闪光灯是否支持。
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                // 获取摄像头支持的配置属性
                StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                //根据TextureView的尺寸设置预览尺寸
                Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
                List<Size> sizeList = Arrays.asList(sizes);
                Size mPreviewSize = getCloselySize(true, surfaceView.getWidth(), surfaceView.getHeight(), sizeList);
                mSurfaceHolder.setFixedSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
                Log.e("ziq", " 相机可用 " + cameraId + "mPreviewSize = "+mPreviewSize);
                targetCameraId = cameraId;
            }

            if(targetCameraId != null){
                manager.openCamera(targetCameraId, mStateCallback, mainHandler);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            mCameraDevice = cameraDevice;
            //创建CameraPreviewSession
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mCameraDevice = null;
        }

    };
    CameraCaptureSession mCaptureSession;
    CaptureRequest mPreviewRequest;
    private void createCameraPreviewSession() {
        try {
            //设置了一个具有输出Surface的CaptureRequest.Builder。
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(mSurfaceHolder.getSurface());
            //创建一个CameraCaptureSession来进行相机预览。
            mCameraDevice.createCaptureSession(Arrays.asList(mSurfaceHolder.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    // 相机已经关闭
                    if (null == mCameraDevice) {
                        return;
                    }
                    // 会话准备好后，我们开始显示预览
                    mCaptureSession = cameraCaptureSession;
                    try {
                        // 自动对焦应
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        // 闪光灯
//                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                        // 开启相机预览并添加事件
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        //发送请求
                        mCaptureSession.setRepeatingRequest(mPreviewRequest, null, mainHandler);
                        Log.e("ziq"," 开启相机预览并添加事件");
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Log.e("ziq"," onConfigureFailed 开启预览失败");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e("ziq"," CameraAccessException 开启预览失败");
        }
    }

    public static Size getCloselySize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<Size> sizeList) {
        Log.i("CameraUtils", "getCloselySize: ----- surfaceWidth "+surfaceWidth+" surfaceHeight "+surfaceHeight);
        Size targetSize = null;
        int reqTmpWidth;
        int reqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (isPortrait) {
            reqTmpWidth = surfaceHeight;
            reqTmpHeight = surfaceWidth;
        } else {
            reqTmpWidth = surfaceWidth;
            reqTmpHeight = surfaceHeight;
        }
        if(sizeList != null){
            for (Size size:sizeList){
                Log.i("CameraUtils", "getCloselySize: "+size.getWidth()+" "+size.getHeight());
                if((size.getWidth() == reqTmpWidth) && (size.getHeight() == reqTmpHeight)){
                    return size;
                }
            }
            // 得到与传入的宽高比最接近的size
            float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
            float curRatio, deltaRatio;
            float deltaRatioMin = Float.MAX_VALUE;

            for (Size size : sizeList) {
                curRatio = ((float) size.getWidth()) / size.getHeight();
                deltaRatio = Math.abs(reqRatio - curRatio);
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio;
                    targetSize = size;
                }
            }

        }
        return targetSize;
    }

}
