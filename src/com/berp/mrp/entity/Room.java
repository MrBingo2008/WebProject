package com.berp.mrp.entity;

import com.berp.core.entity.Category;

public class Room {
	private Integer id;
	private String name;
	private Integer status;
	
	private Category parent;
	public Room(){
		
	}
	
	public Room (java.lang.Integer id) {
		this.setId(id);
	}
	
	//还有产品用料表、反映产品结构的物料清单
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
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	
	//parent
	public Category getParent () {
		return parent;
	}

	public void setParent (Category p) {
		this.parent = p;
	}
}
