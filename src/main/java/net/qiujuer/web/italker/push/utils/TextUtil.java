package net.qiujuer.web.italker.push.utils;

import net.qiujuer.web.italker.push.provider.GsonProvider;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Base64;
public class TextUtil {
    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    public static String encodeBase64(String str) {
        return Base64
                .getEncoder()
                .encodeToString(str.getBytes());
    }

    public static String toJson(Object obj) {
        return GsonProvider.getGson().toJson(obj);
    }
}

