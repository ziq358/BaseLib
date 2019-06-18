package com.ziq.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.ziq.base.utils.IntentUtil.IMAGE_UNSPECIFIED;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class PictureUtil {


    /**
     * 将Bitmap存到本地
     */
    public static String saveFile(Context context, Bitmap mBitmap) {
        try {
            File file = new File(FileUtil.getAlbumDir(), System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //扫描媒体库并加入其数据库
            scanMedia(context, file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 4.0以上版本则可利用MediaScannerConnection类来通知图库扫描图片，并加入其数据库。
     */
    public static void scanMedia(Context context, String path) {
        new SingleMediaScanner(context, path);
    }

    /**
     * 4.0以上版本则可利用MediaScannerConnection类来通知图库扫描图片，并加入其数据库。
     */
    private static class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private String path;

        public SingleMediaScanner(Context context, String path) {
            this.path = path;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mMs.scanFile(path, null);
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            mMs.disconnect();
        }

    }

    /**
     * 拍照
     */
    public static boolean takePhoto(Activity activity, int requestCode, File output) {
        if (activity != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));
            activity.startActivityForResult(intent, requestCode);
            return true;
        }
        return false;
    }

    /**
     * 选择图片
     */
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

    /**
     * 图片剪切
     */
    public static void getCroppedPhoto(Activity activity, int requestCode, Uri inputUri, Uri outputUri, int outputWight, int outputHeight) {
        if (activity != null) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(inputUri, IMAGE_UNSPECIFIED);
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




    /**
     * 获取旋转角度
     *
     * @param path 路径
     * @return 角度
     */
    public static int getRotationForImage(String path) {
        int rotation = 0;
        int exifOrientation = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation = 270;
                break;
        }

        return rotation;
    }

    /**
     * 将传入的 Bitmap 对象转换成对应的 Drawable 对象
     *
     * @param bitmap 需要转变成 Drawable 的 Bitmap 对象
     * @return 对应传入的 Bitmap 对象的 Drawable 对象
     */
    public static BitmapDrawable bitmap2Drawable(Bitmap bitmap) {
        return new BitmapDrawable(Resources.getSystem(), bitmap);
    }

    /**
     * 将传入的 Drawable 对象转换成对应的 Bitmap 对象
     *
     * @param drawable 需要转变成 Bitmap 的 Drawable 对象
     * @return 对应传入的 Drawable 对象的 Bitmap 对象
     */
    public static Bitmap drawable2Bitmap(BitmapDrawable drawable) {
        Bitmap bitmap = null;
        if (drawable != null) {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            drawable.draw(canvas);
        }
        return bitmap;
    }

    /**
     * 根据传入的 Bitmap 对象生成对应的圆角位图
     *
     * @param bitmap 需要被转化成圆角的位图
     * @param width  生成的位图的宽
     * @param height 生成的位图的高
     * @param radius 圆角的半径
     * @return 圆角位图
     */
    public static Bitmap obtainRoundCornerBitmap(Bitmap bitmap, int width, int height, int radius) {
        Bitmap result = bitmap;
        if (result != null && width > 0 && height > 0) {
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            if (result != null) {
                Canvas canvas = new Canvas(result);
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setColor(0xff424242);
                Rect rect = new Rect(0, 0, width, height);
                RectF rectF = new RectF(rect);
                canvas.drawARGB(0, 0, 0, 0);
                canvas.drawRoundRect(rectF, (float) radius, (float) radius, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, width, height, true)
                        , rect, rect, paint);
            }
        }
        return result;
    }

    public static Bitmap loadBitmapFromAssets(Context context, String filePath){
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream==null) return null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

}
