package com.berp.mrp.entity;

import java.util.List;

import com.berp.core.entity.Category;

public class Process {
	private Integer id;
	private String name;
	private String comment;
	private String serial;
	private Category category;
	
	private List<ProcessStep> steps;
	
	public Process(){
	}
	
	public Process (java.lang.Integer id) {
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
	
	//comment
	public java.lang.String getComment () {
		return comment;
	}

	public void setComment (java.lang.String comment) {
		this.comment = comment;
	}
	
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
	
	//category
	public Category getCategory () {
		return category;
	}

	public void setCategory (Category p) {
		this.category = p;
	}
	
	public List<ProcessStep> getSteps(){
		return this.steps;
	}
	
	public void setSteps(List<ProcessStep> steps){
		this.steps = steps;
	}
}
