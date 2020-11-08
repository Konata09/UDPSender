package org.konata.udpsender.util;

import java.util.regex.Pattern;

public class Utils {
    public static String trimMACtoShow(String in) {
        if (in.isEmpty())
            return in;
        String s = in.replace(":", "").replace("-", "").toUpperCase();
        return s.substring(0, 2) + ":" + s.substring(2, 4) + ":" + s.substring(4, 6) + ":" + s.substring(6, 8) + ":" + s.substring(8, 10) + ":" + s.substring(10, 12);
    }

    public static String trimMACtoStor(String in) {
        return in.replace(":", "").replace("-", "").toUpperCase();
    }


    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
