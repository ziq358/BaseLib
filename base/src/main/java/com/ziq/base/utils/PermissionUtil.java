package com.ziq.base.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class PermissionUtil {
    public static final String TAG = "Permission";


    private PermissionUtil() {
    }

    public interface RequestPermission {
        /**
         * 权限请求成功
         */
        void onRequestPermissionSuccess();
        /**
         * 用户拒绝了权限请求, 权限请求失败, 但还可以继续请求该权限
         */
        void onRequestPermissionFailure(List<String> permissions);
        /**
         * 用户拒绝了权限请求并且用户选择了以后不再询问, 权限请求失败, 这时将不能继续请求该权限, 需要提示用户进入设置页面打开该权限
         */
        void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions);
    }


    public static void requestPermission(final RequestPermission requestPermission, RxPermissions rxPermissions, String... permissions) {
        if (permissions == null || permissions.length == 0) return;

        List<String> needRequest = new ArrayList<>();
        for (String permission : permissions) { //过滤调已经申请过的权限
            if (!rxPermissions.isGranted(permission)) {
                needRequest.add(permission);
            }
        }

        if (needRequest.isEmpty()) {//全部权限都已经申请过，直接执行操作
            requestPermission.onRequestPermissionSuccess();
        } else {//没有申请过,则开始申请
            rxPermissions
                    .requestEach(needRequest.toArray(new String[needRequest.size()]))
                    .buffer(permissions.length)
                    .subscribe(new Observer<List<Permission>>() {
                        @Override
                        public void onSubscribe(Disposable d) { }

                        @Override
                        public void onNext(List<Permission> permissions) {
                            List<String> failurePermissions = new ArrayList<>();
                            List<String> failureWithAskNeverAgainPermissions = new ArrayList<>();
                            for (Permission p : permissions) {
                                if (!p.granted) {
                                    if (p.shouldShowRequestPermissionRationale) {
                                        failurePermissions.add(p.name);
                                    } else {
                                        failureWithAskNeverAgainPermissions.add(p.name);
                                    }
                                }
                            }

                            if(!failurePermissions.isEmpty()){
                                requestPermission.onRequestPermissionFailure(failurePermissions);
                                return;
                            }else if(!failureWithAskNeverAgainPermissions.isEmpty()){
                                requestPermission.onRequestPermissionFailureWithAskNeverAgain(failureWithAskNeverAgainPermissions);
                                return;
                            }else{
                                requestPermission.onRequestPermissionSuccess();
                            }
                        }

                        @Override
                        public void onError(Throwable e) { }

                        @Override
                        public void onComplete() { }
                    });
        }

    }

    public static boolean checkOverlayPermission(Context context){
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)){
                Toast.makeText(context, "当前无权限使用悬浮窗，请授权！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getApplicationContext().getPackageName()));
                context.getApplicationContext().startActivity(intent);
                result = false;
            }
        }
        return result;
    }

}