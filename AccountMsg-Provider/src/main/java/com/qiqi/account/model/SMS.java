package com.qiqi.account.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="fx_sms")
public class SMS {
	
	private long id;
	private String phoneNB;
	private Date createTime;
	private String verificationCode;
	private String smsContent;
	
	@GeneratedValue
	@Id
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Column(name = "phoneNB", unique = true, nullable = true)
	public String getPhoneNB() {
		return phoneNB;
	}
	public void setPhoneNB(String phoneNB) {
		this.phoneNB = phoneNB;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date date) {
		this.createTime = date;
	}
	
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	

}
