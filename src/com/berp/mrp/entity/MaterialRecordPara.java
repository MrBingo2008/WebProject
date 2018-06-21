package com.berp.mrp.entity;

public class MaterialRecordPara {
	private Integer materialId;
	private String materialInfo;
	private Double materialNumber;
	
	private Integer recordId;
	private String recordInfo;
	
	public Integer getMaterialId() {
		return materialId;
	}
	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}
	public String getMaterialInfo() {
		return materialInfo;
	}
	public void setMaterialInfo(String materialInfo) {
		this.materialInfo = materialInfo;
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

	public String getRecordInfo() {
		return recordInfo;
	}
	public void setRecordInfo(String recordInfo) {
		this.recordInfo = recordInfo;
	}
}
