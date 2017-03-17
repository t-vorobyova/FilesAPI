package com.restfileapi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Tatyana on 15.03.2017.
 */
public class MessageDigestSHA256 {
    public static String hash256(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        InputStream is = new FileInputStream(file);
        DigestInputStream dis = new DigestInputStream(is, md);
        while(dis.read()!=-1);
        dis.close();
        byte[] digest = md.digest();
        return bytesToHex(digest);
    }

    public static String hash256(String data) throws NoSuchAlgorithmException{
        MessageDigest md= MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
