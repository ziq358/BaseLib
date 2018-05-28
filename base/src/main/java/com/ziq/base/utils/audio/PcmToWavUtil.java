package com.ziq.base.utils.audio;

import com.ziq.base.utils.ByteUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author john.
 * @since 2018/5/22.
 * Des:
 */

public class PcmToWavUtil {

    //wav  头信息，44字节
    public static byte[] converToWav(byte[] pcm, int sampleRate) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        swapStream.write("RIFF".getBytes());
        //不知道文件最后的大小，所以设置0
        swapStream.write(ByteUtils.integer2Bytes(Integer.reverseBytes(36 + pcm.length)));
        swapStream.write("WAVE".getBytes());
        swapStream.write("fmt ".getBytes());
        // Sub-chunk size,16 for PCM
        swapStream.write(ByteUtils.integer2Bytes(Integer.reverseBytes(16)));
        // AudioFormat, 1 为 PCM
        swapStream.write(ByteUtils.short2Bytes(Short.reverseBytes((short) 1)));
        // 数字为声道, 1 为 mono, 2 为 stereo
        swapStream.write(ByteUtils.short2Bytes(Short.reverseBytes((short) 1)));
        // 采样率
        swapStream.write(ByteUtils.integer2Bytes(Integer.reverseBytes(sampleRate)));
        // 采样率, SampleRate*NumberOfChannels*BitsPerSample/8
        swapStream.write(ByteUtils.integer2Bytes(Integer.reverseBytes(sampleRate * 16 / 8)));
        swapStream.write(ByteUtils.short2Bytes(Short.reverseBytes((short) (16 / 8))));
        swapStream.write(ByteUtils.short2Bytes(Short.reverseBytes((short) 16))); // Bits per sample
        swapStream.write("data".getBytes());
        swapStream.write(ByteUtils.integer2Bytes(Integer.reverseBytes(pcm.length)));
        swapStream.write(pcm);
        return swapStream.toByteArray();
    }
}
