package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;

public class PlanStepNumber {
	private Integer id;
	
	private java.util.Date startTime;
	private java.util.Date endTime;
	private Double number;
	
	private Integer priority;
	private PlanStep step;
	
	public PlanStepNumber(){
	}
	
	public PlanStepNumber (java.lang.Integer id) {
		this.setId(id);
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public java.util.Date getStartTime() {
		return startTime;
	}

	public void setStartTime(java.util.Date startTime) {
		this.startTime = startTime;
	}

	public java.util.Date getEndTime() {
		return endTime;
	}

	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}

	public Double getNumber() {
		return number;
	}

	public void setNumber(Double number) {
		this.number = number;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public PlanStep getStep() {
		return step;
	}

	public void setStep(PlanStep step) {
		this.step = step;
	}

}
