package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;
import com.berp.core.entity.User;

public class BaseBill {
	private Integer id;
	private String name;
	private String serial;
	
	private User createUser;
	private java.util.Date createTime;
	
	private java.util.Date billTime;
	private Integer type = 0;
	private Integer status = 0;
	private String comment;
	
	public BaseBill(){
	}
	
	public BaseBill (java.lang.Integer id) {
		this.setId(id);
	}
	
	//id serial name
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getName () {
		return name;
	}

	public void setName (java.lang.String name) {
		this.name = name;
	}
	
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
	
	//create time and user
	public User getCreateUser () {
		return createUser;
	}

	public void setCreateUser (User user) {
		this.createUser = user;
	}
	
	public java.util.Date getCreateTime () {
		return createTime;
	}

	public void setCreateTime (java.util.Date time) {
		this.createTime = time;
	}
	
	//type status billTime comment
	public java.util.Date getBillTime () {
		return billTime;
	}

	public void setBillTime (java.util.Date time) {
		this.billTime = time;
	}
	
	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}
	
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}

	public java.lang.String getComment () {
		return comment;
	}

	public void setComment (java.lang.String comment) {
		this.comment = comment;
	}
}
