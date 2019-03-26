package com.ziq.base.utils.audio;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import androidx.core.content.ContextCompat;
import android.widget.Toast;

import com.ziq.base.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.Calendar;

/**
 * @author john.
 * @since 2018/5/22.
 * Des:
 */

public class AudioRecorderManager {

    public static final String TAG = "AudioRecorderManager";

    private RecordTask mRecordTask;

    private AudioRecord audioRecorder;
    private int bufferSize = 0;

    private AudioDataCallback mAudioDataCallback;

    public AudioRecorderManager(Context context) {
        bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "需要获取录音权限", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, "录音初始化失败", Toast.LENGTH_SHORT).show();
        }
    }

    public interface AudioDataCallback {
        void onRecordCompleted(byte[] fcmData) throws IOException;
        void onRead(byte[] fcmData) throws IOException;
    }

    public void setCallback(AudioDataCallback audioDataCallback) {
        mAudioDataCallback = audioDataCallback;
    }

    public void startRecord(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "需要获取录音权限", Toast.LENGTH_SHORT).show();
        } else {
            mRecordTask = new RecordTask(mAudioDataCallback, audioRecorder, bufferSize);
            new Thread(mRecordTask).start();
        }
    }

    public void stopRecord() {
        if (mRecordTask != null) {
            mRecordTask.stopRecord();
        }
    }

    public void discardRecording() {
        mRecordTask.discardRecording();
    }

    public void release() {
        try {
            if (audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecorder.stop();
            }
            audioRecorder.release();
            audioRecorder = null;
        } catch (Exception e) {
            LogUtil.e(TAG, "AudioRecorder 释放错误: " + e);
        }
    }


    /**
     * @author john.
     * @since 2018/5/22.
     * Des:
     */
    public static class RecordTask implements Runnable {

        private boolean isRecording;
        private boolean isDiscardRecording;
        private SoftReference<AudioRecord> mAudioRecorder;
        private byte[] mBuffer;
        private SoftReference<AudioDataCallback> mAudioDataCallback;

        RecordTask(AudioDataCallback audioDataCallback, AudioRecord audioRecorder, int bufferSize) {
            mAudioDataCallback = new SoftReference<AudioDataCallback>(audioDataCallback);
            mAudioRecorder = new SoftReference<AudioRecord>(audioRecorder);
            mBuffer = new byte[bufferSize];
        }

        void stopRecord() {
            isRecording = false;
            isDiscardRecording = false;
        }

        void discardRecording() {
            isRecording = false;
            isDiscardRecording = true;
        }

        @Override
        public void run() {
            isRecording = true;
            LogUtil.i(TAG, "开始录音: ");
            long startTime = Calendar.getInstance().getTimeInMillis();
            try {
                mAudioRecorder.get().startRecording();
            } catch (Exception e) {
                LogUtil.e(TAG, "开始录音出错: " + e);
            }

            long bufferLength = 0;
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            while (isRecording) {
                try {
                    int readSize = mAudioRecorder.get().read(mBuffer, 0, mBuffer.length); // Fill buffer
                    if (AudioRecord.ERROR_INVALID_OPERATION == readSize
                            || AudioRecord.ERROR_BAD_VALUE == readSize
                            || AudioRecord.ERROR_DEAD_OBJECT == readSize) {
                        LogUtil.e(TAG, "读取音频数据失败, errorCode = " + readSize);
                    }
                    if (mAudioDataCallback.get() != null) {
                        mAudioDataCallback.get().onRead(mBuffer);
                    }
                    swapStream.write(mBuffer);
                } catch (Exception e) {
                    LogUtil.e(TAG, "写入录音数据出错: " + e);
                }
            }

            try {
                //录制结束
                LogUtil.i(TAG, "录音结束: ");
                mAudioRecorder.get().stop();
                if (mAudioDataCallback.get() != null) {
                    mAudioDataCallback.get().onRecordCompleted(swapStream.toByteArray());
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "录制结束出错: " + e);
            }
        }
    }
}
