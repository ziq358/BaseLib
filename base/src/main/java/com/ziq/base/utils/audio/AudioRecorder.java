package com.ziq.base.utils.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.ziq.base.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;


/**
 * @author John
 * @since 2017/12/22
 * Des:
 */

public final class AudioRecorder {

    private static AudioRecorder sInstance;

    public enum State {
        INITIALIZING, READY, RECORDING, ERROR, STOPPED
    }

    private static final int ENCODING_PCM_16BIT = 16;
    private static final int ENCODING_PCM_8BIT = 8;
    private static final int BIT_8 = 8;

    // 录音状态
    private State state;

    public static final int[] SAMPLE_RATES = {22050, 11025, 44100, 8000};
    //音频源
    private int audioSource = MediaRecorder.AudioSource.MIC;
    //采样率
    private int sampleRate = SAMPLE_RATES[0];
    //声道设置
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private short channels = 2;
    //编码制式和采样大小
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSize;
    //周期的时间间隔
    private static final int DEFAULT_TIMER_INTERVAL = 120;
    private int timerInterval = DEFAULT_TIMER_INTERVAL;
    // 录音 通知周期
    private static final int MILLISECOND_UNIT = 1000;
    private int framePeriod = sampleRate * timerInterval / MILLISECOND_UNIT;

    //录音的开始时间
    private long startTime;
    //最大录音时长
    private static final long MAX_RECORD_DURATION = 45000L;

    // 当前的振幅
    private int cAmplitude = 0;

    // 输出的字节
    private byte[] buffer;
    private short samples;


    // 文件
    private RandomAccessFile randomAccessWriter;
    // 写入头文件的字节数
    private int payloadSize;
    private String filePath;

    private AudioRecord audioRecorder = null;

    /**
     * @return 返回实例
     */
    public static AudioRecorder getInstance() {
        if (sInstance == null) {
            sInstance = new AudioRecorder();
        }
        return sInstance;
    }

    private AudioRecorder() {
        try {
            init();
        } catch (Exception e) {
        }
    }

    private void init() throws Exception {
        for (int i = 0; i < SAMPLE_RATES.length; i++) {
            sampleRate = SAMPLE_RATES[i];
            try {
                cAmplitude = 0;
                filePath = null;
                if (audioFormat == AudioFormat.ENCODING_PCM_16BIT) {
                    samples = ENCODING_PCM_16BIT;
                } else {
                    samples = ENCODING_PCM_8BIT;
                }
                if (channelConfig == AudioFormat.CHANNEL_CONFIGURATION_MONO) {
                    channels = 1;
                } else {
                    channels = 2;
                }

                framePeriod = sampleRate * timerInterval / MILLISECOND_UNIT;
                bufferSize = framePeriod * 2 * samples * channels / BIT_8;
                if (bufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
                    //确保不小于MinBufferSize
                    bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                    // 相应地修改周期
                    framePeriod = bufferSize / (2 * samples * channels / BIT_8);
                    LogUtil.i(AudioRecorder.class.getName(), "buffer大小 " + Integer.toString(bufferSize));
                }
                audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize);
                if (audioRecorder.getState() != AudioRecord.STATE_INITIALIZED) {
                    throw new Exception();
                }
                audioRecorder.setRecordPositionUpdateListener(updateListener);
                audioRecorder.setPositionNotificationPeriod(framePeriod);

                state = State.INITIALIZING;
                LogUtil.i(AudioRecorder.class.getName(), "声音采样率：" + sampleRate);
                return;
            } catch (Exception e) {
                LogUtil.e(AudioRecorder.class.getName(), "录音初始化失败: 采样率 " + sampleRate);
                if (i == SAMPLE_RATES.length - 1) {
                    logError(e, "录音初始化失败");
                }
            }
        }
    }

    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() {
        @Override
        public void onMarkerReached(AudioRecord recorder) {

        }

        public void onPeriodicNotification(AudioRecord recorder) {
            audioRecorder.read(buffer, 0, buffer.length); // Fill buffer
            try {
                randomAccessWriter.write(buffer); // Write buffer to file
                payloadSize += buffer.length;
                if (samples == ENCODING_PCM_16BIT) {
                    for (int i = 0; i < buffer.length / 2; i++) { // 16bit sample size
                        short curSample = getShort(buffer[i * 2], buffer[i * 2 + 1]);
                        if (curSample > cAmplitude) { // Check amplitude
                            cAmplitude = curSample;
                        }
                    }
                } else { // 8bit sample size
                    for (int i = 0; i < buffer.length; i++) {
                        if (buffer[i] > cAmplitude) { // Check amplitude
                            cAmplitude = buffer[i];
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.e(AudioRecorder.class.getName(), e.getMessage() != null ? e.getMessage() : e.toString());
            }
        }
    };

    /**
     * 准备录音
     *
     * @return
     * @throws Exception
     */
    public void prepare() throws Exception {
        try {
            if (state == State.INITIALIZING) {
                if ((audioRecorder.getState() == AudioRecord.STATE_INITIALIZED) && (filePath != null)) {
                    // 写文件头
                    randomAccessWriter = new RandomAccessFile(filePath, "rw");
                    //设置文件长度为0，为了防止这个file以存在
                    randomAccessWriter.setLength(0);
                    randomAccessWriter.writeBytes("RIFF");
                    //不知道文件最后的大小，所以设置0
                    randomAccessWriter.writeInt(0);
                    randomAccessWriter.writeBytes("WAVE");
                    randomAccessWriter.writeBytes("fmt ");
                    // Sub-chunk size,16 for PCM
                    randomAccessWriter.writeInt(Integer.reverseBytes(16));
                    // AudioFormat, 1 为 PCM
                    randomAccessWriter.writeShort(Short.reverseBytes((short) 1));
                    // 数字为声道, 1 为 mono, 2 为 stereo
                    randomAccessWriter.writeShort(Short.reverseBytes(channels));
                    // 采样率
                    randomAccessWriter.writeInt(Integer.reverseBytes(sampleRate));
                    // 采样率, SampleRate*NumberOfChannels*BitsPerSample/8
                    randomAccessWriter.writeInt(Integer.reverseBytes(sampleRate * samples * channels / BIT_8));
                    randomAccessWriter.writeShort(Short.reverseBytes((short) (channels * samples / BIT_8)));
                    // Block
                    // align,
                    // NumberOfChannels*BitsPerSample/8
                    randomAccessWriter.writeShort(Short.reverseBytes(samples)); // Bits per sample
                    randomAccessWriter.writeBytes("data");
                    randomAccessWriter.writeInt(0); // Data chunk size not known yet, write 0
                    buffer = new byte[framePeriod * samples / BIT_8 * channels];
                    state = State.READY;
                } else {
                    reset();
                    if (filePath == null) {
                        logError(null, "输出文件没有设置");
                    } else {
                        logError(null, "录音初始化失败");
                    }

                }
            } else {
                reset();
                logError(null, "录音准备过程状态出错");
            }
        } catch (Exception e) {
            reset();
            logError(e, "录音准备过程出错");
        }
    }

    /**
     * 开始录音
     *
     * @return
     * @throws Exception
     */
    public void start() throws Exception {
        if (state == State.READY) {
            payloadSize = 0;
            audioRecorder.startRecording();
            //为了激活监听
            audioRecorder.read(buffer, 0, buffer.length);
            state = State.RECORDING;
            this.startTime = Calendar.getInstance().getTimeInMillis();
        } else {
            reset();
            logError(null, "录音开始过程状态出错");
        }
    }

    /**
     * 停止录音
     *
     * @return 录音时长， 毫秒数
     * @throws Exception
     */
    public long stop() throws Exception {
        long time = Calendar.getInstance().getTimeInMillis() - this.startTime;
        startTime = Calendar.getInstance().getTimeInMillis();
        if (state == State.RECORDING) {
            audioRecorder.stop();
            try {
                randomAccessWriter.seek(4); // Write size to RIFF header
                randomAccessWriter.writeInt(Integer.reverseBytes(36 + payloadSize));
                randomAccessWriter.seek(40); // Write size to Subchunk2Size field
                randomAccessWriter.writeInt(Integer.reverseBytes(payloadSize));
                randomAccessWriter.close();
            } catch (IOException e) {
                logError(e, "录音停止过程读写出错");
            }
            state = State.STOPPED;
            File file = new File(filePath);
            if (file.exists() && file.isFile()) {
                if (file.length() == 0L) {
                    file.delete();
                    return 0;
                } else {
                    return time;
                }
            } else {
                return 0;
            }
        } else {
            reset();
            logError(null, "录音停止过程状态出错");
            return 0;
        }
    }

    private void release() throws Exception {
        if (state == State.RECORDING) {
            stop();
        } else {
            if ((state == State.READY)) {
                try {
                    randomAccessWriter.close(); // 删除准备文件
                } catch (IOException e) {
                    logError(e, "录音释放过程出错");
                }
                (new File(filePath)).delete();
            }
        }
        if (audioRecorder != null) {
            audioRecorder.release();
        }
    }

    /**
     * 重置录音
     *
     * @return
     * @throws Exception
     */
    public void reset() throws Exception {
        try {
            release();
            init();
        } catch (Exception e) {
            logError(e, "录音重置过程出错");
        }
    }

    //提示音
    public static void startAlarm(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (notification == null) {
            return;
        }
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
    }


    /**
     * @throws Exception 异常
     */
    //录音时间太短，调用
    public void discardRecording() throws Exception {
        stop();
        File file = new File(filePath);
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    private short getShort(byte argB1, byte argB2) {
        return (short) (argB1 | (argB2 << BIT_8));
    }

    /**
     * @param argPath 文件路径
     * @throws Exception
     */
    public void setOutputFile(String argPath) throws Exception {
        if (state == State.INITIALIZING || state == State.STOPPED) {
            filePath = argPath;
            state = State.INITIALIZING;
        } else {
            reset();
            filePath = argPath;
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public State getState() {
        return state;
    }

    public int getAmplitude() {
        return cAmplitude;
    }

    private void logError(Exception e, String header) throws Exception {
        String message = "";
        if (e != null && e.getMessage() != null) {
            message = e.getMessage();
        }
        LogUtil.i(AudioRecorder.class.getName(), header + message);
        throw new Exception(header);
    }
}
