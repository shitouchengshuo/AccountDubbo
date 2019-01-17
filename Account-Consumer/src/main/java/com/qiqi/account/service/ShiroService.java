package com.qiqi.account.service;

import com.alibaba.fastjson.JSON;
import com.qiqi.account.SendPhoneMsgService;
import com.qiqi.account.dao.AccountMapper;
import com.qiqi.account.dao.AccountVerifyCodeMapper;
import com.qiqi.account.exception.*;
import com.qiqi.account.model.TbAccount;
import com.qiqi.account.model.TbAccountVerifyCode;
import com.qiqi.account.utils.MD5Util;
import com.qiqi.account.utils.SaltAndMD5Util;
import com.qiqi.account.utils.TimeUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Service
public class ShiroService {

    private static final Logger logger = Logger.getLogger(ShiroService.class);
    private final static int verificationCodeKeepSecond = 60; //验证码超时时间60秒
    private final static int verificationCodeExpireSecond = 1800;//验证码过期时间半小时
    @Autowired
    private AccountVerifyCodeMapper accountVerifyCodeMapper;

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 发送短信验证码
     */
    public String sendPhoneVerificationCode(String phoneNumber, String expiresecond)throws Exception {
        TbAccountVerifyCode accountVerifyCode = accountVerifyCodeMapper.getByPhone(phoneNumber);
        Integer expire = null;
        //设置验证码过期时间
        if(expiresecond == null || expiresecond.equals("") || Integer.valueOf(expiresecond)<=0) {
            expire = verificationCodeExpireSecond;
        }else {
            expire = Integer.valueOf(expiresecond);
        }
        String randomNumber = null;
        if (accountVerifyCode != null) {
            //如果验证码超时或者码状态为1（表示已经被使用），重新生成验证码
            if (TimeUtil.isExpired(accountVerifyCode.getVfccreatetime(), verificationCodeKeepSecond)
                    || accountVerifyCode.getVfcstatus() == 1) {
                randomNumber = generateRandomNumber();
                accountVerifyCode.setVerifycode(randomNumber);
                accountVerifyCode.setVfccreatetime(TimeUtil.DateToString(new Date()));
                //设置验证码过期时间，这里过期时间为半小时
                accountVerifyCode.setVfcexpiretime(TimeUtil.getExpiredTime(new Date(), expire));
            } else {
                randomNumber = accountVerifyCode.getVerifycode();
            }
        } else {
            randomNumber = generateRandomNumber();
            accountVerifyCode = new TbAccountVerifyCode();
            accountVerifyCode.setPhonenumber(phoneNumber);
            accountVerifyCode.setVerifycode(randomNumber);
            accountVerifyCode.setVfccreatetime(TimeUtil.DateToString(new Date()));
            accountVerifyCode.setVfcexpiretime(TimeUtil.getExpiredTime(new Date(), expire));
        }
        //设置验证码状态为0，表示未被使用
        accountVerifyCode.setVfcstatus(0);
        //将验证码及手机、邮箱信息保存到数据库
        accountVerifyCodeMapper.saveOrUpdateById(accountVerifyCode);

        String result = null;
        logger.info("向手机："+phoneNumber+"发送短信验证码：" + randomNumber);
        try {
            result = sendTextMessage(phoneNumber, randomNumber, String.valueOf(expire));
        } catch (VerificationCodeRequestTooMuchException e) {
            throw new VerificationCodeRequestTooMuchException();
        } catch (VerificationCodeRequestTooFastException e) {
            throw new VerificationCodeRequestTooFastException();
        } catch (ParameterErrorException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new ParameterErrorException();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new GetVerificationCodeFaliureException("send verification code:" + randomNumber + " failure!");
        }

        logger.info("---FX_Cloud_Platform result:" + result);
        return result;
    }

    /**
     * @return 六位数字的随机数验证码
     */
    public static String generateRandomNumber() {
        int random = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(random);
    }

    /**
     * 发送短信验证码,dubbo调用AccountMsg-Provider的SendPhoneMsgService接口方法
     */
    private String sendTextMessage(String phoneNumber, String randomnumber, String expiresecond) throws Exception {

        logger.info("phoneNB=" + phoneNumber +  "&verificationCode=" + randomnumber + "&expiresecond=" + expiresecond);

        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("spring/spring-application.xml");
        context.start();
        System.out.println("consumer start");
        SendPhoneMsgService demoService = context.getBean(SendPhoneMsgService.class);
        System.out.println("consumer");
        String result = demoService.sendPhoneMsg(phoneNumber, randomnumber, expiresecond);

        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);
        logger.info("send mobilePhone reslut: " + json.get("error").toString());
        // 短信服务返回102标示该手机请求次数超额
//        if (json.get("error").toString().equals("102"))
//            throw new VerificationCodeRequestTooMuchException("Phone Number: " + phoneNumber + "requested too much");
//        // 短信服务返回103标示该手机请求过快
//        if (json.get("error").toString().equals("103"))
//            throw new VerificationCodeRequestTooFastException("Phone Number: " + phoneNumber + "requested too fast");
//        if (json.get("error").toString().equals("104"))
//            throw new VerificationCodeRequestTooFastException("Phone Number: " + phoneNumber + "the system is busy");
//        if (!json.get("error").toString().equals("0"))
//            throw new GetVerificationCodeFaliureException("send verification code:" + randomnumber + " failure!");
        return result;
    }

    /**
     * 通过手机号注册
     */
    public String registeredPhoneAccount(String accountname, String password, String phonenumber, String verificationcode,
                                      String data) throws Exception {
        TbAccount account = accountMapper.getByAccountName(accountname);
        String result = "{\"error\":\"0\"}";
        if (account != null ) {
            return result = "{\"error\":\"24\"}"; //用户已存在
            //throw new AccountNameAlreadyExistException("Account name:" + accountname + " already exist!");
        }

        if (account != null && phonenumber.equals(account.getPhonenumber()) && !account.isUnActived()) {
            return result = "{\"error\":\"22\"}";//用户未激活
            //throw new AccountIsUnActivedException("Account name:" + accountname + " is UnActived!");
        }

        account = accountMapper.getByPhone(phonenumber);
        if (account != null && !account.isUnActived() && account.getPassword() != null) {
            return result = "{\"error\":\"14\"}";//手机号已经注册
            //throw new AccountPhonenumberAlreadyExistException("Phone number:" + phonenumber + " already exist!");
        }
        //验证码校验，及设置验证码的使用状态
        result = CheckUtil.checkVerificationCode(accountVerifyCodeMapper, phonenumber, null, verificationcode, result);
        if (data != null) {
            //校验用户的详细信息是否完整
            CheckUtil.checkProperty(data);
        }

        if (account == null) {
            account = new TbAccount();
        }
        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);
        if (json.get("error").toString().equals("0")){
            // MD5以用户名作为盐值对密码进行加密
            String salt = accountname;
            password =  MD5Util.ShiroMD5(password);
            account.setPasssalt(salt);
            account.setPassword(password);

            account.setAccountname(accountname);
            account.setPhonenumber(phonenumber);
            account.setCreatetime(TimeUtil.DateToString(new Date()));
            account.setUpdatetime(TimeUtil.DateToString(new Date()));
            account.setAccount_status(1);//账户激活
            accountMapper.saveOrUpdateByPhone(account);
        }
        return result;
    }

    /**
     * 通过手机号或邮箱得到uid
     * @param phonenumber
     * @param mailaddress
     * @return
     * @throws Exception
     */
    public String getUidByPhoneOrMail(String phonenumber, String mailaddress, String accountname) throws Exception {
        TbAccount account = null;

        if (phonenumber != null && mailaddress == null ) {
            account = accountMapper.getByPhone(phonenumber);
        } else if (phonenumber == null && mailaddress != null) {
            //account = accountMapper.getByMail(mailaddress);
        } else if (phonenumber == null && mailaddress == null && accountname != null) {
            account = accountMapper.getByAccountName(accountname);
        } else {
            throw new ParameterErrorException();
        }

        if (account == null) {
            throw new AccountNotExistException();
        }
        return String.valueOf(account.getUid());
    }

    /**
     *

    public void registeredMailAccount(String accountname, String emailaddress, String verificationcode, String password,
                                      int appId, String data) throws Exception {
        TbFxAccount fxAccount = fxAccountMapper.getByAccountName(accountname);
        if (fxAccount != null && !emailaddress.equals(fxAccount.getEmailaddress())) {
            throw new AccountNameAlreadyExistException("Account name:" + accountname + " already exist!");
        }

        if (fxAccount != null && emailaddress.equals(fxAccount.getEmailaddress()) && !fxAccount.isUnActived()) {
            throw new AccountNameAlreadyExistException("Account name:" + accountname + " already exist!");
        }
        fxAccount = fxAccountMapper.getByMail(emailaddress);
        if (fxAccount != null && !fxAccount.isUnActived()) {
            throw new AccountMailaddressAlreadyExistException("emailaddress:" + emailaddress + " already exist!");
        } else if (fxAccount == null) {
            fxAccount = new TbFxAccount();
        }
        CheckUtil.checkVerificationCode(fxAccountVerifyCodeMapper, null, emailaddress, verificationcode);
        CheckUtil.checkProperty(data);

        // pbkdf2算法对密码进行加密
        String salt = EncryptedPBKDF2.generateSalt();// 生成盐
        password = EncryptedPBKDF2.getEncryptedPwd(password, salt);
        fxAccount.setPasssalt(salt);
        fxAccount.setPassword(password);

        fxAccount.setAccountname(accountname);
        fxAccount.setEmailaddress(emailaddress);
        fxAccount.setCreatetime(TimeUtil.DateToString(new Date()));
        fxAccount.setUpdatetime(TimeUtil.DateToString(new Date()));
        fxAccount.setAccount_status(0x01);
        fxAccount.setRegister_source(appId);
        fxAccountMapper.saveOrUpdateByPhone(fxAccount);

        if (data != null) {
            Thread.sleep(800);
            TbFxAccount newAccount = GetUtil.getAccount(fxAccountMapper, null, emailaddress, null);
            updateAccountData(newAccount.getUid(), data);
        }
    }

     */

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("spring/spring-application.xml");
        context.start();
        System.out.println("consumer start");
        SendPhoneMsgService demoService = context.getBean(SendPhoneMsgService.class);
        System.out.println("consumer");
        //String result = demoService.sendPhoneMsg(phoneNumber, randomnumber, expiresecond);
        String result = demoService.sendPhoneMsg("13167003258", "123456", "30");
        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);
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
