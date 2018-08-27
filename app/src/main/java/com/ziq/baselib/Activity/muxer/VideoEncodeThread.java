package com.ziq.baselib.Activity.muxer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Vector;

public class VideoEncodeThread extends Thread {

    public static final String TAG = "Video Encode";

    private static final String MIME_TYPE = "video/avc";
    private static final int FRAME_RATE = 25; // 帧率
    private static final int IFRAME_INTERVAL = 10; // I帧间隔（GOP）
    private static final int COMPRESS_RATIO = 256; // 压缩比
    private static final int TIME_OUT = 10000; // 编码超时时间
    private int BIT_RATE = 1080 * 1920 * 3 * 8 * FRAME_RATE / COMPRESS_RATIO; // 码率  每秒 多少字节 bit rate CameraWrapper.
    private MediaCodecInfo mCodecInfo;
    private MediaCodec mMediaCodec;
    private int mVideoWidth;
    private int mVideoHeight;
    private WeakReference<MuxerThread> mWeakMuxerThread;
    private Vector<byte[]> mFrameBytes = new Vector<byte[]>();

    private volatile boolean isRunning = false;

    public VideoEncodeThread(int mVideoWidth, int mVideoHeight, WeakReference<MuxerThread> mWeakMuxerThread) {
        this.mVideoWidth = mVideoWidth;
        this.mVideoHeight = mVideoHeight;
        this.mWeakMuxerThread = mWeakMuxerThread;
        BIT_RATE = mVideoHeight * mVideoWidth * 3 * 8 * FRAME_RATE / COMPRESS_RATIO;
    }

    private static void NV21toI420SemiPlanar(byte[] nv21bytes, byte[] i420bytes, int width, int height) {
        System.arraycopy(nv21bytes, 0, i420bytes, 0, width * height);
        for (int i = width * height; i < nv21bytes.length; i += 2) {
            i420bytes[i] = nv21bytes[i + 1];
            i420bytes[i + 1] = nv21bytes[i];
        }
    }

    public void startEncode() {
        isRunning = true;
        start();
    }

    public void stopEncode() {
        isRunning = false;
    }

    public void addData(byte[] data) {
        if (isRunning) {//避免 停止后， 还一直增加， oom
            mFrameBytes.add(data);//注意内存泄漏
            Log.e(TAG, "视频编码 -- 添加数据 -- 帧数 " + mFrameBytes.size());
        }
    }

    @Override
    public void run() {
        Log.e(TAG, "视频编码 -- 开始");
        try {
            mCodecInfo = MuxerThread.selectCodec(MIME_TYPE);
            if (mCodecInfo != null) {
                MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME_TYPE, this.mVideoWidth, this.mVideoHeight);
                mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
                mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
                mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
                mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);
                mMediaCodec = MediaCodec.createByCodecName(mCodecInfo.getName());
                mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                mMediaCodec.start();
                while (isRunning) {
                    if (mFrameBytes != null && !mFrameBytes.isEmpty()) {

                        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
                        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
                        //写数据
                        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIME_OUT);
                        if (inputBufferIndex >= 0) {
                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                            byte[] bytes = this.mFrameBytes.remove(0);
                            byte[] mFrameData = new byte[this.mVideoWidth * this.mVideoHeight * 3 / 2];
                            // 将原始的N21数据转为I420
                            NV21toI420SemiPlanar(bytes, mFrameData, this.mVideoWidth, this.mVideoHeight);
                            inputBuffer.clear();
                            inputBuffer.put(mFrameData);
                            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, mFrameData.length, System.nanoTime() / 1000, 0);
                        }
                        //读编码后数据
                        MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                        MediaFormat outputFormat;
                        MuxerThread muxerThread = mWeakMuxerThread.get();
                        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(videoBufferInfo, 10000);
                        switch (outputBufferIndex) {
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                Log.e(TAG, "video 取 缓存 超时");
                                break;
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                outputFormat = mMediaCodec.getOutputFormat();
                                if (muxerThread != null) {
                                    muxerThread.addMuxerTrackIndex(MuxerThread.TRACK_TYPE_VIDEO, outputFormat);
                                }
                                Log.e(TAG, "video 取 缓存 format changed");
                                break;
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                outputBuffers = mMediaCodec.getOutputBuffers();
                                Log.e(TAG, "video 取 缓存 output buffers changed");
                                break;
                            default:
                                if (outputBufferIndex < 0) {
                                        Log.e(TAG, "outputBufferIndex < 0");
                                } else {
                                    if (videoBufferInfo.size > 0) {
                                        outputFormat = mMediaCodec.getOutputFormat();
                                        if (muxerThread != null && muxerThread.isMuxerVideoTrackInit()) {
                                            muxerThread.addMuxerTrackIndex(MuxerThread.TRACK_TYPE_VIDEO, outputFormat);
                                        }
                                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];

                                        //貌似不需要
                                        //outputBuffer.position(videoBufferInfo.offset);
                                        //outputBuffer.limit(videoBufferInfo.offset + videoBufferInfo.size);

                                        if (muxerThread != null) {
                                            Log.e(TAG, "写数据到muxer: " + videoBufferInfo.size);
                                            muxerThread.addMuxerData(new MuxerThread.MuxerData(MuxerThread.TRACK_TYPE_VIDEO, outputBuffer, videoBufferInfo));
                                        }
                                    }
                                    mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                }
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "视频编码 -- 出错 " + e);
        }
        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
        }
        isRunning = false;
        Log.e(TAG, "视频编码 -- 结束");
    }
}