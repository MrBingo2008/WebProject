package com.berp.mrp.entity;

public class Batch extends BaseBatch {
	
	private Integer type=0;
	
	private Material material;
	private Cir cir;
	
	public static enum BatchType{purchaseIn, rawProduct, product, sellBack, overflow}
	
	public Batch(){
	}
	
	public Batch (String serial, Integer type, Double number, Material material, Plan plan) {
		super(serial, number, plan);
		this.type = type;
		this.material = material;
	}
	
	public Batch (String serial, Double number, Material material, Plan plan){
		super(serial, number, plan);
		this.material = material;
	}
	
	//type
	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}

	//material
	public Material getMaterial () {
		return material;
	}

	public void setMaterial (Material material) {
		this.material = material;
	}
	
	//cir
	public Cir getCir () {
		return cir;
	}

	public void setCir (Cir cir) {
		this.cir = cir;
	}
}
