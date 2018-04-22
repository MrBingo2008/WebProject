package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;
import com.berp.framework.util.StrUtils;
import com.berp.mrp.entity.Cir.CirType;

public class Order extends BaseBill {
	
	private Integer direction=1;
	
	private Date deadlineTime;
	private Company company;
	private List<OrderRecord> records;
	private Order sellOrder;
	private Set<Order> purchaseOrders;
	
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

	//sell order
	public Order getSellOrder() {
		return sellOrder;
	}

	public void setSellOrder(Order sellOrder) {
		this.sellOrder = sellOrder;
	}
	
	//purchase orders
	public Set<Order> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(Set<Order> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}

}
