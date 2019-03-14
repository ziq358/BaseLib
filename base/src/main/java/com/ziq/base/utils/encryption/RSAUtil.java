package com.ziq.base.utils.encryption;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {

	public static String encrypt( String str, String publicKey ) throws Exception{
		//base64编码的公钥
		byte[] decoded = Base64.decode(publicKey.getBytes("utf-8"), Base64.DEFAULT);
		RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
		//RSA加密
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		String outStr = Base64.encodeToString(cipher.doFinal(str.getBytes("utf-8")), Base64.NO_WRAP);
		return outStr;
	}

}
