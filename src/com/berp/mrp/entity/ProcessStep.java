package com.berp.mrp.entity;

import com.berp.mrp.entity.Process;

public class ProcessStep {
	private Integer id;
	private String name;
	private Process process;
	private Integer type = 0;
	private String serial;
	/*private String unit;
	private String spec;
	private String dim;
	
	
	private Integer fetchType;
	private Integer status;*/
	
	public ProcessStep(){
		
	}
	
	public ProcessStep (java.lang.Integer id) {
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
	
	public Process getProcess () {
		return process;
	}

	public void setProcess (Process process) {
		this.process = process;
	}
	
	//type
	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}
		
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
/*
	public java.lang.String getUnit () {
		return unit;
	}

	public void setUnit (java.lang.String unit) {
		this.unit = unit;
	}
	
	public java.lang.String getSpec () {
		return spec;
	}

	public void setSpec (java.lang.String spec) {
		this.spec = spec;
	}
	
	public java.lang.String getDim() {
		return dim;
	}

	public void setDim (java.lang.String dim) {
		this.dim = dim;
	}
	
	//fetchType
	public java.lang.Integer getFetchType() {
		return fetchType;
	}

	public void setFetchType(java.lang.Integer fetchType) {
		this.fetchType = fetchType;
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}*/
}
