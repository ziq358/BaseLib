package com.ziq.base.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;

import java.io.IOException;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class ImageUtil {


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

}
