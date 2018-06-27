package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.berp.core.entity.Company;
import com.berp.framework.util.StrUtils;
import com.berp.mrp.entity.Cir.CirType;

public class Order extends BaseBill {
	
	private Integer direction=1;
	
	private Date deadlineTime;
	private Company company;
	private List<OrderRecord> records;
	//private Set<Order> purchaseOrders;
	
	public static enum Status{notApproval, approval, partFinish, finish};
	
	public Order(){
	}
	
	public Order (java.lang.Integer id) {
		this.setId(id);
	}
	
	//direction
	public java.lang.Integer getDirection() {
		return direction;
	}

	public void setDirection(java.lang.Integer direction) {
		this.direction = direction;
	}
	
	//deadline
	public Date getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(Date deadlineTime) {
		this.deadlineTime = deadlineTime;
	}
	
	//company
	public Company getCompany(){
		return this.company;
	}
	
	public void setCompany(Company company){
		this.company = company;
	}
	
	//records	
	public List<OrderRecord> getRecords(){
		return this.records;
	}
	
	public void setRecords(List<OrderRecord> rs){
		this.records = rs;
	}
	
	//items
	public String getItems(){
		StringBuilder sb = new StringBuilder();
		if(this.records != null && !this.records.isEmpty())
			for(OrderRecord record : this.records){
				String tempName = StrUtils.getFixedLenString(record.getMaterial().getName(), 10, "&nbsp;&nbsp;");
				sb.append(String.format("%s - %.0f (%.0f) %s; ", tempName, record.getNumber(), record.getFinishNumber(), record.getMaterial().getUnit()));
		}
		return sb.toString();
	}

	public String getInfo(){
		String temp = this.getName();
		if(StringUtils.isBlank(temp))
			temp = this.getSerial();
		return String.format("%s / %s", temp, this.company.getName());
	}
	
	public String getDisplay(){
		if(StringUtils.isBlank(this.getName()))
			return this.getSerial();
		return this.getName();
	}
	
	/*
	//purchase orders
	public Set<Order> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(Set<Order> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}
	
	public String getPurchaseOrderSerials(){
		StringBuilder serials = new StringBuilder();
		if(purchaseOrders!=null){
			for(Order pOrder : this.purchaseOrders){
				if(pOrder.getStatus() == 1)
					serials.append(pOrder.getSerial() + " ");
			}
		}
		return serials.toString();
	}*/

}
