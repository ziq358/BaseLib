package com.ziq.base.utils;

import android.util.Base64;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.keys.HmacKey;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class JWTEncodeUtil {

    public static String getJWTBySecretAndApiKey(String secretKey, String apiKey) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String claims = String.format("{\"iss\":\"%s\",\"exp\":%s,\"methods\":[\"post\", \"put\", \"delete\"]}", apiKey, String.valueOf(System.currentTimeMillis() + 120000));
            String content = String.format("%s.%s", Base64.encodeToString(header.getBytes("UTF-8"), Base64.NO_WRAP), Base64.encodeToString(claims.getBytes("UTF-8"), Base64.NO_WRAP));
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            return String.format("%s.%s", content, new String(Base64.encodeToString(sha256_HMAC.doFinal(content.getBytes("UTF-8")), Base64.NO_PADDING))).trim();
        } catch (NoSuchAlgorithmException e) {
        } catch (InvalidKeyException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public static String generateJWT(String jwtSecretKey, String issuer) {
        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        jsonWebSignature.setHeader("typ", "JWT");
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setIssuer(issuer);
        jwtClaims.setExpirationTimeMinutesInTheFuture(60f);
        jsonWebSignature.setPayload(jwtClaims.toJson());
        Key key = new HmacKey(jwtSecretKey.getBytes());
        jsonWebSignature.setKey(key);
        String jwt = null;
        try {
            jwt = jsonWebSignature.getCompactSerialization();
        } catch (Exception exception) {
        }
        return jwt;
    }

}
