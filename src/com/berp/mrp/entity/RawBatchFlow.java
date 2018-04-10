package com.berp.mrp.entity;

public class RawBatchFlow extends BaseBatchFlow {
	
	private Double arriveNumber;
	private RawBatchFlow parent;
	
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
		return this.getNumber() - this.getArriveNumber() - this.getLeftNumber();
	}
}
