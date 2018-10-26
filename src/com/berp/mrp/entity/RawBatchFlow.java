package com.berp.mrp.entity;

import java.util.Set;

public class RawBatchFlow extends BaseBatchFlow {
	
	private Double arriveNumber;
	private RawBatchFlow parent;
	private Set<RawBatchFlow> children;

	private PlanStep planStep;
	
	public static enum Type{produce, outsideOut, outsideIn}
	
	public RawBatchFlow(){}
	
	public RawBatchFlow (String serial, Double number, Plan plan){
		this.setSerial(serial);
		this.setNumber(number);
		this.setLeftNumber(number);
		this.setPlan(plan);
		this.setMaterial(plan.getMaterial());
		this.arriveNumber = 0.00;
	}
	
	public String getInfo(){
		if(this.getCir()!=null)
			return String.format("%s / %s", this.getSerial(), this.getCir().getCompany().getName());
		return this.getSerial();
	}
	
	//parent
	public RawBatchFlow getParent () {
		return parent;
	}

	public void setParent (RawBatchFlow batch) {
		this.parent = batch;
	}
	
	//arriveNum
	public java.lang.Double getArriveNumber () {
		return arriveNumber == null?0:arriveNumber;
	}

	public void setArriveNumber (java.lang.Double num) {
		this.arriveNumber = num;
	}
	
	public Double getNotArriveNumber(){
		//对于type=0，也适用于type=1， leftNumber=0
		return this.getNumber() - this.getArriveNumber();
	}
	
	//children
	public Set<RawBatchFlow> getChildren() {
		return children;
	}

	public void setChildren(Set<RawBatchFlow> children) {
		this.children = children;
	}
	
	public String getChildrenString(){
		StringBuilder sb = new StringBuilder();
		for(RawBatchFlow flow:children){
			sb.append(flow.getSerial());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	//获取他的子flows所属的业务单号
	public String getchildrenParentSerial(){
		StringBuilder sb = new StringBuilder();
		for(RawBatchFlow flow : this.getChildren()){
			sb.append(flow.getCir()==null?flow.getPlan().getSerial() : flow.getCir().getSerial());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	//plan step
	public PlanStep getPlanStep() {
		return planStep;
	}

	public void setPlanStep(PlanStep planStep) {
		this.planStep = planStep;
	}
}
