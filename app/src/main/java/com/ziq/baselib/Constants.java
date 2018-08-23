package com.ziq.baselib;

import android.content.Context;

import com.ziq.base.utils.FileUtil;

import java.io.File;

/**
 * Created by jj on 2018/8/23.
 */

public class Constants {

    public static String getDataDirPath(Context context, String dir) {
        String path = context.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + dir;
        if (FileUtil.isExistSDCard()) {
            path = context.getExternalCacheDir().getAbsolutePath() + File.separator + dir;
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }

}
