package com.berp.mrp.entity;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.berp.core.entity.Category;
import com.berp.core.entity.Company;

public class Material {
	private Integer id;
	private String name;
	private String serial;
	private String customerSerial;
	private Double leftNumber;
	private Double notPurchaseInNumber;
	private Double notSellOutNumber;
	private String unit;
	private String spec;
	private String dim;
	
	private Integer type;
	private Integer fetchType;
	private Integer status;
	
	private Category parent;
	private Company company;
	private Set<OrderRecord> records;
	
	public Material(){
		
	}
	
	public Material (java.lang.Integer id) {
		this.setId(id);
	}
	
	//还有产品用料表、反映产品结构的物料清单
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
	
	//serial
	public java.lang.String getSerial () {
		return serial;
	}

	public void setSerial (java.lang.String serial) {
		this.serial = serial;
	}
	
	//customer serial
	public java.lang.String getCustomerSerial () {
		return customerSerial;
	}

	public void setCustomerSerial (java.lang.String serial) {
		this.customerSerial = serial;
	}
	
	public String getAllSerial(){
		String s1 = StringUtils.isBlank(this.serial)?"":this.serial;
		String s2 = StringUtils.isBlank(this.customerSerial)?"":this.customerSerial;
		return s2 + " / " + s1;
	}
	
	public String getInfo(){
		String result = this.getAllSerial();
		if(this.name != null && !this.name.equals(""))
			result = result + " (" +this.name + ")";
		return result;
	}
	
	//leftNumber
	public java.lang.Double getLeftNumber () {
		return leftNumber == null?0:leftNumber;
	}

	public void setLeftNumber (java.lang.Double number) {
		this.leftNumber = number;
	}
	
	//not purchaseIn number
	public java.lang.Double getNotPurchaseInNumber () {
		/*Double sum = 0.00;
		for(OrderRecord record:records){
			if(record.getOrd().getType() == 1)
				sum += (record.getNumber() - record.getFinishNumber());
		}
		return sum;*/
		return this.notPurchaseInNumber==null?0:this.notPurchaseInNumber;
	}

	public void setNotPurchaseInNumber (java.lang.Double number) {
		this.notPurchaseInNumber = number;
	}
	
	//not sellOut number
	public java.lang.Double getNotSellOutNumber () {
		/*Double sum = 0.00;
		for(OrderRecord record:records){
			if(record.getOrd().getType() == 2)
				sum += (record.getNumber() - record.getFinishNumber());
		}
		return sum;*/
		return this.notSellOutNumber == null?0:this.notSellOutNumber;
	}

	public void setNotSellOutNumber (java.lang.Double number) {
		this.notSellOutNumber = number;
	}
		
	//unit
	public java.lang.String getUnit () {
		return unit;
	}

	public void setUnit (java.lang.String unit) {
		this.unit = unit;
	}
	
	public java.lang.String getSpec () {
		return spec;
	}

	public void setSpec (java.lang.String spec) {
		this.spec = spec;
	}
	
	public java.lang.String getDim() {
		return dim;
	}

	public void setDim (java.lang.String dim) {
		this.dim = dim;
	}

	//type
	public java.lang.Integer getType() {
		return type;
	}

	public void setType(java.lang.Integer type) {
		this.type = type;
	}
	
	//fetchType
	public java.lang.Integer getFetchType() {
		return fetchType;
	}

	public void setFetchType(java.lang.Integer fetchType) {
		this.fetchType = fetchType;
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	
	//parent
	public Category getParent () {
		return parent;
	}

	public void setParent (Category p) {
		this.parent = p;
	}
	
	//company
	public Company getCompany () {
		return company;
	}

	public void setCompany (Company p) {
		this.company = p;
	}
	
	//order record
	
	public Set<OrderRecord> getRecords(){
		return this.records;
	}
	
	public void setRecords(Set<OrderRecord> rs){
		this.records = rs;
	}
}
