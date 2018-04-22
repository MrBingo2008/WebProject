package com.berp.framework.util;

public class StrUtils {
	public static String getFixedLenString(String str, int len, String c) {
        if (str == null || str.length() == 0){
            str = "";
        }
        if (str.length() == len){
            return str;
        }
        if (str.length() > len){
            return str.substring(0,len);
        }
        StringBuilder sb = new StringBuilder(str);
        int needAppend = len - str.length();
        
        for(int i=0;i<needAppend;i++)
            sb.append(c);
 
        return sb.toString();
    }
}
