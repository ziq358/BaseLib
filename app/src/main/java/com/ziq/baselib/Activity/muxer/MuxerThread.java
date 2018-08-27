package com.ziq.baselib.Activity.muxer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Created by jj on 2018/8/26.
 */

public class MuxerThread extends Thread {

    public static final String TAG = "Muxer Thread";

    private String mFilePath;
    private int mVideoWidth;
    private int mVideoHeight;

    private MediaMuxer mMediaMuxer;
    public static final int TRACK_TYPE_VIDEO = 0;
    public static final int TRACK_TYPE_AUDIO = 1;
    private int mVideoTrackIndex = -1;
    private int mAudioTrackIndex = -1;

    private VideoEncodeThread mVideoEncodeThread;
    private AudioEncodeThread mAudioEncodeThread;

    private volatile boolean isRunning = false;

    public MuxerThread(String filePath, int width, int height) {
        this.mFilePath = filePath;
        this.mVideoWidth = width;
        this.mVideoHeight = height;
    }

    // 开始音视频混合任务
    public void startMuxer() {
        isRunning = true;
        start();
    }

    // 停止音视频混合任务
    public void stopMuxer() {
        isRunning = false;
    }


    // 添加视频帧数据
    public void addVideoFrameData(byte[] data) {
        if(mVideoEncodeThread != null){
            mVideoEncodeThread.addData(data);
        }
    }

    public synchronized void addMuxerTrackIndex(int index, MediaFormat mediaFormat){
        if(isMuxerVideoTrackInit() && isMuxerAudioTrackInit()){
            return;
        }
        if(mMediaMuxer != null){
            /* 如果已经添加了，就不做处理了 */
            if(TRACK_TYPE_VIDEO == index && mVideoTrackIndex == -1){
                mVideoTrackIndex = mMediaMuxer.addTrack(mediaFormat);
            }
            /* 如果已经添加了，就不做处理了 */
            if(TRACK_TYPE_AUDIO == index && mAudioTrackIndex == -1){
                mAudioTrackIndex = mMediaMuxer.addTrack(mediaFormat);
            }

            if(isMuxerVideoTrackInit() && isMuxerAudioTrackInit()){
                Log.e(TAG, "addMuxerTrackIndex: mMediaMuxer.start()");
                mMediaMuxer.start();//需要添加  轨道 后 开始
            }
        }
    }

    public boolean isMuxerVideoTrackInit(){
        return mVideoTrackIndex != -1;
    }

    public boolean isMuxerAudioTrackInit(){
        // TODO: 2018/8/27  test
        return true;
//        return mAudioTrackIndex != -1;
    }

    public static MediaCodecInfo selectCodec(String mimeType) {
        int numCodecs = MediaCodecList.getCodecCount();
        for (int i = 0; i < numCodecs; i++) {
            MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++) {
                if (types[j].equalsIgnoreCase(mimeType)) {
                    return codecInfo;
                }
            }
        }
        return null;
    }


    /**
     * 封装需要传输的数据类型
     */
    public static class MuxerData {

        int trackIndex;
        ByteBuffer byteBuf;
        MediaCodec.BufferInfo bufferInfo;

        public MuxerData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
            this.trackIndex = trackIndex;
            this.byteBuf = byteBuf;
            this.bufferInfo = bufferInfo;
        }
    }

    public void addMuxerData(MuxerData muxerData){
        if(isRunning && mMediaMuxer != null){
            mMuxerDatas.add(muxerData);
        }
    }

    private Vector<MuxerData> mMuxerDatas = new Vector<MuxerData>();

    @Override
    public void run() {
        Log.e(TAG, "音视频混合 -- 开始");
        try {
            mMediaMuxer = new MediaMuxer(mFilePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mVideoEncodeThread = new VideoEncodeThread(mVideoWidth, mVideoHeight, new WeakReference<MuxerThread>(this));
            mAudioEncodeThread = new AudioEncodeThread(new WeakReference<MuxerThread>(this));
            mVideoEncodeThread.startEncode();
            mAudioEncodeThread.startEncode();
            while (isRunning){
                if (mMuxerDatas != null && !mMuxerDatas.isEmpty()) {
                    MuxerData muxerData = mMuxerDatas.remove(0);
                    int track = -1;
                    if(TRACK_TYPE_VIDEO == muxerData.trackIndex){
                        track = mVideoTrackIndex;
                    }else if(TRACK_TYPE_AUDIO == muxerData.trackIndex){
                        track = mAudioTrackIndex;
                    }
                    if(track != -1){
                        mMediaMuxer.writeSampleData(track, muxerData.byteBuf, muxerData.bufferInfo);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "音视频混合 -- 出错 "+e);
        }

        if(mVideoEncodeThread != null){
            mVideoEncodeThread.stopEncode();
        }
        if(mAudioEncodeThread != null){
            mAudioEncodeThread.stopEncode();
        }
        mMediaMuxer.stop();
        mMediaMuxer.release();
        isRunning = false;
        Log.e(TAG, "音视频混合 -- 结束");
    }
}
