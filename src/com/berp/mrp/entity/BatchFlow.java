package com.berp.mrp.entity;

public class BatchFlow extends BaseBatchFlow {

	private BatchFlow parent;
	private Step surface;
	private OrderRecord record;
	
	public static enum Type{purchaseIn, purchaseBack, sellOut, sellBack, planMaterial, planIn, checkIn, checkOut}
	
	public BatchFlow(){
		
	}
	
	public BatchFlow (java.lang.Integer id) {
		this.setId(id);
	}
	
	//surface
	public Step getSurface() {
		return surface;
	}

	public void setSurface(Step surface) {
		this.surface = surface;
	}

	//record
	public OrderRecord getRecord() {
		return record;
	}

	public void setRecord(OrderRecord record) {
		this.record = record;
	}
	
	//parent
	public BatchFlow getParent () {
		return parent;
	}

	public void setParent (BatchFlow parent) {
		this.parent = parent;
	}
	
	public Step getDefaultSurface(){
		if(this.surface!=null)
			return surface;
		if(this.getMaterial().getSurface()!=null)
			return this.getMaterial().getSurface();
		return null;
	}
	
	//纯粹为了做参数用
	private Integer defaultSurfaceId;

	public Integer getDefaultSurfaceId() {
		return defaultSurfaceId;
	}

	public void setDefaultSurfaceId(Integer defaultSurfaceId) {
		this.defaultSurfaceId = defaultSurfaceId;
	}
}
