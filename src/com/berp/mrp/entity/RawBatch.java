package com.berp.mrp.entity;

public class RawBatch extends BaseBatch {
	private Double arriveNum;
	public RawBatch(){
	}
	
	public RawBatch (String serial, Double number, Plan plan){
		super(serial, number, plan);
		this.arriveNum = 0.00;
	}
	
	//arriveNum
	public java.lang.Double getArriveNum () {
		return arriveNum;
	}

	public void setArriveNum (java.lang.Double num) {
		this.arriveNum = num;
	}
	
	public Double getNotArriveNum(){
		return this.getNumber() - this.arriveNum - this.getLeftNum();
	}
}
