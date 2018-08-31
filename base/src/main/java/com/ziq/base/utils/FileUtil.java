package com.ziq.base.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class FileUtil {

    /**
     * 获取内置SD卡路径
     *
     * @return 路径
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 需要申请读写 sd卡 的权限， 不然目录、文件无法建立
     *
     * @param context 上下文
     * @return 字符
     */
    public static String getInnerSDCardAppPath(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "需要获取SD卡读写权限", Toast.LENGTH_SHORT).show();
        } else {
            String path = getInnerSDCardPath() + File.separator + AppInfoUtil.getApplicationName(context, context.getPackageName());
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            if (file.exists()) {
                return path;
            }
        }
        return "";
    }

    public static boolean isExistSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取外置SD卡路径
     *
     * @return 应该就一条记录或空
     */
    public static List<String> getExtSDCardPath() {
        List<String> lResult = new ArrayList<String>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("extSdCard")) {
                    String[] arr = line.split(" ");
                    String path = arr[1];
                    File file = new File(path);
                    if (file.isDirectory()) {
                        lResult.add(path);
                    }
                }
            }
            isr.close();
        } catch (Exception e) {
        }
        return lResult;
    }

    /**
     * 获取File 方式的Uri，兼容android 7.0
     *
     * @param context  上下文
     * @param filePath 文件路径
     * @return File uri
     */
    public static Uri getFileUri(Context context, String filePath) {
        Uri fileUri = null;
        File file = new File(filePath);
        if (!file.exists()) {
            return fileUri;
        }
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));
        } else {
            fileUri = Uri.parse("file://" + filePath);
        }
        return fileUri;
    }


    /**
     * fixed the Google Photos App get permission issue
     * @param context 上下文
     * @param data 上下文
     * @param outputFile 文件
     * @return 字符
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


    /**
     * 读取文件内容，并以字符串返回
     *
     * @param filePath 被读取的文件的路径
     * @return 文件的内容
     */
    public static String readFile(String filePath) {
        File file = new File(filePath);
        StringBuilder content = new StringBuilder("");
        BufferedReader reader = null;
        String line;
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return content.toString();
    }

    /**
     * 读取文件的内容，并将每一行作为独立的字符串对象封装到容器
     *
     * @param filePath 被读取的问题件的路径
     * @return 装载了每一行字符串的容器
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        BufferedReader reader = null;
        String line;
        if (file.exists() && file.isFile() && file.canRead()) {
            try {
                reader = new BufferedReader(new FileReader(file));
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return lines;
    }

    /**
     * 读取获得assets目录下文件的内容
     *
     * @param context  上下文对象
     * @param fileName assets目录下的文件名
     * @return 文件的内容
     */
    public static String getAssets(Context context, String fileName) {
        String line;
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        StringBuilder result = new StringBuilder();
        try {
            inputStream = context.getResources().getAssets().open(fileName);
            inputReader = new InputStreamReader(inputStream, "utf-8");
            bufReader = new BufferedReader(inputReader);
            while ((line = bufReader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }

    /**
     * 将指定的输入流的内容写入到指定的文件
     *
     * @param filePath 保存输入流的文件
     * @param is       输入流
     */
    public static void writeFile(String filePath, InputStream is) {
        File file = new File(filePath);
        OutputStream os = null;
        try {
            if (!file.exists() && !file.createNewFile()) {
                return;
            } else if (file.isFile() && file.canWrite()) {
                os = new FileOutputStream(file);
                byte[] data = new byte[1024];
                int length;
                while ((length = is.read(data)) != -1) {
                    os.write(data, 0, length);
                }
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将指定的字符串内容追加到指定的文件
     *
     * @param filePath 保存追加内容的文件
     * @param content  追加的内容
     * @param append   是否在文件的末尾增加追加的内容，true 表示是，false 表示否
     */
    public static void writeFile(String filePath, String content, boolean append) {
        File file = new File(filePath);
        FileWriter fileWriter = null;
        try {
            if (!file.exists() && !file.createNewFile()) {
                return;
            } else if (file.isFile() && file.canWrite()) {
                fileWriter = new FileWriter(filePath, append);
                fileWriter.write(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据指定的文件路径，删除指定文件
     *
     * @param filePath 需要被删除的文件的路径
     * @return 文件是否删除成功
     */
    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.delete();
    }

    /**
     * 删除指定目录下的所有文件
     *
     * @param directory 需要被删除的目录
     * @return 是否删除成功
     */
    public static boolean deleteDirectory(File directory) {
        if (null == directory || !directory.exists()) {
            return false;
        }
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    /**
     * 获取指定文件夹
     *
     * @param f 文件
     * @return 数量
     * @throws Exception 异常
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size = size + getFileSizes(files[i]);
            } else {
                size = size + getFileSize(files[i]);
            }
        }
        return size;
    }


    /**
     * 获取指定文件大小
     *
     * @param file 文件
     * @return 大小
     * @throws Exception 异常
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
        }
        return size;
    }

    /**
     * 获得指定文件的大小
     *
     * @param filePath 文件路径
     * @return 指定文件的大小，如果获取成功，则返回真正的文件大小，否则，返回 -1
     */
    public static long getFileSize(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                return file.length();
            }
        }
        return -1;
    }

    /**
     * 重命名文件/文件夹
     *
     * @param oldName 旧名字
     * @param newName 新名字
     * @return 是否成功
     */
    public static boolean rename(String oldName, String newName) {
        if (!TextUtils.isEmpty(oldName) && !TextUtils.isEmpty(newName)) {
            File oldFile = new File(oldName);
            File newFile = new File(newName);
            return oldFile.exists() && oldFile.renameTo(newFile);
        }
        return false;
    }

}
