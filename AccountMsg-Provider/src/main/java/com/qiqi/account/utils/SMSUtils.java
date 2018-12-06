package com.qiqi.account.utils;

import java.util.Properties;

public class SMSUtils {

	public static String account() {
		Properties prop = PropertiesUtil.getIpProp();
		String account = prop.getProperty("account");
		return account;
	}

	public static String pswd() {
		Properties prop = PropertiesUtil.getIpProp();
		String pswd = prop.getProperty("pswd");
		return pswd;
	}

	public static Boolean ifLimitTimes() {
		Properties prop = PropertiesUtil.getConfigProp();
		String ifLimitTimes = prop.getProperty("ifLimitTimes");
		if (ifLimitTimes.equals("true")) {
			return true;
		} else
			return false;
	}

	public static int getMaxLimits() {
		Properties prop = PropertiesUtil.getConfigProp();
		int maxLimitTimes = Integer.parseInt(prop.getProperty("maxLimitTimes"));
		return maxLimitTimes;
	}

	/**
	 * 从配置文件获取发送短信服务需要的创蓝账号及密码
	 * @param mobile
	 * @param msg
	 * @param needstatus
	 * @return
	 */
	public static String sendSingalUrl(String mobile, String msg, Boolean needstatus) {

		String singalSendUrl = PropertiesUtil.getConfigProp().getProperty("singalSendUrl");
		String accountKey = PropertiesUtil.getConfigProp().getProperty("account");
		String pswdKey = PropertiesUtil.getConfigProp().getProperty("pswd");
		String mobileKey = PropertiesUtil.getConfigProp().getProperty("mobile");
		String msgKey = PropertiesUtil.getConfigProp().getProperty("msg");
		String needstatusKey = PropertiesUtil.getConfigProp().getProperty("needstatus");
		String url = singalSendUrl + "?" + accountKey + "=" + account() + "&" + pswdKey + "=" + pswd() + "&" + mobileKey
				+ "=" + mobile + "&" + msgKey + "=" + msg + "&" + needstatusKey + "=" + needstatus;
		return url;
	}


	public static String batchSendUrl(String mobile, String msg, Boolean needstatus) {

		String singalSendUrl = PropertiesUtil.getConfigProp().getProperty("batchSendUrl");
		String accountKey = PropertiesUtil.getConfigProp().getProperty("account");
		String pswdKey = PropertiesUtil.getConfigProp().getProperty("pswd");
		String mobileKey = PropertiesUtil.getConfigProp().getProperty("mobile");
		String msgKey = PropertiesUtil.getConfigProp().getProperty("msg");
		String needstatusKey = PropertiesUtil.getConfigProp().getProperty("needstatus");
		String url = singalSendUrl + "?" + accountKey + "=" + account() + "&" + pswdKey + "=" + pswd() + "&" + mobileKey
				+ "=" + mobile + "&" + msgKey + "=" + msg + "&" + needstatusKey + "=" + needstatus;
		return url;
	}
	
	public static String quotaUrl(String account, String pswd) {

		String singalSendUrl = PropertiesUtil.getConfigProp().getProperty("quotaUrl");
		String accountKey = PropertiesUtil.getConfigProp().getProperty("account");
		String pswdKey = PropertiesUtil.getConfigProp().getProperty("pswd");
		String url = singalSendUrl + "?" + accountKey + "=" + account + "&" + pswdKey + "=" + pswd;
		return url;
	}


	//创蓝新版本查询短信余额接口
	public static String newQuotaUrl(String account, String pswd) {
		String newQuotaUrl = PropertiesUtil.getConfigProp().getProperty("newQuotaUrl");
		String url = newQuotaUrl + "?un=" + account + "&pw=" + pswd;
		return url;
	}


	//创蓝新版本查询国际短信余额
	public static String newInternationalQuotaUrl(String account, String pswd) {
		String newInternationalQuotaUrl = PropertiesUtil.getConfigProp().getProperty("quotaInternationalUrl");
		String url = newInternationalQuotaUrl + "?un=" + account + "&pw=" + pswd;
		return url;
	}

}
