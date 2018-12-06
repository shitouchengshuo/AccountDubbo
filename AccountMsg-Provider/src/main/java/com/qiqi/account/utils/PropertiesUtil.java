package com.qiqi.account.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


public class PropertiesUtil {
	private static Properties ipProp;
	private static Properties jdbcProp;
	private static Properties template;
	private static Properties configProp;
	private static Properties appidProp;
	
	public static synchronized Properties getJdbcProp() {
		try {
			if(jdbcProp==null) {
				jdbcProp = new Properties();
				jdbcProp.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("jdbc.properties"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jdbcProp;
	}
	
	public static synchronized Properties getConfigProp() {
		try {
			if(configProp==null) {
				configProp = new Properties();
				configProp.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("config.properties"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return configProp;
	}
	
	public static Properties getIpProp() {
		try {
			synchronized(PropertiesUtil.class){
				if(ipProp==null) {
					ipProp = new Properties();
					ipProp.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("ip.properties"));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ipProp;
	}	
	
	public static Properties getTemplateProp() {
		try {
			synchronized(PropertiesUtil.class){
				if(template==null) {
					template = new Properties();
					InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream("messageTemplate.properties");
					BufferedReader bf = new BufferedReader(new  InputStreamReader(inputStream,"UTF-8"));
					//template.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("messageTemplate.properties"));
					template.load(bf);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return template;
	}

	
	
	public static void reload(){
		if(ipProp != null)
			ipProp = null;
	}
	public static synchronized String chinaToUnicode(String str){ 
		String result=""; 
		for (int i = 0; i < str.length(); i++){ 
		            int chr1 = (char) str.charAt(i); 
		            if(chr1>=19968&&chr1<=171941){//汉字范围 \u4e00-\u9fa5 (中文) 
		                result+="#x" + Integer.toHexString(chr1); 
		            }else{ 
		            result+=str.charAt(i); 
		            } 
		        } 
		return result; 
		} 
}
