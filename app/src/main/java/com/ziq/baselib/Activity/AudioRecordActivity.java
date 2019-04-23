package com.ziq.baselib.Activity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.utils.LogUtil;
import com.ziq.base.utils.audio.AudioRecorder;
import com.ziq.base.utils.audio.AudioRecorderManager;
import com.ziq.base.utils.audio.PcmToWavUtil;
import com.ziq.baselib.Constants;
import com.ziq.baselib.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class AudioRecordActivity extends MvpBaseActivity implements View.OnClickListener, AudioRecorderManager.AudioDataCallback {
    @BindView(R.id.result)
    TextView result;

    AudioRecorderManager audioRecorderManager;
    String pcmFilePath;
    String wavFilePath;
    boolean isPcmPlay;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_audio_record;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        audioRecorderManager = new AudioRecorderManager(this);
        audioRecorderManager.setCallback(this);
    }

    @OnClick({R.id.start, R.id.stop, R.id.playPcm, R.id.playWav})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                result.setText("开始");
                AudioRecorder.startAlarm(this);
                audioRecorderManager.startRecord(this);
                break;
            case R.id.stop:
                AudioRecorder.startAlarm(this);
                audioRecorderManager.stopRecord();
                break;
            case R.id.playPcm:
                playPcm(pcmFilePath);
                break;
            case R.id.playWav:
                playWav(wavFilePath);
                break;
        }
    }

    @Override
    public void onRecordCompleted(byte[] data) throws IOException {
        byte[] bytes = PcmToWavUtil.converToWav(data, 16000);// wav 数据
        pcmFilePath = getDataDirPath() + File.separator + Calendar.getInstance().getTimeInMillis() + ".pcm";
        wavFilePath = getDataDirPath() + File.separator + Calendar.getInstance().getTimeInMillis() + ".wav";
        try {
            RandomAccessFile randomAccessWriter = new RandomAccessFile(pcmFilePath, "rw");
            randomAccessWriter.write(data);
            randomAccessWriter.close();

            randomAccessWriter = new RandomAccessFile(wavFilePath, "rw");
            randomAccessWriter.write(bytes);
            randomAccessWriter.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.setText(wavFilePath + "\n" + pcmFilePath);
                }
            });
        } catch (Exception e) {
            LogUtil.e("AudioRecordActivity", e.getMessage());
        }
    }

    @Override
    public void onRead(byte[] fcmData) throws IOException {

    }

    public String getDataDirPath() {
        String path = Constants.getDataDirPath(this, "Record");
        return path;
    }

    private void playPcm(String fcmPath) {
        if (isPcmPlay) {
            Toast.makeText(this, "播放中", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(fcmPath)) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {
                        isPcmPlay = true;
                        int bufferSize = AudioTrack.getMinBufferSize(16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 16000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
                        audioTrack.play();
                        byte[] buffer = new byte[bufferSize];
                        FileInputStream fileInputStream = new FileInputStream(pcmFilePath);
                        int length;
                        while ((length = fileInputStream.read(buffer)) > 0) {
//                            audioTrack.write(buffer, 0, length);
                            audioTrack.write(buffer, 0, buffer.length);
                        }
                        fileInputStream.close();
                        audioTrack.stop();
                        audioTrack.release();
                        isPcmPlay = false;
                    } catch (Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isPcmPlay = false;
                                Toast.makeText(AudioRecordActivity.this, "出错结束", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();
        }
    }

    private void playWav(String wavPath) {
        if (TextUtils.isEmpty(wavPath)) {
            Toast.makeText(this, "文件不存在", Toast.LENGTH_LONG).show();
        } else {

        }
    }
}
