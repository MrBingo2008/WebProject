package com.berp.mrp.entity;

import com.berp.core.entity.Category;

public class BaseBatchFlow {
	private Integer id;
	private String serial;
	
	private Integer direct=1;
	private Integer type=0;
	private Integer status = 0;
	
	private Double number;
	private Double leftNumber;
	private Double numPerBox;
	private Integer boxNum;

	private Material material;
	private Category room;
	private Plan plan;
	private Cir cir;
	
	private Integer priority;
	
	public BaseBatchFlow(){
		
	}
	
	public BaseBatchFlow (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}

	//serial
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
	
	//priority
	public java.lang.Integer getPriority () {
		return priority;
	}
	
	public void setPriority (java.lang.Integer priority) {
		this.priority = priority;
	}
	
	//type
	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
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
	
	//leftNumber
	public java.lang.Double getLeftNumber () {
		return leftNumber == null ? 0: leftNumber;
	}

	public void setLeftNumber (java.lang.Double number) {
		this.leftNumber = number;
	}
	
	public java.lang.Integer getDirect() {
		return direct;
	}

	public void setDirect (java.lang.Integer direct) {
		this.direct = direct;
	}

	//material
	public Material getMaterial () {
		return material;
	}

	public void setMaterial (Material material) {
		this.material = material;
	}

	//room
	public Category getRoom () {
		return room;
	}

	public void setRoom (Category room) {
		this.room = room;
	}
	
	//Cir
	public Cir getCir () {
		return cir;
	}

	public void setCir (Cir cir) {
		this.cir = cir;
	}
	
	//Plan
	public Plan getPlan () {
		return plan;
	}

	public void setPlan (Plan plan) {
		this.plan = plan;
	}

}
