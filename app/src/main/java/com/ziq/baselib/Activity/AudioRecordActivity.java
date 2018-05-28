package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.LogUtil;
import com.ziq.base.utils.audio.AudioRecorder;
import com.ziq.base.utils.audio.AudioRecorderManager;
import com.ziq.baselib.R;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class AudioRecordActivity extends BaseActivity implements View.OnClickListener, AudioRecorderManager.AudioDataCallback {
    @Bind(R.id.result)
    TextView result;

    AudioRecorderManager audioRecorderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        ButterKnife.bind(this);
        audioRecorderManager = new AudioRecorderManager(this);
        audioRecorderManager.setCallback(this);
    }

    @OnClick({R.id.start, R.id.stop})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                AudioRecorder.startAlarm(this);
                audioRecorderManager.startRecord(this);
                break;
            case R.id.stop:
                AudioRecorder.startAlarm(this);
                audioRecorderManager.stopRecord();
                break;
        }
    }

    @Override
    public void onRecordCompleted(byte[] data) {
        String filePath = getDataDirPath() + File.separator + Calendar.getInstance().getTimeInMillis() + ".wav";
        try {
            RandomAccessFile randomAccessWriter = new RandomAccessFile(filePath, "rw");
            randomAccessWriter.write(data);
            randomAccessWriter.close();
            result.setText(filePath);
        } catch (Exception e) {
            LogUtil.e("AudioRecordActivity", e.getMessage());
        }
    }

    public String getDataDirPath() {
        String path = this.getApplicationContext().getFilesDir().getAbsolutePath();
        // TODO: 2018/5/11 方便查看 文件先存在SD卡
        if (FileUtil.isExistSDCard()) {
            path = this.getExternalCacheDir().getAbsolutePath() + File.separator + "Record";
        }
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        return path;
    }
}
