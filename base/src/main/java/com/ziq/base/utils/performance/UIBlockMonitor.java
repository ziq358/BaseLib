package com.ziq.base.utils.performance;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Method;

public class UIBlockMonitor {

    public static final String TAG = "UIBlockMonitor";
    private static UIBlockMonitor sInstance = new UIBlockMonitor();
    private HandlerThread mLogThread = new HandlerThread("log");
    private Handler mIoHandler;
    private static final long TIME_BLOCK = 1000L; //卡顿时间阈值
    private static boolean isStarted;

    private static UIBlockListener mListener;

    public static UIBlockMonitor getInstance() {
        return sInstance;
    }

    public static void setListener(UIBlockListener mListener) {
        UIBlockMonitor.mListener = mListener;
    }

    private UIBlockMonitor() {
        mLogThread.start();
        mIoHandler = new Handler(mLogThread.getLooper());
    }

    private static Runnable mBlockRunnable = new Runnable() {
        @Override
        public void run() {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stackTrace = Looper.getMainLooper().getThread().getStackTrace();
            for (StackTraceElement s : stackTrace) {
                sb.append(s.toString() + "\n");
            }
            Log.e(TAG, sb.toString());
            isStarted = false;
            if(mListener != null ){
                mListener.onUIBlock();
            }
        }
    };



    public boolean isMonitoring() {
        boolean result;
        try {
            Method method = mIoHandler.getClass().getDeclaredMethod("hasCallbacks", Runnable.class);
            result = (boolean) method.invoke(mIoHandler, mBlockRunnable);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            result = isStarted;
        }
        return result;
    }

    public void startMonitor() {
        isStarted = true;
        mIoHandler.postDelayed(mBlockRunnable, TIME_BLOCK);
    }

    public void removeMonitor() {
        isStarted = false;
        mIoHandler.removeCallbacks(mBlockRunnable);
    }

    public interface UIBlockListener{
        void onUIBlock();
    }

}
