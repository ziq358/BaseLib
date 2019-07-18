package com.ziq.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jj on 2018/8/26.
 */

public class CameraUtils {
    private static final int AREA_SIZE = 100;
    private static int sCameraID = Camera.CameraInfo.CAMERA_FACING_BACK;//默认后置摄像头
    private static Camera sCamera;
    private static int sSurfaceWidth;
    private static int sSurfaceHeight;

    public static int getCameraID() {
        return sCameraID;
    }

    public static Camera getCamera() {
        return sCamera;
    }

    /**
     * 开始预览
     */
    public static void startPreview() {
        if (sCamera != null) {
            sCamera.startPreview();
        }
    }

    /**
     * 停止预览
     */
    public static void stopPreview() {
        if (sCamera != null) {
            sCamera.stopPreview();
        }
    }
    /**
     * 释放相机
     */
    public static void releaseCamera() {
        if (sCamera != null) {
            sCamera.stopPreview();
            sCamera.release();
            sCamera = null;
        }
    }


    public static void openCamera(Context context, int cameraID, int surfaceWidth, int surfaceHeight) throws Exception {
        checkCameraInitStatus();
        sSurfaceWidth = surfaceWidth;
        sSurfaceHeight = surfaceHeight;
        sCamera = Camera.open(cameraID);
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraID, info);
        sCameraID = info.facing;
        initCamera(context);
    }

    public static void openCamera(Context context, boolean isFront, int surfaceWidth, int surfaceHeight) throws Exception {
        checkCameraInitStatus();
        sSurfaceWidth = surfaceWidth;
        sSurfaceHeight = surfaceHeight;
        int targetFacing = isFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK;
        Camera.CameraInfo info = new Camera.CameraInfo();
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == targetFacing) {
                sCamera = Camera.open(i);
                sCameraID = info.facing;
                break;
            }
        }
        // 没有摄像头时，抛出异常
        checkCamera();
        initCamera(context);
    }

    private static void checkCamera() throws Exception {
        if (sCamera == null) {
            throw new Exception("Unable to open camera");
        }
    }

    private static void checkCameraInitStatus() throws Exception {
        if (sCamera != null) {
            throw new Exception("Camera already initialized!");
        }
    }

    private static void initCamera(Context context){
        initPreviewSize(context, sCamera, sSurfaceWidth, sSurfaceHeight);
        initPictureSize(context, sCamera, sSurfaceWidth, sSurfaceHeight);
    }

    //设置预览大小
    public static void initPreviewSize(Context context, android.hardware.Camera camera, int surfaceWidth, int surfaceHeight){
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        Log.i("CameraUtils", "initPreviewSize: ---"+parameters.getPreviewSize().width+" "+parameters.getPreviewSize().height);
        android.hardware.Camera.Size size = getCloselySize(isPortrait(context), surfaceWidth, surfaceHeight, parameters.getSupportedPreviewSizes());
        Log.i("CameraUtils", "initPreviewSize: --- target "+size.width+" "+size.height);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    public static void initPictureSize(Context context, android.hardware.Camera camera, int surfaceWidth, int surfaceHeight){
        android.hardware.Camera.Parameters parameters = camera.getParameters();
        Log.i("CameraUtils", "initPictureSize: ---"+parameters.getPictureSize().width+" "+parameters.getPictureSize().height);
        android.hardware.Camera.Size size = getCloselySize(isPortrait(context), surfaceWidth, surfaceHeight, parameters.getSupportedPictureSizes());
        Log.i("CameraUtils", "initPictureSize: --- target "+size.width+" "+size.height);
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);
    }

    //旋转角度
    public static int setCameraDisplayOrientation(Activity activity) throws Exception {
        checkCamera();
        return setCameraDisplayOrientation(activity, sCameraID, sCamera);
    }
    public static int setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        Log.i("CameraUtils", "mCameraRestOrientation: "+result);
        if(camera != null){
            camera.setDisplayOrientation(result);
        }
        return result;
    }

    //activity 方向
    public static boolean isPortrait(Context context){
        int orientation = context.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    public static android.hardware.Camera.Size getCloselySize(boolean isPortrait, int surfaceWidth, int surfaceHeight, List<android.hardware.Camera.Size> sizeList) {
        Log.i("CameraUtils", "getCloselySize: ----- surfaceWidth "+surfaceWidth+" surfaceHeight "+surfaceHeight);
        android.hardware.Camera.Size targetSize = null;
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
            for (android.hardware.Camera.Size size:sizeList){
                Log.i("CameraUtils", "getCloselySize: "+size.width+" "+size.height);
                if((size.width == reqTmpWidth) && (size.height == reqTmpHeight)){
                    return size;
                }
            }
            // 得到与传入的宽高比最接近的size
            float reqRatio = ((float) reqTmpWidth) / reqTmpHeight;
            float curRatio, deltaRatio;
            float deltaRatioMin = Float.MAX_VALUE;

            for (android.hardware.Camera.Size size : sizeList) {
                curRatio = ((float) size.width) / size.height;
                deltaRatio = Math.abs(reqRatio - curRatio);
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio;
                    targetSize = size;
                }
            }

        }
        return targetSize;
    }





    /**
     * 开始预览
     */
    public static void startPreviewDisplay(SurfaceHolder holder, Camera.PreviewCallback cb) throws Exception {
        checkCamera();
        sCamera.setPreviewDisplay(holder);
        sCamera.setPreviewCallback(cb);
        sCamera.startPreview();
    }

    /**
     * 对焦
     */
    public static void doFocus(MotionEvent event, Camera.AutoFocusCallback callback) throws Exception {
        checkCamera();
        doFocus(sCamera, event, sSurfaceWidth, sSurfaceHeight, callback);
    }


    public static void doFocus(Camera mCamera, MotionEvent event, int surfaceWidth, int surfaceHeight, Camera.AutoFocusCallback callback){
        mCamera.cancelAutoFocus();
        android.hardware.Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO);
        float touchX = (event.getX() / surfaceWidth) * 2000 - 1000;
        float touchY = (event.getY() / surfaceHeight) * 2000 - 1000;
        int left = clamp((int) touchX - AREA_SIZE / 2, -1000, 1000);
        int right = clamp(left + AREA_SIZE, -1000, 1000);
        int top = clamp((int) touchY - AREA_SIZE / 2, -1000, 1000);
        int bottom = clamp(top + AREA_SIZE, -1000, 1000);
        Rect rect = new Rect(left, top, right, bottom);

        if (parameters.getMaxNumFocusAreas() > 0) {
            List<Camera.Area> areaList = new ArrayList<Camera.Area>();
            areaList.add(new android.hardware.Camera.Area(rect, 1000));
            parameters.setFocusAreas(areaList);
        }

        if (parameters.getMaxNumMeteringAreas() > 0) {
            List<android.hardware.Camera.Area> areaList = new ArrayList<android.hardware.Camera.Area>();
            areaList.add(new android.hardware.Camera.Area(rect, 1000));
            parameters.setMeteringAreas(areaList);
        }
        mCamera.setParameters(parameters);
        mCamera.autoFocus(callback);
    }

    private static int clamp(int x, int min, int max) {//保证坐标必须在min到max之内，否则异常
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }





}
