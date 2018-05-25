package com.ziq.base.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * 处理Byte与Byte[]相关逻辑的工具类
 *
 * @author
 * @since 2016 /2/26
 */
public class ByteUtils {

    /**
     * 字符转换为字节数组
     *
     * @param c 目标字符
     * @return 转换后的数组
     */
    public static byte[] char2Bytes(char c) {
        return new byte[]{(byte) c};
    }

    /**
     * 短整型转换为字节数组
     *
     * @param s 目标短整型
     * @return 转换后的数组
     */
    public static byte[] short2Bytes(short s) {
        return ByteBuffer.allocate(2).putShort(s).array();
    }

    /**
     * 整型转换为字节数组
     *
     * @param i 目标整型
     * @return 转换后的数组
     */
    public static byte[] integer2Bytes(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    /**
     * 长整型转换为字节数组
     *
     * @param l 目标长整型
     * @return 转换后的数组
     */
    public static byte[] long2Bytes(long l) {
        return ByteBuffer.allocate(8).putLong(l).array();
    }

    /**
     * 字节转化为比特 以字符串形式输出
     *
     * @param b 目标字节
     * @return 转化后的字符串
     */
    public static String byte2BitString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    /**
     * 短整型转化为比特 以字符串形式输出
     *
     * @param s 目标短整型
     * @return 转化后的字符串
     */
    public static String short2BitString(short s) {
        byte[] bytes = short2Bytes(s);
        return byte2BitString(bytes[0]) + byte2BitString(bytes[1]);
    }

    /**
     * 合并字节数组
     *
     * @param arrays 元素为字节数组的集合
     * @return 合并后的字节数组
     */
    public static byte[] joinByteArrays(List<byte[]> arrays) {
        byte[] result = {};

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            for (byte[] array : arrays) {
                stream.write(array);
            }
            stream.flush();
            result = stream.toByteArray();
        } catch (IOException ignored) {
        } finally {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        }

        return result;
    }

    /**
     * 合并多个字节数组
     *
     * @param arrays 参数为字节数组的可变参数
     * @return 合并后的字节数组
     */
    public static byte[] joinByteArrays(byte[]... arrays) {
        return joinByteArrays(Arrays.asList(arrays));
    }

}
