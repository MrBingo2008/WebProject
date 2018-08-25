package com.berp.mrp.entity;

import java.util.List;
import java.util.Set;

import com.berp.core.entity.Category;

public class Process {
	private Integer id;
	private String name;
	private String comment;
	private String serial;
	private Category category;
	
	private Set<Material> materials;

	private List<ProcessStep> steps;
	
	public Process(){
	}
	
	public Process (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getName () {
		return name;
	}

	public void setName (java.lang.String name) {
		this.name = name;
	}
	
	//comment
	public java.lang.String getComment () {
		return comment;
	}

	public void setComment (java.lang.String comment) {
		this.comment = comment;
	}
	
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
	
	//category
	public Category getCategory () {
		return category;
	}

	public void setCategory (Category p) {
		this.category = p;
	}
	
	//materials，适用产品
	public Set<Material> getMaterials() {
		return materials;
	}

	public void setMaterials(Set<Material> materials) {
		this.materials = materials;
	}
	
	public String getApplyMaterialsInfo(){
		if(materials == null || materials.size() == 0)
			return null;
		StringBuilder result = new StringBuilder();
		for(Material material: materials){
			result.append(material.getNameSpec()+" ");
		}
		return result.toString();
	}
	
	
	public List<ProcessStep> getSteps(){
		return this.steps;
	}
	
	public void setSteps(List<ProcessStep> steps){
		this.steps = steps;
	}
}
