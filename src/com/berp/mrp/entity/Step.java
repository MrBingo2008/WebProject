package com.berp.mrp.entity;

public class Step {
	private Integer id;
	private String name;
	private String serial;
	
	private Integer type;
	private boolean surface;
	
	public Step(){
		
	}
	
	public Step (java.lang.Integer id) {
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
	
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
	
	//type
	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}
	
	//surface
	public boolean getSurface() {
		return surface;
	}

	public void setSurface(boolean surface) {
		this.surface = surface;
	}
}
