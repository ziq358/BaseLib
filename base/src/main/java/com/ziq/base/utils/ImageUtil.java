package com.ziq.base.utils;

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
     * @param path
     * @return
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

}
