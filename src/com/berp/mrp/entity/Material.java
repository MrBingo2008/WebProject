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
	private Step surface;

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
		if(StringUtils.isBlank(name))
			return "";
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
		return String.format("%s / %s", this.customerSerial==null?"":this.customerSerial, this.serial==null?"":this.serial);
	}
	
	public String getInfo(){
		if(!StringUtils.isBlank(this.serial))
			return String.format("%s ( %s )", this.getName(),this.serial);
		if(!StringUtils.isBlank(this.dim))
			return String.format("%s ( %s )", this.getName(),this.dim);
		if(!StringUtils.isBlank(this.customerSerial))
			return String.format("%s ( %s )", this.getName(),this.customerSerial);
		return this.getName();
		
		/*String result = this.getAllSerial();
		if(this.name != null && !this.name.equals(""))
			result = result + " (" +this.name + ")";
		return result;*/
	}
	
	public String getFullShowName(){
		String result = "";
		if(!StringUtils.isBlank(this.name))
			result = this.name;
		else if(!StringUtils.isBlank(this.customerSerial))
			result = this.customerSerial;
		else
			result = this.serial;
		return result;
	}
	
	public String getShowName(){
		String result = this.getFullShowName();
		if(!StringUtils.isBlank(result) && result.length()>12)
			result = String.format("%s..", result.substring(0, 12));
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
	
	//surface
	public Step getSurface() {
		return surface;
	}

	public void setSurface(Step surface) {
		this.surface = surface;
	}
	
	//order record
	public Set<OrderRecord> getRecords(){
		return this.records;
	}
	
	public void setRecords(Set<OrderRecord> rs){
		this.records = rs;
	}
	
	//for stat
	private Double purchaseOrder = 0.00;
	private Double sellOrder = 0.00;
	private Double purchaseIn = 0.00;
	private Double purchaseBack = 0.00;
	private Double sellOut = 0.00;
	private Double sellBack=0.00;
	private Double planMaterial = 0.00;
	private Double planIn = 0.00;
	private Double checkIn = 0.00;
	private Double checkOut = 0.00;
	
	public Double getPlanMaterial() {
		return planMaterial;
	}

	public void setPlanMaterial(Double planMaterial) {
		this.planMaterial = planMaterial;
	}

	public Double getPlanIn() {
		return planIn;
	}

	public void setPlanIn(Double planIn) {
		this.planIn = planIn;
	}

	public Double getCheckIn() {
		return checkIn;
	}

	public void setCheckIn(Double checkIn) {
		this.checkIn = checkIn;
	}

	public Double getCheckOut() {
		return checkOut;
	}

	public void setCheckOut(Double checkOut) {
		this.checkOut = checkOut;
	}

	public Double getPurchaseOrder(){
		return purchaseOrder;
	}
	
	public void setPurchaseOrder(Double p){
		purchaseOrder = p;
	}

	public Double getSellOrder() {
		return sellOrder;
	}

	public void setSellOrder(Double sellOrder) {
		this.sellOrder = sellOrder;
	}

	public Double getPurchaseIn() {
		return purchaseIn;
	}

	public void setPurchaseIn(Double purchaseIn) {
		this.purchaseIn = purchaseIn;
	}

	public Double getPurchaseBack() {
		return purchaseBack;
	}

	public void setPurchaseBack(Double purchaseBack) {
		this.purchaseBack = purchaseBack;
	}

	public Double getSellOut() {
		return sellOut;
	}

	public void setSellOut(Double sellOut) {
		this.sellOut = sellOut;
	}

	public Double getSellBack() {
		return sellBack;
	}

	public void setSellBack(Double sellBack) {
		this.sellBack = sellBack;
	}
}
