package com.qiqi.account.model;

import javax.persistence.*;

@Entity
@Table(name="fx_sms_records")
public class Records {

	private int id;
	private long times;
	private String type;

	@GeneratedValue(strategy=GenerationType.AUTO)
	@Id
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getTimes() {
		return times;
	}
	public void setTimes(long times) {
		this.times = times;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
