package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;
import com.berp.mrp.entity.Cir.CirType;

public class Order extends BaseBill {
	
	private Integer direction=1;
	
	private Date deadlineTime;
	private Company company;
	private List<OrderRecord> records;
	
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
				sb.append(String.format("%s - %.0f%s; ",record.getMaterial().getInfo(), record.getNumber(), record.getMaterial().getUnit()));
		}
		return sb.toString();
	}

}
