package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;

public class PlanStep {
	private Integer id;
	private Integer priority;
	private String name;
	private Integer type = 0;
	
	private Integer status = 0;
	private Plan plan;
	private Step step;
	
	private java.util.Date finishTime;
	private Double number;
	
	public static enum Status{notFinish, finish};
	
	public PlanStep(){
	}
	
	public PlanStep (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}
	
	//priority
	public java.lang.Integer getPriority () {
		return priority;
	}
	
	public void setPriority (java.lang.Integer priority) {
		this.priority = priority;
	}

	//name
	public java.lang.String getName () {
		return name;
	}

	public void setName (java.lang.String name) {
		this.name = name;
	}
	
	//type
	public java.lang.Integer getType () {
		return type;
	}

	public void setType (java.lang.Integer type) {
		this.type = type;
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	
	//plan
	public Plan getPlan(){
		return this.plan;
	}
	
	public void setPlan(Plan plan){
		this.plan = plan;
	}
	
	//finish time
	public java.util.Date getFinishTime () {
		return finishTime;
	}

	public void setFinishTime (java.util.Date time) {
		this.finishTime = time;
	}
	
	//number
	public java.lang.Double getNumber() {
		return number;
	}

	public void setNumber(java.lang.Double number) {
		this.number = number;
	}
	

	//step
	
	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}
}
