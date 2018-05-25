package com.ziq.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 负责读取 AndroidManifest.xml 中的 meta 数据
 *
 * @author
 */
public class MetaUtils {

    /**
     * 获取 AndroidManifest.xml 中指定键值所对应的数据内容
     *
     * @param context 上下文
     * @param key     键值
     * @return 指定键值所对应的数据内容
     */
    private static Object getApplicationMeta(Context context, String key) {
        synchronized (context) {
            Object applicationMeta = null;

            try {
                ApplicationInfo applicationInfo = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(),
                                PackageManager.GET_META_DATA).applicationInfo;
                if (applicationInfo != null && applicationInfo.metaData != null) {
                    applicationMeta = applicationInfo.metaData.get(key);
                }
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            return applicationMeta;
        }
    }

    /**
     * 获取 AndroidManifest.xml 中指定键值所对应的 Boolean 类型的内容
     *
     * @param context 上下文
     * @param key     键值
     * @param def     如果指定键值没有对应的数据时，默认返回的值
     * @return 指定键值所对应的数据，如果数据存在，则返回该数据，否则，则返回用户指定的默认值
     */
    public static Boolean getBoolean(Context context, String key, Boolean def) {
        return get(context, key, def);
    }

    public static Integer getInt(Context context, String key, Integer def) {
        return get(context, key, def);
    }

    public static Long getLong(Context context, String key, Long def) {
        return get(context, key, def);
    }

    public static Float getFloat(Context context, String key, Float def) {
        return get(context, key, def);
    }

    public static Double getDouble(Context context, String key, Double def) {
        return get(context, key, def);
    }

    public static String getString(Context context, String key, String def) {
        return get(context, key, def);
    }

    @SuppressWarnings("unchecked")
    private static <T> T get(Context context, String key, T def) {
        synchronized (context) {
            if (def == null) {
                throw new NullPointerException("default value cannot be null!");
            }
            Object value = getApplicationMeta(context, key);
            if (value != null && value.getClass().getName().equals(def.getClass().getName())) {
                return (T) value;
            }
            return def;
        }
    }

}
