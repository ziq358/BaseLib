package com.ziq.baselib.Activity.muxer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class AudioEncodeThread extends Thread {

    public static final String TAG = "Audio Encode";
    private static final String MIME_TYPE = "audio/mp4a-latm";
    private static final int SAMPLE_RATE = 16000;
    private static final int BIT_RATE = 64000;
    private static final int TIME_OUT = 10000; // 编码超时时间
    private MediaCodecInfo mCodecInfo;
    private MediaCodec mMediaCodec;

    private WeakReference<MuxerThread> mWeakMuxerThread;
    private volatile boolean isRunning = false;

    public AudioEncodeThread(WeakReference<MuxerThread> mWeakMuxerThread) {
        this.mWeakMuxerThread = mWeakMuxerThread;
    }

    public void startEncode() {
        isRunning = true;
        start();
    }

    public void stopEncode() {
        isRunning = false;
    }

    @Override
    public void run() {
        Log.e(TAG, "音频编码 -- 开始");
        try {
            mCodecInfo = MuxerThread.selectCodec(MIME_TYPE);
            if(mCodecInfo != null){
                MediaFormat audioFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, 1);
                audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
                audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
                audioFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
                mMediaCodec = MediaCodec.createByCodecName(mCodecInfo.getName());
                mMediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                mMediaCodec.start();

                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
                AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                audioRecorder.startRecording();
                while(isRunning){
                    byte[] buffer = new byte[bufferSize];
                    int readSize = audioRecorder.read(buffer, 0, buffer.length);
                    Log.e(TAG, "音频编码 -- 读数据: " + readSize);
                    if(readSize > 0){
                        //写数据
                        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();
                        ByteBuffer[] outputBuffers = mMediaCodec.getOutputBuffers();
                        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(TIME_OUT);
                        if(inputBufferIndex >= 0){
                            ByteBuffer byteBuffer = inputBuffers[inputBufferIndex];
                            byteBuffer.clear();
                            byteBuffer.put(buffer);
                            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, readSize, System.nanoTime() / 1000, 0);
                        }
                        //读编码后数据
                        MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
                        MediaFormat outputFormat;
                        MuxerThread muxerThread = mWeakMuxerThread.get();
                        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(audioBufferInfo, TIME_OUT);
                        switch (outputBufferIndex) {
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                Log.e(TAG, "audio 取 缓存 超时");
                                break;
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                outputFormat = mMediaCodec.getOutputFormat();
                                if(muxerThread != null){
                                    muxerThread.addMuxerTrackIndex(MuxerThread.TRACK_TYPE_AUDIO, outputFormat);
                                }
                                Log.e(TAG, "audio 取 缓存 format changed");
                                break;
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                outputBuffers = mMediaCodec.getOutputBuffers();
                                Log.e(TAG, "audio 取 缓存 output buffers changed");
                                break;
                            default:
                                if (outputBufferIndex < 0) {
                                    Log.e(TAG, "outputBufferIndex < 0");
                                } else {

                                    //重要 ！！！！！
                                    if ((audioBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                                        Log.e(TAG, "ignoring BUFFER_FLAG_CODEC_CONFIG");
                                        audioBufferInfo.size = 0;
                                    }
                                    if (audioBufferInfo.size > 0) {
                                        outputFormat = mMediaCodec.getOutputFormat();
                                        if(muxerThread != null){
                                            muxerThread.addMuxerTrackIndex(MuxerThread.TRACK_TYPE_AUDIO, outputFormat);
                                        }
                                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                                        Log.e(TAG, "音频编码 -- 写数据到muxer: " + audioBufferInfo.size);
//                                        audioBufferInfo.presentationTimeUs = getPTSUs();//貌似不需要
                                        if(muxerThread != null && muxerThread.isMuxerStart()){
                                            muxerThread.addMuxerData(new MuxerThread.MuxerData(MuxerThread.TRACK_TYPE_AUDIO, outputBuffer, audioBufferInfo));
                                        }
//                                        prevOutputPTSUs = audioBufferInfo.presentationTimeUs;//貌似不需要
                                    }
                                    mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                }
                                break;
                        }
                    }
                }
                audioRecorder.stop();
                audioRecorder.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "音频编码 -- 出错 " + e);
        }

        if (mMediaCodec != null) {
            mMediaCodec.stop();
            mMediaCodec.release();
        }
        isRunning = false;
        Log.e(TAG, "音频编码 -- 结束");
    }


    private long prevOutputPTSUs = 0;
    /**
     * get next encoding presentationTimeUs
     *
     * @return
     */
    private long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}