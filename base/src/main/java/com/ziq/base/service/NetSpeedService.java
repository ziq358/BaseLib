package com.ziq.base.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ziq.base.R;
import com.ziq.base.utils.DeviceInfoUtil;
import com.ziq.base.utils.NetSpeedUtil;

import java.lang.ref.WeakReference;

import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

/**
 * @author wuyanqiang
 * 2019/1/15
 */
public class NetSpeedService extends Service {


    View floatingWindow;
    TextView tvSpeed;
    WindowManager windowManager;
    WindowManager.LayoutParams wmParams;
    Handler mHandler;
    NetSpeedUtil netSpeedUtil;
    int uid;

    int displayHeight;
    int displayWidth;
    int statusBarHeight;
    int floatingWindowHeight;
    int floatingWindowWidth;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new NetSpeedHandler(this);
        netSpeedUtil = new NetSpeedUtil(getApplicationContext());
        uid = netSpeedUtil.getUid();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkPermissionAndShow();
        return START_STICKY;
    }

    private void checkPermissionAndShow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(this)){
                Toast.makeText(this, "当前无权限使用悬浮窗，请授权！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplication().getPackageName()));
                startActivity(intent);
            }else{
                showFloatingWindow();
            }
        }else{
            showFloatingWindow();
        }
    }


    private void showFloatingWindow(){
        if(floatingWindow == null){
            windowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);
            wmParams = new WindowManager.LayoutParams();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1 && Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
                wmParams.type =  WindowManager.LayoutParams.TYPE_TOAST;
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                wmParams.type =  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            }else{
                wmParams.type =  WindowManager.LayoutParams.TYPE_PHONE;
            }
            wmParams.flags = FLAG_NOT_TOUCH_MODAL|FLAG_NOT_FOCUSABLE|FLAG_LAYOUT_NO_LIMITS;

            wmParams.gravity = Gravity.LEFT | Gravity.TOP;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            displayHeight = DeviceInfoUtil.getDisplayHeight(getApplicationContext());
            displayWidth = DeviceInfoUtil.getDisplayWidth(getApplicationContext());
            statusBarHeight = DeviceInfoUtil.getStatusBarHeight(getApplicationContext());

            LayoutInflater inflater = LayoutInflater.from(getApplication());
            floatingWindow = inflater.inflate(R.layout.layout_net_speed, null);
            floatingWindow.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标

                    if(floatingWindowHeight == 0){
                        floatingWindowWidth = floatingWindow.getMeasuredWidth();
                        floatingWindowHeight = floatingWindow.getMeasuredHeight();
                    }

                    int targetX = (int) event.getRawX() - floatingWindowWidth / 2;
                    int targetY = (int) event.getRawY() - floatingWindowHeight / 2 - statusBarHeight;

                    if(targetX <= 0){
                        targetX = 0;
                    }else if(targetX + floatingWindowWidth >= displayWidth){
                        targetX = displayWidth - floatingWindowWidth;
                    }
                    if(targetY <= 0){
                        targetY = 0;
                    }else if(targetY + floatingWindowHeight >= displayHeight){
                        targetY = displayHeight - floatingWindowHeight - statusBarHeight;
                    }

                    wmParams.x = targetX;
                    wmParams.y = targetY;
                    windowManager.updateViewLayout(floatingWindow, wmParams);// 刷新
                    return false; // 此处必须返回false，否则OnClickListener获取不到监听
                }
            });

            tvSpeed = floatingWindow.findViewById(R.id.tv_speed);

            windowManager.addView(floatingWindow, wmParams);

            startTimer();
        }
    }

    public void updateSpeed(){
        floatingWindowWidth = floatingWindow.getMeasuredWidth();
        floatingWindowHeight = floatingWindow.getMeasuredHeight();
        tvSpeed.setText(netSpeedUtil.getIntervalToTalRxKB() + "k/s");
    }

    private static class NetSpeedHandler extends Handler{

        WeakReference<NetSpeedService> weakReference;

        public NetSpeedHandler(NetSpeedService speedService) {
            weakReference = new WeakReference<>(speedService);
        }

        @Override
        public void handleMessage(Message msg) {
            NetSpeedService speedService = weakReference.get();
            if(speedService != null){
                speedService.updateSpeed();
            }
        }
    }

    private Runnable timerRunnable;
    private void startTimer(){
        if(timerRunnable == null){
            timerRunnable = new Runnable() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(1);
                    mHandler.postDelayed(timerRunnable, 1000);
                }
            };
        }
        mHandler.post(timerRunnable);
    }


}
