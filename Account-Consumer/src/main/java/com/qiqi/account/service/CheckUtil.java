package com.qiqi.account.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiqi.account.dao.AccountMapper;
import com.qiqi.account.dao.AccountVerifyCodeMapper;
import com.qiqi.account.exception.*;
import com.qiqi.account.model.TbAccount;
import com.qiqi.account.model.TbAccountVerifyCode;
import com.qiqi.account.model.dto.AccountData;
import com.qiqi.account.utils.TimeUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import java.text.ParseException;
import java.util.Iterator;

public class CheckUtil {

	private static final Logger logger = Logger.getLogger(CheckUtil.class);

	/**
	 * 校验 手机验证码
	 */
	private static String checkPhoneVerificationCode(AccountVerifyCodeMapper accountVerifyCodeMapper,
			String phonenumber, String verifyCode,String result) throws VerificationCodeErrorException, ParseException,
			VerificationCodeExpiredException, VerificationCodeUsedException {
		if (phonenumber == null || verifyCode == null) {
			logger.error("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is null");
			return result = "{\"error\":\"1\"}";//表示验证码错误
		}

		TbAccountVerifyCode accountVerifyCode = accountVerifyCodeMapper.getByPhone(phonenumber);
		if (accountVerifyCode == null) {
			logger.error("phonenumber:" + phonenumber + " is not get verification yet");
			//throw new VerificationCodeErrorException("phonenumber:" + phonenumber + " not get verification yet");
			return result = "{\"error\":\"1\"}";
		}
		//校验验证码
		if (!verifyCode.equals(accountVerifyCode.getVerifycode())) {
			logger.error("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is not equals DB verifyCode");
			//throw new VerificationCodeErrorException("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is not equals DB verifyCode");
			return result = "{\"error\":\"1\"}";
		}
		//若验证码过期
		if (TimeUtil.isExpired(accountVerifyCode.getVfcexpiretime())) {
			logger.error("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is expired");
			//throw new VerificationCodeExpiredException("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is expired");
			return result = "{\"error\":\"2\"}";
		}
		//若验证码已经被使用
		if (accountVerifyCode.getVfcstatus() == 1) {
			logger.error("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is used");
			//throw new VerificationCodeUsedException("phonenumber:" + phonenumber + ", verifyCode:" + verifyCode + " parameter is used");
			return result = "{\"error\":\"40\"}";
		}
		com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);
		if (json.get("error").toString().equals("0")){
			logger.info("phonenumber:" + phonenumber +",saveOrUpdate: " + " verifyCode:" + verifyCode);
			//设置验证码状态为已经被使用，再次使用时需要重新生成
			accountVerifyCode.setVfcstatus(1);
			accountVerifyCodeMapper.saveOrUpdateById(accountVerifyCode);
		}
		return result;
	}

	/**
	 * 通过邮箱校验 验证码
	 */
	private static void checkMailVerificationCode(AccountVerifyCodeMapper accountVerifyCodeMapper,
			String mailaddress, String verifyCode) throws VerificationCodeErrorException, ParseException,
			VerificationCodeExpiredException, VerificationCodeUsedException {
		if (mailaddress == null || verifyCode == null) {
			logger.error("mailaddress:" + mailaddress + ", verifyCode:" + verifyCode + " parameter error");
		}

		TbAccountVerifyCode accountVerifyCode = accountVerifyCodeMapper.getByMail(mailaddress);
		if (accountVerifyCode == null) {
			throw new VerificationCodeErrorException(
					"verifycode:" + verifyCode + ",mailaddress:" + mailaddress + " not get verification yet");
		}
		if (!verifyCode.equals(accountVerifyCode.getVerifycode())) {
			throw new VerificationCodeErrorException(
					"verifycode:" + verifyCode + ",mailaddress:" + mailaddress + "is wrong");
		}
		if (TimeUtil.isExpired(accountVerifyCode.getVfcexpiretime())) {
			throw new VerificationCodeExpiredException(
					"verifycode:" + verifyCode + ",mailaddress:" + mailaddress + "is expired");
		}
		if (accountVerifyCode.getVfcstatus() == 1) {
			throw new VerificationCodeUsedException(
					"verifycode:" + verifyCode + ",mailaddress:" + mailaddress + "is used");
		}

		accountVerifyCode.setVfcstatus(1);
		accountVerifyCodeMapper.saveOrUpdateById(accountVerifyCode);
	}

	/**
	 * 校验验证码
	 */
	public static String checkVerificationCode(AccountVerifyCodeMapper accountVerifyCodeMapper, String phonenumber,
			String mailaddress, String verifyCode, String result) throws VerificationCodeErrorException, ParseException,
			VerificationCodeExpiredException, VerificationCodeUsedException, ParameterErrorException {
		if (phonenumber != null && mailaddress == null) {
			 result = checkPhoneVerificationCode(accountVerifyCodeMapper, phonenumber, verifyCode, result);
		} else if (phonenumber == null && mailaddress != null) {
			//checkMailVerificationCode(accountVerifyCodeMapper, mailaddress, verifyCode);
		} else {
			throw new ParameterErrorException("Parameter error");
		}
		return result;
	}

	public static void checkIfExist(AccountMapper accountMapper, String phonenumber, String mailaddress)
			throws AccountPhonenumberAlreadyExistException, ParameterErrorException,
			AccountMailaddressAlreadyExistException {
		TbAccount tbFxAccount;
		if (phonenumber != null && mailaddress == null) {
			tbFxAccount = accountMapper.getByPhone(phonenumber);
			if (tbFxAccount != null) {
                throw new AccountPhonenumberAlreadyExistException("phone number:" + phonenumber + " already exist!");
			}
		} else if (mailaddress != null && phonenumber == null) {
			tbFxAccount = accountMapper.getByMail(mailaddress);
			if (tbFxAccount != null) {
				throw new AccountMailaddressAlreadyExistException("mail:" + mailaddress + " already exist!");
			}
		} else {
			throw new ParameterErrorException("Parameter error");
		}
	}

	public static void checkProperty(String property) throws ParameterErrorException {
		try {
			JSONObject propertyJSON = JSONObject.parseObject(property);
			if(propertyJSON instanceof JSONObject){
				// Do nothing.
			} else {
				throw new ParameterErrorException("data format is wrong!");
			}
			@SuppressWarnings("unchecked")
			Iterator<String> keys = propertyJSON.keySet().iterator();

			while (keys.hasNext()) {
				if (!AccountData.containsKey(keys.next())) {
					throw new ParameterErrorException("Parameter error");
				}
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ParameterErrorException("Parameter error");
		}
	}
}
