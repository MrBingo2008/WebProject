package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;

public class ProductMaterial {
	private Integer id;
	private Material product;
	private Material material;
	private Double productNumber;
	private Double materialNumber;
	private Integer priority;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Material getProduct() {
		return product;
	}
	public void setProduct(Material product) {
		this.product = product;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Double getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(Double productNumber) {
		this.productNumber = productNumber;
	}
	public Double getMaterialNumber() {
		return materialNumber;
	}
	public void setMaterialNumber(Double materialNumber) {
		this.materialNumber = materialNumber;
	}
	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
