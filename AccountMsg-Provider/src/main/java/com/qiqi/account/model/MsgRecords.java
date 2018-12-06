package com.qiqi.account.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="fx_sms_sendMsgRecords")
public class MsgRecords {

    private long id;
    private Date sendTime;
    private String phoneNB;
    private String msgid;
    private String type;
    private String country;
    private String verificationType;


    @GeneratedValue
    @Id
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @Column(name = "PhoneNB")
    public String getPhoneNB() {
        return phoneNB;
    }

    public void setPhoneNB(String phoneNB) {
        this.phoneNB = phoneNB;
    }

    @Column(name = "msgid", unique = true, nullable = true)
    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

}
