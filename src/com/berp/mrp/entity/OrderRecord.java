package com.berp.mrp.entity;

import java.util.Set;

import com.berp.mrp.entity.Process;

public class OrderRecord {
	private Integer id;
	private Double number;
	private Double finishNumber=0.00;
	private Integer status=0;
	
	private Material material;
	private Step surface;
	private Order order;
	private Set<Plan> plans;
	
	public OrderRecord(){
		
	}
	
	public OrderRecord (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.Double getNumber () {
		return number;
	}

	public void setNumber (java.lang.Double number) {
		this.number = number;
	}
	
	//
	public java.lang.Double getFinishNumber () {
		return finishNumber==null?0:finishNumber;
	}

	public void setFinishNumber (java.lang.Double num) {
		this.finishNumber = num;
	}

	public Double getNotFinishNumber(){
		return this.getNumber() - this.getFinishNumber();
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	
	//material
	public Material getMaterial () {
		return material;
	}

	public void setMaterial (Material m) {
		this.material = m;
	}
	
	//surface
	public Step getSurface() {
		return surface;
	}

	public void setSurface(Step surface) {
		this.surface = surface;
	}
	
	public Order getOrd () {
		return order;
	}

	public void setOrd (Order order) {
		this.order = order;
	}
	
	public Set<Plan> getPlans(){
		return this.plans;
	}
	
	public void setPlans(Set<Plan> plans){
		this.plans = plans;
	}
	
	public String getPlansDetail(){
		StringBuilder result = new StringBuilder();
		for(Plan plan : plans){
			result.append(plan.getDetail()+"<br>");
		}
		return result.toString();
	}
}
