package com.berp.mrp.entity;

import com.berp.mrp.entity.Process;

public class ProcessStep {
	private Integer id;
	private String name;
	private Process process;
	private Integer type = 0;
	private String serial;
	private Step step;

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

	//step
	
	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}
}
