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
	
	public static Integer [] getIntegersFromString(String str){
		Integer [] ids = null;
		if(str != null && !str.equals(""))
		{
			String [] objectNames = str.split(",");
			ids = new Integer[objectNames.length];
			for(int i=0;i<objectNames.length;i++){
				ids[i] = Integer.parseInt(objectNames[i]);
			}
		}
		return ids;
	}
	
	public static Double [] getDoublesFromString(String str){
		Double [] ids = null;
		if(str != null && !str.equals(""))
		{
			String [] objectNames = str.split(",");
			ids = new Double[objectNames.length];
			for(int i=0;i<objectNames.length;i++){
				ids[i] = Double.parseDouble(objectNames[i]);
			}
		}
		return ids;
	}
	
	public static String getStringFromIntegers(Integer [] ins){
		StringBuilder result = new StringBuilder();
		for(Integer in:ins){
			result.append(in);
			result.append(',');
		}
		if(result.length()>=1)
			result.deleteCharAt(result.length()-1);
		
		return result.toString();
	}
	
	public static String doubleTrans(double d){
		if(Math.round(d)-d==0){
			return String.valueOf((long)d);
		}
		return String.valueOf(d);
	}
	
}
