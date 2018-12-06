package com.qiqi.account;

/**
 * Created by ZhaoQiqi on 2018/12/6.
 */
public interface SendPhoneMsgService {
   //expiresecond 验证码过期时间
   String sendPhoneMsg(String phoneNumber, String verificationCode, String expiresecond);
}
