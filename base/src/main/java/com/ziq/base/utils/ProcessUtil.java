package com.ziq.base.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.text.TextUtils;
import android.os.Process;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * @author john.
 * @since 2018/5/28.
 * Des:
 */

public class ProcessUtil {

    private static ActivityManager getActivityManager(Context context) {
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * 获取当前进程的名称
     *
     * @param context 位于该进程的 Context 实例
     * @return 当前进程的名称，如果没有找到，
     */
    public static String myProcessName(Context context) {
        int pid = Process.myPid();
        ActivityManager am = getActivityManager(context);
        String processName = "";
        for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        if (TextUtils.isEmpty(processName)) {
            processName = getProcessNameBy(pid);
        }
        return processName;
    }

    /**
     * 读取/proc/pid/cmdline（kernel）获取进程名称
     *
     * @param pid 进程ID
     * @return processName
     */
    public static String getProcessNameBy(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Exception e) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        return "";
    }


    /**
     * 根据包名获取对应进程消耗的内存
     *
     * @param context     上下文对象
     * @param packageName 包名
     * @return 进程实际消耗的内存
     */
    public static int getProcessMemorySize(Context context, String packageName) {
        //获得系统里正在运行的所有进程
        ActivityManager am = getActivityManager(context);
        List<ActivityManager.RunningAppProcessInfo> listTaskInfo = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : listTaskInfo) {
            if (runningAppProcessInfo != null && runningAppProcessInfo.processName.equals(packageName)) {

                int pid = runningAppProcessInfo.pid;

                int[] pids = new int[] {pid};
                Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(pids);
                // 占用的内存
                return memoryInfo[0].getTotalPss();
            }

        }
        return 0;
    }

}
