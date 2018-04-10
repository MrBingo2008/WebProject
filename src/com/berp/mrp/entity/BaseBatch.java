package com.berp.mrp.entity;

public class BaseBatch {
	private Integer id;
	private String serial;
	private Double numPerBox;
	private Integer boxNum;
	private Double number;
	private Double leftNum;
	private Plan plan;
	
	public BaseBatch(){
	}
	
	public BaseBatch (String serial, Double number, Plan plan) {
		this.serial = serial;
		this.number = number;
		this.leftNum = number;
		this.plan = plan;
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}
	
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}

	//numPerBox
	public java.lang.Double getNumPerBox () {
		return numPerBox;
	}
	
	public void setNumPerBox (java.lang.Double num) {
		this.numPerBox = num;
	}
	
	//boxNum
	public java.lang.Integer getBoxNum () {
		return boxNum;
	}
	
	public void setBoxNum (Integer num) {
		this.boxNum = num;
	}
	
	//number
	public java.lang.Double getNumber () {
		return number;
	}
	
	public void setNumber (java.lang.Double num) {
		this.number = num;
	}

	//leftNum
	public java.lang.Double getLeftNum () {
		return leftNum;
	}

	public void setLeftNum (java.lang.Double num) {
		this.leftNum = num;
	}
	
	public Plan getPlan () {
		return plan;
	}

	public void setPlan (Plan plan) {
		this.plan = plan;
	}
}
