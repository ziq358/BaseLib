package com.ziq.base.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class CopyUtil {


    /**
     * 通过流， 深度复制
     *
     * @param oldObject 原对象
     * @param <E>       实参类必须实现Serializable接口，所有包含的对象
     * @return 新对象
     */
    public static <E extends Serializable> E copyObject(E oldObject) {
        E resultObject = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(oldObject);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            resultObject = (E) in.readObject();
        } catch (Exception e) {
            LogUtil.e("CopyUtil", e.getMessage());
        }
        return resultObject;
    }

}
