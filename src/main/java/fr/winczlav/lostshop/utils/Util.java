package fr.winczlav.lostshop.utils;

import java.util.Base64;

public class Util {

    public static String getSplitSold(int i){
        String s = String.valueOf(i);
        StringBuilder builder = new StringBuilder();
        char[] chars = s.toCharArray();
        int t = 0;
        for (int i1 = chars.length - 1; i1 >= 0; i1--) {
            if (t == 3){
                builder.append(" ");
                t = 0;
            }
            builder.append(chars[i1]);
            t++;
        }
        return reverseString(builder.toString());
    }

    private static String reverseString(String s){
        char[] chars = s.toCharArray();
        StringBuilder builder = new StringBuilder();
        for (int i = chars.length - 1; i >= 0; i--) {
            builder.append(chars[i]);
        }
        return builder.toString();
    }

    public static String encodeBase64(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    public static String decodeBase64(String s) {
        byte[] decodedBytes = Base64.getDecoder().decode(s);
        return new String(decodedBytes);
    }
}
