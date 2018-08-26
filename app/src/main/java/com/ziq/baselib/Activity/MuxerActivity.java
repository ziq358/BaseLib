package com.ziq.baselib.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.baselib.Constants;
import com.ziq.baselib.R;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class MuxerActivity extends BaseActivity implements SurfaceHolder.Callback , Camera.PreviewCallback {

    public static final String TAG = "Muxer";
    private static final long TIMEOUT_US = 10000;
    @Bind(R.id.action)
    Button mAction;
    @Bind(R.id.surface_view)
    SurfaceView mSurfaceView;

    Camera camera;
    SurfaceHolder surfaceHolder;


    String videoPath;

    private boolean isStarted;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muxer);
        ButterKnife.bind(this);
        videoPath = Constants.getDataDirPath(this, "muxer") + File.separator + "muxer.mp4";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show();
            // 申请 相机 麦克风权限
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        surfaceHolder = mSurfaceView.getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.e(TAG, "onPreviewFrame: "+data.length);
    }

    /**
     * 打开摄像头
     */
    private void startCamera() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        camera.setDisplayOrientation(90);
        Camera.Parameters parameters = camera.getParameters();
//        parameters.setPreviewFormat(ImageFormat.NV21);

        // 这个宽高的设置必须和后面编解码的设置一样，否则不能正常处理
        parameters.setPreviewSize(1920, 1080);

        try {
            camera.setParameters(parameters);
            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(this);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.action})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action:
                if (isStarted) {
                    isStarted = false;
                    mAction.setText("开始");
                    stop();
                } else {
                    isStarted = true;
                    mAction.setText("停止");
                    start();
                }
                break;
        }
    }

    private void start() {
        Toast.makeText(this, "开始", Toast.LENGTH_SHORT).show();


    }

    private void stop() {
        Toast.makeText(this, "停止", Toast.LENGTH_SHORT).show();
    }



    private class VideoThread extends Thread {
        @Override
        public void run() {


            MediaExtractor mediaExtractor = new MediaExtractor();
            try {
                mediaExtractor.setDataSource(videoPath);
                int videoTrackIndex = -1;
                MediaFormat mediaFormat = null;
                MediaCodec videoCodec = null;
                for (int i = 0; i < mediaExtractor.getTrackCount(); i++) {
                    mediaFormat = mediaExtractor.getTrackFormat(i);
                    if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("video/")) {
                        mediaExtractor.selectTrack(i);
                        videoTrackIndex = i;
                        break;
                    }
                }
                if (videoTrackIndex >= 0) {
                    videoCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
                    videoCodec.configure(mediaFormat, mSurfaceView.getHolder().getSurface(), null, 0);
                }
                if (videoCodec != null) {
                    videoCodec.start();
                    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                    long startMs = System.currentTimeMillis();
                    while (!Thread.interrupted()) {
                        //不断读数据 写进 缓存
                        ByteBuffer[] inputBuffers = videoCodec.getInputBuffers();
                        int inputBufferIndex = videoCodec.dequeueInputBuffer(10000);
                        if (inputBufferIndex >= 0) {
                            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                            int sampleSize = mediaExtractor.readSampleData(inputBuffer, 0);
                            if (sampleSize < 0) {
                                //告诉 解码器 到达 文件 末尾
                                videoCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                Log.e(TAG, "到文件末尾");
                            } else {
                                Log.e(TAG, "写video 数据进缓存 " + sampleSize);
                                videoCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, mediaExtractor.getSampleTime(), 0);
                                mediaExtractor.advance();
                            }
                        }
                        //取 缓存
                        int outputBufferIndex = videoCodec.dequeueOutputBuffer(videoBufferInfo, 10000);
                        switch (outputBufferIndex) {
                            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                                Log.e(TAG, "取 缓存 format changed");
                                break;
                            case MediaCodec.INFO_TRY_AGAIN_LATER:
                                Log.e(TAG, "取 缓存 超时");
                                break;
                            case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                                Log.e(TAG, "取 缓存 output buffers changed");
                                break;
                            default:
                                //渲染
                                Log.e(TAG, "渲染 " + videoBufferInfo.presentationTimeUs);
                                videoCodec.releaseOutputBuffer(outputBufferIndex, true);
                                break;
                        }

                        if ((videoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                            Log.e(TAG, "取 缓存 buffer stream end  退出循环");
                            break;
                        }

                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "VideoThread " + e.getMessage());
            }
        }
    }

    private class AudioThread extends Thread {
        private AudioTrack audioTrack;

        @Override
        public void run() {
            try {

                MediaExtractor audioExtractor = new MediaExtractor();
                audioExtractor.setDataSource(videoPath);
                int audioTrackIndex = -1;
                MediaFormat mediaFormat = null;
                MediaCodec audioCodec = null;
                for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
                    mediaFormat = audioExtractor.getTrackFormat(i);
                    if (mediaFormat.getString(MediaFormat.KEY_MIME).startsWith("audio/")) {
                        audioExtractor.selectTrack(i);
                        audioTrackIndex = i;
                        break;
                    }
                }

                int minBufferSize = 0;
                if (audioTrackIndex >= 0) {
                    int audioChannels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    int audioSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    audioSampleRate = 48000;// 告白气球.MP4  的采样 有误
                    minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO), AudioFormat.ENCODING_PCM_16BIT);
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, audioSampleRate,
                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO), AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
                    audioTrack.play();

                    audioCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
                    audioCodec.configure(mediaFormat, null, null, 0);

                }

                if (audioCodec == null) {
                    Log.v(TAG, "audio decoder null");
                    return;
                }

                audioCodec.start();

                MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
                ByteBuffer[] inputBuffers = audioCodec.getInputBuffers();
                ByteBuffer[] outputBuffers = audioCodec.getOutputBuffers();
                long startMs = System.currentTimeMillis();
                while (!Thread.interrupted()) {
                    //不断读数据 写进 缓存
                    int inputBufferIndex = audioCodec.dequeueInputBuffer(10000);
                    if (inputBufferIndex >= 0) {
                        ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                        inputBuffer.clear();
                        int sampleSize = audioExtractor.readSampleData(inputBuffer, 0);
                        if (sampleSize < 0) {
                            //告诉 解码器 到达 文件 末尾
                            audioCodec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            Log.e(TAG, "到文件末尾");
                        } else {
                            Log.e(TAG, "写audio  数据进缓存 " + sampleSize);
                            audioCodec.queueInputBuffer(inputBufferIndex, 0, sampleSize, audioExtractor.getSampleTime(), 0);
                            audioExtractor.advance();
                        }
                    }

                    //取 缓存
                    int outputBufferIndex = audioCodec.dequeueOutputBuffer(audioBufferInfo, 10000);
                    switch (outputBufferIndex) {
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            Log.e(TAG, "取 缓存 format changed");
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            Log.e(TAG, "取 缓存 超时");
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            outputBuffers = audioCodec.getOutputBuffers();
                            Log.e(TAG, "取 缓存 output buffers changed");
                            break;
                        default:
                            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                            if (audioBufferInfo.size > 0) {
                                byte[] mAudioOutTempBuf = new byte[audioBufferInfo.size];
                                outputBuffer.position(0);
                                outputBuffer.get(mAudioOutTempBuf, 0, audioBufferInfo.size);
                                outputBuffer.clear();
                                Log.e(TAG, "取 缓存 " + audioBufferInfo.presentationTimeUs + "    " + mAudioOutTempBuf.length);
                                if (audioTrack != null)
                                    audioTrack.write(mAudioOutTempBuf, 0, audioBufferInfo.size);
                            }
                            audioCodec.releaseOutputBuffer(outputBufferIndex, false);
                            break;
                    }

                    if ((audioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.e(TAG, "取 缓存 buffer stream end  退出循环");
                        break;
                    }
                }
                audioCodec.stop();
                audioCodec.release();
                audioExtractor.release();
                audioTrack.stop();
                audioTrack.release();

            } catch (Exception e) {
                Log.e(TAG, "VideoThread --- " + e.getMessage());
            }
        }
    }


}
