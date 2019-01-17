package com.qiqi.account.impl;

import com.alibaba.fastjson.JSON;
import com.qiqi.account.SendPhoneMsgService;
import com.qiqi.account.model.MsgRecords;
import com.qiqi.account.model.chuanglan.request.SmsSendRequest;
import com.qiqi.account.model.chuanglan.response.SmsSendResponse;
import com.qiqi.account.utils.ChuangLanSmsUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sendPhoneMsgService")
public class SendPhoneMsgServiceImpl implements SendPhoneMsgService {

    public static final Logger logger = Logger.getLogger(SendPhoneMsgServiceImpl.class);

    /**
     *  通过创蓝向用户手机发送验证码
     * @param phoneNumber
     * @param verificationCode
     * @param expiresecond
     * @return 格式："{\"error\":" + xx + "}"
     */
    public String sendPhoneMsg(String phoneNumber, String verificationCode, String expiresecond) {
//        String phoneNumber = "";
//        String verificationCode = "123456";
//        String expiresecond = "";
        logger.info("/verification传入的参数是：" + "phoneNumber=" + phoneNumber + ",verificationCode=" + verificationCode+",expiresecond="+expiresecond);

        Integer intExpireMinute = null;
        if (expiresecond == null || expiresecond.equals("")) {
            intExpireMinute = 30;
        } else {
            intExpireMinute = Integer.valueOf(expiresecond) / 60;
        }

        //此处对短信发送数目及间隔的判断，待实现，代码在本页最后面

        // 发送给用户手机的内容
        String msg = "验证码为：" + verificationCode + ",请于" + intExpireMinute + "分钟内输入。";

        //短信发送的URL 请登录zz.253.com 获取完整的URL接口信息
        String smsSingleRequestServerUrl = "http://smssh1.253.com/msg/send/json";

        //状态报告
        String report = "true";
        String accountKey = "****";
        String pswdKey = "*****";
        //String accountKey = PropertiesUtil.getConfigProp().getProperty("account");
        //String pswdKey = PropertiesUtil.getConfigProp().getProperty("pswd");
        SmsSendRequest smsSingleRequest = new SmsSendRequest(accountKey, pswdKey, msg, phoneNumber, report);
        String requestJson = JSON.toJSONString(smsSingleRequest);
        String response = ChuangLanSmsUtil.sendSmsByPost(smsSingleRequestServerUrl, requestJson);
        SmsSendResponse smsSingleResponse = JSON.parseObject(response, SmsSendResponse.class);

        // 向数据库写入短信发送信息
        if (smsSingleResponse.getMsgId() != null && !smsSingleResponse.getMsgId().equals("")) {
            MsgRecords msgNewRecords = new MsgRecords();
            //这里主要用于限制每天发送的短信次数及时间间隔
            msgNewRecords.setSendTime(new Date());
            msgNewRecords.setPhoneNB(phoneNumber);
            msgNewRecords.setMsgid(smsSingleResponse.getMsgId());
            // msgRecordsService.add(msgNewRecords);
        }
        //创蓝发送短信的状态码
        String sendSatues = smsSingleResponse.getCode();
        //方法返回值 格式："{\"error\":" + xx + "}"
        String result = "";
        if ("0".equals(sendSatues)){
            //发送成功
            logger.info("向用户手机：" + phoneNumber + "发送验证码：" + verificationCode+"成功！");
            result = "{\"error\":" + 0 + "}";
        } else if ("103".equals(sendSatues)) {
            //提交过快（提交速度超过流速限制）
            //必要时要保存错误信息到数据库，这里先不实现
            logger.warn("使用创蓝发送短信的状态"+smsSingleResponse.getErrorMsg());
            result = "{\"error\":" + 103 + "}";
        } else if ("104".equals(sendSatues)) {
            //系统忙
            logger.error("使用创蓝发送短信的状态"+smsSingleResponse.getErrorMsg());
            result = "{\"error\":" + 104 + "}";
        } else if ("107".equals(sendSatues)) {
            //包含错误的手机号码
            logger.error("使用创蓝发送短信的状态"+smsSingleResponse.getErrorMsg());
            result = "{\"error\":" + 107 + "}";
        }else if ("109".equals(sendSatues)) {
            //无发送额度 109
            logger.error("使用创蓝发送短信的状态"+smsSingleResponse.getErrorMsg());
            result = "{\"error\":" + 109 + "}";
        } else if ("110".equals(sendSatues)) {
            //不在发送时间内 110
            logger.error("使用创蓝发送短信的状态"+smsSingleResponse.getErrorMsg());
            result = "{\"error\":" + 110 + "}";
        }else {
            logger.error("使用创蓝发送短信的状态"+smsSingleResponse.getErrorMsg());
            result = "{\"error\":" + result + "}";
        }
        return result;



    //限制每天发送短信的次数,在配置文件中定义，默认false
    /** 待实现
     Boolean ifLimits = SMSUtils.ifLimitTimes();
     Records records = smsRecordsService.getTimesByType(appType);
     String d = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
     if (ifLimits) {
     LimitRecords limitRecords = smsLimitRecordsService.getTodayTimes(d);
     if (limitRecords == null) {
     LimitRecords limit = new LimitRecords();
     limit.setDate(d);
     limit.setTimes(0);
     smsLimitRecordsService.add(limit);
     } else if (limitRecords.getTimes() < SMSUtils.getMaxLimits()) {
     limitRecords.setTimes(limitRecords.getTimes() + 1);
     smsLimitRecordsService.update(limitRecords);
     } else {
     return "{\"errorCode\":" + 102 + "}";
     }
     }
     String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

     if(fxSmsAccount==null){
     addSendStatus("17", phonenumber, appid, null, null);
     resMap.put("errorCode", "17");
     resMap.put("message", "the service is not allowed! ");
     return JsonUtils.ojbectToStr(resMap);
     }

     // 检查发送短信间隔
     if ("true".equals(sendIntervalEnable)) {
     MsgRecords msgRecords = msgRecordsService.getMsgRecordsByPhone(phonenumber);
     if (msgRecords != null) {
     if (new Date().getTime() - msgRecords.getSendTime().getTime() < Long.parseLong(sendInterval)) {
     resMap.put("errorCode", "103");
     resMap.put("message", "send message so frequently");
     return JsonUtils.ojbectToStr(resMap);
     }
     }
     }

     // 检查appid一天发送的短信数
     Map<String, Object> checkLimitTimesMap = checkLimitTimes();
     if (!checkLimitTimesMap.get("errorCode").equals("0")) {
     return JsonUtils.ojbectToStr(checkLimitTimesMap);
     }
     *
     */
    }
}
