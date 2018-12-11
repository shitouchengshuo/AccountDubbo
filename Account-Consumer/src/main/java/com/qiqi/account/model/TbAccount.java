package com.qiqi.account.model;

import lombok.Data;

import java.io.Serializable;

@SuppressWarnings("serial")
@Data
public class TbAccount implements Serializable {

	private int uid;    //自增id
	private String accountname;
	private String password;
    private String passsalt;  //盐值
	private String phonenumber;
	private String emailaddress;
	private int roleId;
	private String securityQA;
	
	/*
	 * account_stats: 描述账户的状态的位
	 * 账户的激活状态
	 * 0表示未激活，1表示已激活
	 * 3表示登陆中，4表示未登录
	 * 5表示手机已经通过旧手机的认证，可以被重新绑定
	 * 6表示邮箱已经通过旧邮箱的认证，可以被重新绑定
	 * 7表示账户冻结
	 */
	private int account_status;
	private int register_source;
	private String createtime;
	private String  updatetime;

    private String access_token;
    private String refresh_token;

	private int count;
	
	public boolean isUnActived() {
		if (account_status == 0) {
			return true;
		}
		return false;
	}

	public boolean isActived() {
		if (account_status  == 1) {
			return true;
		}
		return false;
	}
}
