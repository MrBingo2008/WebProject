package com.berp.mrp.entity;

public class BatchFlow extends BaseBatchFlow {

	private BatchFlow parent;
	
	public static enum Type{purchaseIn, purchaseBack, sellOut, sellBack, planMaterial, planIn, checkIn, checkOut}
	
	public BatchFlow(){
		
	}
	
	public BatchFlow (java.lang.Integer id) {
		this.setId(id);
	}
	
	//parent
	public BatchFlow getParent () {
		return parent;
	}

	public void setParent (BatchFlow parent) {
		this.parent = parent;
	}
}
