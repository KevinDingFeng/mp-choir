package com.shenghesun.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class DateUtil {
	
	public static final String SECOND_FORMAT_STRING = "mm:ss:SS";
	public static final SimpleDateFormat SECOND_FORMAT = new SimpleDateFormat(SECOND_FORMAT_STRING);
	
	/**
	 * 秒转00:00:00
	 * @Title: formatSecond 
	 * @Description: TODO 
	 * @param second
	 * @return  String 
	 * @author yangzp
	 * @date 2018年8月22日下午2:07:52
	 **/ 
	public static String formatSecond(String second) {
		if(StringUtils.isEmpty(second)) {
			return "00:00:00";
		}
		//毫秒数  
		long ms = (long) (Float.parseFloat(second) * 1000);
		
		//初始化Formatter的转换格式。  
		SECOND_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
		String hms = SECOND_FORMAT.format(ms);
		
		return hms.substring(0, 8);
	}
	public static void main(String[] args) {
		System.out.println(formatSecond("10.23"));
	}
}
