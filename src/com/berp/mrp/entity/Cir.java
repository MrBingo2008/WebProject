package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;

public class Cir extends BaseBill{
	
	private Integer direction = 1;
	private Order order;
	
	protected List<BatchFlow> flows;
	protected List<RawBatchFlow> rawFlows;
	
	private Company company;
	
	public static enum CirType{
		purchaseIn, purchaseBack, sellOut, sellBack, outsideOut, outsideIn, checkIn, checkOut;
		public static CirType getCirType(Integer index){
			for (CirType c : CirType.values()) {  
	            if (c.ordinal() == index) {  
	                return c;  
	            }  
	        }
			return null;
		}
	}
	
	public Cir(){
	}
	
	public Cir (java.lang.Integer id) {
		this.setId(id);
	}

	//order
	public Order getOrder(){
		return this.order;
	}
	
	public void setOrder(Order order){
		this.order = order;
	}
	
	//company
	public Company getCompany(){
		return this.company;
	}
	
	public void setCompany(Company company){
		this.company = company;
	}
	
	//direction
	public java.lang.Integer getDirection() {
		return direction;
	}

	public void setDirection(java.lang.Integer direction) {
		this.direction = direction;
	}
	
	//flows
	public List<BatchFlow> getFlows(){
		return this.flows;
	}
	
	public void setFlows(List<BatchFlow> fs){
		this.flows = fs;
	}
	
	//rawFlows
	public List<RawBatchFlow> getRawFlows(){
		return this.rawFlows;
	}
	
	public void setRawFlows(List<RawBatchFlow> fs){
		this.rawFlows = fs;
	}

	//items
	public String getItems(){
		StringBuilder sb = new StringBuilder();
		if(this.getType().equals(CirType.outsideIn.ordinal()) || this.getType().equals(CirType.outsideOut.ordinal())){
			if(this.rawFlows != null && !this.rawFlows.isEmpty())
				for(RawBatchFlow flow : this.rawFlows){
					sb.append(String.format("%s - %.0f%s; ",flow.getMaterial().getNameSpec(), flow.getNumber(), flow.getMaterial().getUnit()));
				}
			
		}else
		{
			if(this.flows != null && !this.flows.isEmpty())
				for(BatchFlow flow : this.flows){
					sb.append(String.format("%s - %.0f%s; ",flow.getMaterial().getNameSpec(), flow.getNumber(), flow.getMaterial().getUnit()));
				}
		}
		return sb.toString();
	}
}
