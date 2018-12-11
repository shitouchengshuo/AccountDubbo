package com.qiqi.account.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TbAccountVerifyCode implements Serializable {

	private String id;   //自增id
	private String phonenumber;
	private String emailaddress;
	private String verifycode;
	private String vfccreatetime;
	private String vfcexpiretime;
	private int vfcstatus; //验证码状态==1表示已经被使用
	private int count;
}
