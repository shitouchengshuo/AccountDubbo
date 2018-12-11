package com.qiqi.account.service;

import com.alibaba.fastjson.JSON;
import com.qiqi.account.SendPhoneMsgService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by ZhaoQiqi on 2018/11/26.
 */
@Service
public class ShiroService {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("consumer.xml");
        context.start();
        System.out.println("consumer start");
        SendPhoneMsgService demoService = context.getBean(SendPhoneMsgService.class);
        System.out.println("consumer");
        //String result = demoService.sendPhoneMsg(phoneNumber, randomnumber, expiresecond);
        String result = demoService.sendPhoneMsg("13167003258", "123456", "30");
        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result);
    }
}
