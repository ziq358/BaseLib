package com.ziq.base.utils.encryption;

import org.jose4j.base64url.internal.apache.commons.codec.binary.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author john.
 * @since 2018/5/29.
 * Des:
 */

public class AesEncryptionUtil {

    private static final String TAG = "AesEncryptionUtil";

    //   算法/模式/填充   "AES/ECB/PKCS5Padding"
    public static final String CBC_MODE = "CBC";//加密块链
    public static final String CFB_MODE = "CFB";//加密反馈
    public static final String ECB_MODE = "ECB";//电子密码本
    public static final String OFB_MODE = "OFB";//输出反馈
    public static final String PADDING_NO = "NoPadding";
    public static final String PADDING_PKCS5 = "PKCS5Padding";
    public static final String PADDING_PKCS7 = "PKCS7Padding";//PKCS7Padding是缺几个字节就补几个字节的0，而PKCS5Padding是缺几个字节就补充几个字节的几，好比缺6个字节，就补充6个字节的6
    public static final String PADDING_ISO10126 = "ISO10126Padding";

    public static final String CDDE_BASE_64 = "Base64";
    public static final String CDDE_16 = "16";//16进制

    public static final int KEY_LENGTH_128 = 128;
    public static final int KEY_LENGTH_192 = 192;
    public static final int KEY_LENGTH_256 = 256;

    public static String encrypt(String content, int keyLength, String key, String keyOffset, String mode, String padding, String code) throws Exception {

//        if (key.length() * 8 != keyLength) {
//            throw new Exception("请检查密钥的长度," + keyLength + "/8 = " + keyLength / 8);
//        }

        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);
        byte[] byteContent = content.getBytes("utf-8");
        byte[] byteKeyOffset = new byte[]{};
        if (!mode.equalsIgnoreCase(ECB_MODE)) {
            byteKeyOffset = keyOffset.getBytes("utf-8");
        }
        IvParameterSpec zeroIv = new IvParameterSpec(byteKeyOffset);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key, keyLength), zeroIv);// 初始化为加密模式的密码器
        byte[] result = cipher.doFinal(byteContent);// 加密
        return Base64.encodeBase64String(result);//通过Base64转码返回
    }


    /**
     * 生成加密秘钥
     *
     * @return key
     */
    private static Key getSecretKey(final String key, int keyLength) throws Exception {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
//            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
//            secureRandom.setSeed(key.getBytes());
//            //AES 要求密钥长度为 128
//            kg.init(keyLength, secureRandom);
//            //生成一个密钥
//            SecretKey secretKey = kg.generateKey();
        //注意：：：：：：：
        //上面生成随机的一个key，，所以每次加密出来的结果都不一样

        return new SecretKeySpec(key.getBytes("utf-8"), "AES");// 转换为AES专用密钥
    }

}
