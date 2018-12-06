package com.qiqi.account.service;

import com.alibaba.fastjson.JSON;
import com.qiqi.account.dao.AccountVerifyCodeMapper;
import com.qiqi.account.exception.GetVerificationCodeFaliureException;
import com.qiqi.account.exception.ParameterErrorException;
import com.qiqi.account.exception.VerificationCodeRequestTooFastException;
import com.qiqi.account.exception.VerificationCodeRequestTooMuchException;
import com.qiqi.account.model.TbAccountVerifyCode;
import com.qiqi.account.utils.TimeUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Date;


/**
 * Created by ZhaoQiqi on 2018/11/26.
 */
@Service
public class ShiroService {

    private static final Logger logger = Logger.getLogger(ShiroService.class);
    private final static int verificationCodeKeepSecond = 60; //验证码超时时间60秒
    private final static int verificationCodeExpireSecond = 1800;//验证码过期时间半小时
    @Autowired
    private AccountVerifyCodeMapper accountVerifyCodeMapper;

    /**
     * 发送短信验证码
     */
    public String sendPhoneVerificationCode(String phonenumber, String expiresecond)throws Exception {
        TbAccountVerifyCode accountVerifyCode = accountVerifyCodeMapper.getByPhone(phonenumber);
        Integer expire = null;
        //设置验证码过期时间
        if(expiresecond == null || expiresecond.equals("") || Integer.valueOf(expiresecond)<=0) {
            expire = verificationCodeExpireSecond;
        }else {
            expire = Integer.valueOf(expiresecond);
        }
        String randomnumber = null;
        if (accountVerifyCode != null) {
            //如果验证码超时或者码状态为1，重新生成验证码
            if (TimeUtil.isExpired(accountVerifyCode.getVfccreatetime(), verificationCodeKeepSecond)
                    || accountVerifyCode.getVfcstatus() == 1) {
                randomnumber = generateRandomNumber();
                accountVerifyCode.setVerifycode(randomnumber);
                accountVerifyCode.setVfccreatetime(TimeUtil.DateToString(new Date()));
                //设置验证码过期时间，这里过期时间为0
                accountVerifyCode.setVfcexpiretime(TimeUtil.getExpiredTime(new Date(), expire));
            } else {
                randomnumber = accountVerifyCode.getVerifycode();
            }
        } else {
            randomnumber = generateRandomNumber();
            accountVerifyCode = new TbAccountVerifyCode();
            accountVerifyCode.setPhonenumber(phonenumber);
            accountVerifyCode.setVerifycode(randomnumber);
            accountVerifyCode.setVfccreatetime(TimeUtil.DateToString(new Date()));
            accountVerifyCode.setVfcexpiretime(TimeUtil.getExpiredTime(new Date(), expire));
        }
        accountVerifyCode.setVfcstatus(0);
        //将验证码及手机、邮箱信息保存到数据库
        accountVerifyCodeMapper.saveOrUpdateById(accountVerifyCode);

        String result = null;
        logger.info("向手机："+phonenumber+"发送短信验证码：" + randomnumber);
        try {
            result = sendTextMessage(phonenumber, randomnumber, String.valueOf(expire));
        } catch (VerificationCodeRequestTooMuchException e) {
            throw new VerificationCodeRequestTooMuchException();
        } catch (VerificationCodeRequestTooFastException e) {
            throw new VerificationCodeRequestTooFastException();
        }catch (ParameterErrorException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new ParameterErrorException();
        }
        catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new GetVerificationCodeFaliureException("send verification code:" + randomnumber + " failure!");
        }

        logger.info("---FX_Cloud_Platform result:" + result);

        String errorCode = null;

        if (!"0".equals(errorCode)) {
            throw new GetVerificationCodeFaliureException("send verification code:" + randomnumber + " failure!",
                    errorCode);
        }

        return errorCode;
    }

    /**
     * @return 六位数字的随机数
     */
    public static String generateRandomNumber() {
        int random = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(random);
    }

    /**
     * 发送短信验证码
     * @param phone
     * @param randomnumber
     * @param expiresecond
     * @return
     */
    private String sendTextMessage(String phone, String randomnumber, String expiresecond) throws Exception {

        logger.info("phoneNB=" + phone +  "&verificationCode=" + randomnumber + "&expiresecond=" + expiresecond);
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        System.out.println("consumer start");
        SendPhoneMsgService demoService = context.getBean(SendPhoneMsgService.class);
        System.out.println("consumer");
        System.out.println(demoService.getPermissions(1L));

        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);

        logger.info("platform reslut: " + json.get("errorCode").toString());

        // 短信服务返回102标示该手机请求次数超额
        if (json.get("errorCode").toString().equals("102"))
            throw new VerificationCodeRequestTooMuchException("Phone Number: " + phone + "requested too much");
        // 短信服务返回38标示该手机请求过快
        if (json.get("errorCode").toString().equals("38"))
            throw new VerificationCodeRequestTooFastException("Phone Number: " + phone + "requested too fast");
        if (json.get("errorCode").toString().equals("103"))
            throw new VerificationCodeRequestTooFastException("Phone Number: " + phone + "requested too fast");

        return result;
    }
    /**
      测试
    @RequiresRoles({"admin"})  //表示当前Subject需要角色admin
    public void testMethod(){
        System.out.println("testMethod,time:"
                +new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date()));

        //Shiro的Session能在Service层获取数据
        Session session = SecurityUtils.getSubject().getSession();
        Object val = session.getAttribute("test");

        System.out.println("Service SessionVal: "+val);
    }
     *
     */
}
