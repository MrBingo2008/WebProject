package com.berp.core.entity;

public class Company {
	
	private Integer id;
	private String name;
	private String tel;
	private String address;
	private Category parent;
	
	public Company(){
		
	}
	
	public Company (java.lang.Integer id) {
		this.setId(id);
	}
	
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
	
	//tel
	public java.lang.String getTel () {
		return tel;
	}

	public void setTel (java.lang.String tel) {
		this.tel = tel;
	}
	
	//address
	public java.lang.String getAddress () {
		return address;
	}

	public void setAddress (java.lang.String address) {
		this.address = address;
	}
	
	//parent
	public Category getParent () {
		return parent;
	}

	public void setParent (Category p) {
		this.parent = p;
	}
}
