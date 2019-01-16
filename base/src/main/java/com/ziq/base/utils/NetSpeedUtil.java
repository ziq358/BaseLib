package com.ziq.base.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;

/**
 * @author wuyanqiang
 * 2019/1/15
 */
public class NetSpeedUtil {
    private static final String LOG_TAG = "NetSpeedUtil";
    private static final int UNSUPPORTED = -1;
    private Context mContext;
    private long preDownLoadRxBytes = 0;
    private long preUpLoadRxBytes = 0;

    public NetSpeedUtil(Context context) {
        mContext = context;
    }

    /**
     * 获取当前应用uid
     */
    public int getUid() {
        try {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(), PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 获取下载间隔的流量，kb 小数点保留一位
     */
    public double getIntervalToTalRxKB() {
        long curBytes = getNetworkRxBytes();
        if(preDownLoadRxBytes == 0){
            preDownLoadRxBytes = curBytes;
        }
        long bytes = curBytes - preDownLoadRxBytes;
        preDownLoadRxBytes = curBytes;
        double kb = (double)bytes / (double)1024;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取上传间隔的流量，kb 小数点保留一位
     */
    public double getIntervalToTalTxKB() {
        long curBytes = getNetworkTxBytes();
        if(preUpLoadRxBytes == 0){
            preUpLoadRxBytes = curBytes;
        }
        long bytes = curBytes - preUpLoadRxBytes;
        preUpLoadRxBytes = curBytes;
        double kb = (double)bytes / (double)1024;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取下载间隔的流量，kb 小数点保留一位
     */
    public double getIntervalRxKB(int uid) {
        long curBytes = getRcvTraffic(uid);
        if(preDownLoadRxBytes == 0){
            preDownLoadRxBytes = curBytes;
        }
        long bytes = curBytes - preDownLoadRxBytes;
        preDownLoadRxBytes = curBytes;
        double kb = (double)bytes / (double)1024;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 获取上传间隔的流量，kb 小数点保留一位
     */
    public double getIntervalTxKB(int uid) {
        long curBytes = getSndTraffic(uid);
        if(preUpLoadRxBytes == 0){
            preUpLoadRxBytes = curBytes;
        }
        long bytes = curBytes - preUpLoadRxBytes;
        preUpLoadRxBytes = curBytes;
        double kb = (double)bytes / (double)1024;
        BigDecimal bd = new BigDecimal(kb);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }




    /**
     * 获取当前下载流量总和
     */
    private long getNetworkRxBytes() {
        return TrafficStats.getTotalRxBytes();
    }

    /**
     * 获取当前上传流量总和
     */
    private long getNetworkTxBytes() {
        return TrafficStats.getTotalTxBytes();
    }


    /**
     * 获取下载流量 某个应用的网络流量数据保存在系统的/proc/uid_stat/$UID/tcp_rcv | tcp_snd文件中
     */
    private long getRcvTraffic(int uid) {
        long rcvTraffic = UNSUPPORTED; // 下载流量
        rcvTraffic = TrafficStats.getUidRxBytes(uid);
        Log.e(LOG_TAG, "getUidRxBytes = "+ rcvTraffic );
        if (rcvTraffic == UNSUPPORTED) { // 不支持的查询
            return UNSUPPORTED;
        }
        RandomAccessFile rafRcv = null, rafSnd = null; // 用于访问数据记录文件
        String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
        try {
            rafRcv = new RandomAccessFile(rcvPath, "r");
            rcvTraffic = Long.parseLong(rafRcv.readLine()); // 读取流量统计
            Log.e(LOG_TAG, " File UidRxBytes = "+ rcvTraffic );
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rafRcv != null)
                    rafRcv.close();
                if (rafSnd != null)
                    rafSnd.close();
            } catch (IOException e) {
                Log.w(LOG_TAG, "Close RandomAccessFile exception: " + e.getMessage());
            }
        }
        return rcvTraffic;
    }

    /**
     * 获取上传流量
     */
    private long getSndTraffic(int uid) {
        long sndTraffic = UNSUPPORTED; // 上传流量
        sndTraffic = TrafficStats.getUidTxBytes(uid);
        Log.e(LOG_TAG, "getUidTxBytes = "+ sndTraffic );
        if (sndTraffic == UNSUPPORTED) { // 不支持的查询
            return UNSUPPORTED;
        }
        RandomAccessFile rafRcv = null, rafSnd = null; // 用于访问数据记录文件
        String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
        try {
            rafSnd = new RandomAccessFile(sndPath, "r");
            sndTraffic = Long.parseLong(rafSnd.readLine());
            Log.e(LOG_TAG, "File UidTxBytes = "+ sndTraffic );
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, "IOException: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rafRcv != null)
                    rafRcv.close();
                if (rafSnd != null)
                    rafSnd.close();
            } catch (IOException e) {
                Log.w(LOG_TAG, "Close RandomAccessFile exception: " + e.getMessage());
            }
        }
        return sndTraffic;
    }

}
