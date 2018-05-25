package com.ziq.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 负责 MD5 相关处理的工具类
 *
 * @author
 * @since 2015-10-15
 */
public class MD5Util {

    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    protected static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static MessageDigest createMD5Instance() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md;
    }

    /**
     * 生成字符串的md5校验值
     *
     * @param source 对应的字符串
     * @return 返回的MD5值
     */
    public static String getMD5String(String source) {
        return getMD5String(source.getBytes());
    }

    /**
     * 获得字节数组的MD5
     *
     * @param bytes 目标字节数组
     * @return 转化后的MD5字符串
     */
    public static String getMD5String(byte[] bytes) {
        MessageDigest md = createMD5Instance();
        if (md != null) {
            md.update(bytes);
            return bufferToHex(md.digest());
        }
        return "";
    }

    /**
     * 生成文件的md5校验值
     *
     * @param file 文件对象
     * @return 生成的MD5值
     */
    public static String getFileMD5String(File file) {
        MessageDigest md = createMD5Instance();
        InputStream fis = null;
        if (md != null) {
            try {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int numRead;
                while ((numRead = fis.read(buffer)) > 0) {
                    md.update(buffer, 0, numRead);
                }
                return bufferToHex(md.digest());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }


    private static String bufferToHex(byte[] bytes) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte[] bytes, int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    /**
     * 把字节转化为十六进制字符串 存入容器
     *
     * @param bt           字迹
     * @param stringBuffer 容器
     */
    private static void appendHexPair(byte bt, StringBuffer stringBuffer) {
        char c0 = HEX_DIGITS[(bt & 0xf0) >> 4]; // 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = HEX_DIGITS[bt & 0xf]; // 取字节中低 4 位的数字转换
        stringBuffer.append(c0);
        stringBuffer.append(c1);
    }
}