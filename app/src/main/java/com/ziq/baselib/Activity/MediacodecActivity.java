package com.ziq.baselib.Activity;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.LogUtil;
import com.ziq.base.utils.audio.AudioRecorder;
import com.ziq.base.utils.audio.AudioRecorderManager;
import com.ziq.base.utils.audio.PcmToWavUtil;
import com.ziq.baselib.Constants;
import com.ziq.baselib.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 *
 */

public class MediacodecActivity extends BaseActivity {

    public static final String TAG = "Mediacodec";

    @Bind(R.id.path)
    TextView pathTextView;
    @Bind(R.id.surface_view)
    SurfaceView mSurfaceView;

    String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediacodec);
        ButterKnife.bind(this);

        videoPath = Constants.getDataDirPath(this, "meidacodec") + File.separator + "gao_bai_qi_qiu.mp4";
        BufferedInputStream in = new BufferedInputStream(getResources().openRawResource(R.raw.gao_bai_qi_qiu));
        BufferedOutputStream out;
        try {
            FileOutputStream outputStream = new FileOutputStream(videoPath);
            out = new BufferedOutputStream(outputStream);
            byte[] buf = new byte[1024];
            int size = in.read(buf);
            while (size > 0) {
                out.write(buf, 0, size);
                size = in.read(buf);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pathTextView.setText(videoPath);
    }

    @OnClick({R.id.play})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                play();
                break;
        }
    }

    private void play(){
        new VideoThread().start();
    }

    private class VideoThread extends Thread{
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
                if(videoTrackIndex >= 0){
                    videoCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME));
                    videoCodec.configure(mediaFormat, mSurfaceView.getHolder().getSurface(), null, 0);
                }
                if (videoCodec != null) {
                    videoCodec.start();
                    MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
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
                                Log.e(TAG, "写数据进缓存 "+sampleSize);
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
                                //延时 -- 如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
//                                sleepRender(videoBufferInfo, startMs);
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
                Log.e(TAG, "VideoThread "+e.getMessage());
            }
        }
    }





    /*
    * 视频被播放主要分为以下步骤:
    1、将资源加载到extractor
    2、获取视频所在轨道
    3、设置extractor选中视频所在轨道
    4、创将解码视频的MediaCodec，decoder
    5、开始循环，直到视频资源的末尾
    6、将extractor中资源以一个单位填充进decoder的输入缓冲区
    7、decoder将解码之后的视频填充到输出缓冲区
    8、decoder释放输出缓冲区的同时，将缓冲区中数据渲染到surface
    9、音频的播放类似，只多了AudioTrack部分，少了渲染到surface部分。
    MediaCodec.releaseOutputBuffer(int outputBufferIndex, boolean render);
    render为true就会渲染到surface
    *
    * */

}
