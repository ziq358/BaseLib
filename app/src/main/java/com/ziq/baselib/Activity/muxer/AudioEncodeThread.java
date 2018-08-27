package com.ziq.baselib.Activity.muxer;

import android.util.Log;

import java.lang.ref.WeakReference;

public class AudioEncodeThread extends Thread {

    public static final String TAG = "Audio Encode";

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
        while(isRunning){

        }
        isRunning = false;
        Log.e(TAG, "音频编码 -- 结束");
//            try {
//
//                MediaExtractor audioExtractor = new MediaExtractor();
//                audioExtractor.setDataSource(mVideoPath);
//                int audioTrackIndex = -1;
//                MediaFormat mediaFormat = null;
//                MediaCodec audioCodec = null;
//                for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
//                    mediaFormat = audioExtractor.getTrackFormat(i);
//                    if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
//                        audioExtractor.selectTrack(i);
//                        audioTrackIndex = i;
//                        break;
//                    }
//                }
//
//                int minBufferSize = 0;
//                if (audioTrackIndex >= 0) {
//                    int audioChannels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
//                    int audioSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
//                    audioSampleRate = 48000;// 告白气球.MP4  的采样 有误
//                    minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
//                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO), AudioFormat.ENCODING_PCM_16BIT);
//                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioSampleRate,
//                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO), AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
//                    audioTrack.play();
//
//                    audioCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
//                    audioCodec.configure(mediaFormat, null, null, 0);
//
//                }
//
//                if (audioCodec == null) {
//                    Log.v(TAG, "audio decoder null");
//                    return;
//                }
//
//                audioCodec.start();
//
//                MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
//                ByteBuffer[] inputBuffers = audioCodec.getInputBuffers();
//                ByteBuffer[] outputBuffers = audioCodec.getOutputBuffers();
//                long startMs = System.currentTimeMillis();
//                while (!Thread.interrupted()) {
//                    //不断读数据 写进 缓存
//                    int inputBufferIndex = audioCodec.dequeueInputBuffer(10000);
//                    if (inputBufferIndex >= 0) {
//                        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                        inputBuffer.clear();
//                        int sampleSize = audioExtractor.readSampleData(inputBuffer, 0);
//                        if (sampleSize < 0) {
//                            //告诉 解码器 到达 文件 末尾
//                            audioCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                            Log.e(TAG, "到文件末尾");
//                        } else {
//                            Log.e(TAG, "写audio  数据进缓存 " + sampleSize);
//                            audioCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, audioExtractor.getSampleTime(), 0);
//                            audioExtractor.advance();
//                        }
//                    }
//
//                    //取 缓存
//                    int outputBufferIndex = audioCodec.dequeueOutputBuffer(audioBufferInfo, 10000);
//                    switch (outputBufferIndex) {
//                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
//                            Log.e(TAG, "取 缓存 format changed");
//                            break;
//                        case MediaCodec.INFO_TRY_AGAIN_LATER:
//                            Log.e(TAG, "取 缓存 超时");
//                            break;
//                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
//                            outputBuffers = audioCodec.getOutputBuffers();
//                            Log.e(TAG, "取 缓存 output buffers changed");
//                            break;
//                        default:
//                            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
//                            if (audioBufferInfo.size > 0) {
//                                byte[] mAudioOutTempBuf = new byte[audioBufferInfo.size];
//                                outputBuffer.position(0);
//                                outputBuffer.get(mAudioOutTempBuf, 0, audioBufferInfo.size);
//                                outputBuffer.clear();
//                                Log.e(TAG, "取 缓存 " + audioBufferInfo.presentationTimeUs + "    " + mAudioOutTempBuf.length);
//                                if (audioTrack != null)
//                                    audioTrack.write(mAudioOutTempBuf, 0, audioBufferInfo.size);
//                            }
//                            audioCodec.releaseOutputBuffer(outputBufferIndex, false);
//                            break;
//                    }
//
//                    if ((audioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                        Log.e(TAG, "取 缓存 buffer stream end  退出循环");
//                        break;
//                    }
//                }
//                audioCodec.stop();
//                audioCodec.release();
//                audioExtractor.release();
//                audioTrack.stop();
//                audioTrack.release();
//
//            } catch (Exception e) {
//                Log.e(TAG, "VideoEncodeThread --- " + e.getMessage());
//            }
    }
}