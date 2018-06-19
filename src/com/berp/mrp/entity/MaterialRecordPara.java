package com.berp.mrp.entity;

public class MaterialRecordPara {
	private Integer materialId;
	private String material;
	private Double materialNumber;
	
	private Integer recordId;
	private Integer productId;
	private String product;
	public Integer getMaterialId() {
		return materialId;
	}
	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public Double getMaterialNumber() {
		return materialNumber;
	}
	public void setMaterialNumber(Double materialNumber) {
		this.materialNumber = materialNumber;
	}
	public Integer getRecordId() {
		return recordId;
	}
	public void setRecordId(Integer recordId) {
		this.recordId = recordId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
}
