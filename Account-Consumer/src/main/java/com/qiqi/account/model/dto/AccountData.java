package com.qiqi.account.model.dto;

import lombok.Data;

@Data
public class AccountData {
	private String nickname;
	private String realname;
	private String age;
	private String sex;
	private String img;
	private String zone;
	private String zipcode;
	private String address;
	private String weight;
	private String height;
	private String birthday;
	private String job;

	public static boolean containsKey(String key) {
		switch (key) {
		case "nickname":
		case "realname":
		case "age":
		case "sex":
		case "img":
		case "zone":
		case "zipcode":
		case "address":
		case "weight":
		case "height":
		case "birthday":
		case "job":
			
			return true;
		default:
			return false;
		}
	}

}
