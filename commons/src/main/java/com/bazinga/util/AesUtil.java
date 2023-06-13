package com.bazinga.util;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {

    private final static String encoding = "UTF-8";



    public static String encrypt(String content, String aesKey) {
        try {
            SecretKeySpec skeySpec = getKey(aesKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes(encoding));
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(content.getBytes(encoding));

            return Base64Utils.encodeToString(encrypted);
        } catch (Exception e) {
            throw new IllegalArgumentException("加密参数异常，content="+content, e);
        }
    }



    /**
     * 加密
     * @param content
     * @param aesKey
     * @return
     */
    public static byte[] encrypt(byte[] content, String aesKey) {
        try {
            SecretKeySpec skeySpec = getKey(aesKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes(encoding));
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new IllegalArgumentException("加密参数异常，content="+content, e);
        }
    }



    /**
     * 解密
     * @param content
     * @param aesKey
     * @return
     */
    public static String decrypt(String content, String aesKey) {

        try {
            SecretKeySpec skeySpec = getKey(aesKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes(encoding));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] encrypted1 = Base64Utils.decodeFromString(content);

            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, encoding);
            return originalString;
        } catch (Exception e) {
            throw new IllegalArgumentException("解密参数异常，content="+content, e);
        }
    }



    /**
     * 解密
     * @param content
     * @param aesKey
     * @return
     */
    public static byte[] decrypt(byte[] content, String aesKey) {
        try {
            SecretKeySpec skeySpec = getKey(aesKey);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(aesKey.getBytes(encoding));
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            return cipher.doFinal(content);
        } catch (Exception e) {
            throw new IllegalArgumentException("解密参数异常，content="+content, e);
        }
    }

    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes();
        byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");

        return skeySpec;
    }


    public static void main(String args[]) throws Exception {
        String aesKey = "singular20220724";
        String result = encrypt("147258", aesKey);
        System.out.println(result);
        //ZKxwfzWkcJR6QAnSiUIPyT2ZdRnp0ttX45VJgeTP3CJ+l29ucpktN1oRwc9GuNeT
        System.out.println(decrypt(result, aesKey));
    }
}
