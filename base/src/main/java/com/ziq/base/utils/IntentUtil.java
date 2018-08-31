package com.ziq.base.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class IntentUtil {

    public static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String TAG = "IntentUtil";

    public static void startApp(Context context, String packageName, Bundle bundle) {
        if (packageName != null) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.setPackage(packageName);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            PackageManager packageManager = context.getPackageManager();
            ResolveInfo resolveInfo = packageManager.queryIntentActivities(intent, 0).iterator().next();
            if (resolveInfo != null) {
                String className = resolveInfo.activityInfo.name;
                ComponentName componentName = new ComponentName(packageName, className);
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    }


    /**
     * 安装一般的应用, 兼容 7.0
     *
     * @param context  上下文
     * @param filePath 安装包路径
     * @return 安装包是否存在
     */
    public static boolean installNormal(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(FileUtil.getFileUri(context, filePath), "application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT >= 24) {
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
        return true;
    }


    /**
     * 卸载应用
     *
     * @param context     上下文
     * @param packageName package name of app
     * @return 是否成功
     */
    public static boolean uninstallNormal(Context context, String packageName) {
        if (packageName == null || packageName.length() == 0) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        return true;
    }


    public static boolean takePhoto(Activity activity, int requestCode, File output) {
        if (activity != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    public static boolean selectPhoto(Activity activity, int requestCode) {
        if (activity != null) {
            Intent intentPicture = new Intent(Intent.ACTION_PICK, null);
            intentPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(intentPicture, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos != null && !resolveInfos.isEmpty()) {
                activity.startActivityForResult(intentPicture, requestCode);
                return true;
            }
        }
        return false;
    }


    public static void getCroppedPhoto(Activity activity, int requestCode, Uri inputUri, Uri outputUri, int outputWight, int outputHeight) {
        if (activity != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(inputUri, IntentUtil.IMAGE_UNSPECIFIED);
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", outputWight);
            intent.putExtra("outputY", outputHeight);
            intent.putExtra("return-data", false);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            List<ResolveInfo> resolveInfos = activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos != null && !resolveInfos.isEmpty()) {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }

    public static void openDialer(Context context, String phoneNumber) {
        Intent phone = new Intent(Intent.ACTION_DIAL, Uri.parse(String.format("tel:%s", phoneNumber)));
        phone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(phone);
        } catch (ActivityNotFoundException e) {
            LogUtil.i(TAG, e.getMessage());
        }
    }

    /**
     * intent 的一种比较特别的使用方式，学习用
     * createChooser
     *
     * @param context 上下文
     * @param shareContent 内容
     * @param imageFile 文件
     * @param shareLink 链接
     */
    public static void sendShareIntent(Context context, String shareContent, File imageFile, String shareLink) {
        Intent targetIntent = new Intent(Intent.ACTION_SEND);
        targetIntent.setType("text/plain");
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> resInfoList = packageManager.queryIntentActivities(targetIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!resInfoList.isEmpty()) {
            List<LabeledIntent> shareIntentList = new ArrayList<>();
            for (ResolveInfo info : resInfoList) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                ActivityInfo activityInfo = info.activityInfo;
                String packageName = activityInfo.packageName;
                String name = activityInfo.name;
                if (packageName.contains("facebook")) {
                    if (shareLink == null) {
                        continue;
                    }
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareLink);
                } else {
                    if (imageFile == null) {
                        shareIntent.setType("text/plain");
                    } else {
                        shareIntent.setType("image/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
                    }
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                }
                shareIntent.setComponent(new ComponentName(packageName, name));
                shareIntent.setPackage(packageName);
                shareIntentList.add(new LabeledIntent(shareIntent, packageName, activityInfo.loadLabel(packageManager), activityInfo.icon));
            }
            Intent chooserIntent = Intent.createChooser(shareIntentList.remove(0), "share");
            if (chooserIntent == null) {
                return;
            }
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentList.toArray(new Parcelable[]{}));
            context.startActivity(chooserIntent);
        }
    }


}
