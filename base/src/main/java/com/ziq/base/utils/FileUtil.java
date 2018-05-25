package com.ziq.base.utils;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class FileUtil {

    /**
     * fixed the Google Photos App get permission issue
     */
    public static Uri convertToValidUri(Context context, Uri data, File outputFile) {
        BufferedOutputStream bufferedOutputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(data);
            if (inputStream != null) {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
                bufferedInputStream = new BufferedInputStream(inputStream);
                byte[] buffers = new byte[1024];
                while (bufferedInputStream.read(buffers) > 0) {
                    bufferedOutputStream.write(buffers);
                }
                bufferedOutputStream.flush();

            }
        } catch (IOException ignore) {
        } finally {
            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close();
                    bufferedOutputStream = null;
                } catch (IOException exception) {
                }
            }
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                    bufferedInputStream = null;
                } catch (IOException exception) {
                }
            }
        }
        return Uri.fromFile(outputFile);
    }

}
