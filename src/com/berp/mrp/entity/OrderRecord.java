package com.berp.mrp.entity;

import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.berp.framework.util.StrUtils;
import com.berp.mrp.entity.Process;

public class OrderRecord {
	private Integer id;
	private Double number;
	private Double finishNumber=0.00;
	private Integer status=0;
	
	private Material material;
	private Date deadlineTime;
	
	private Step surface;
	private Order order;

	private Set<BatchFlow> flows;
	//用于表示sellRecords的id参数传递
	private String ids;
	private Set<OrderRecord> sellRecords;
	private Set<OrderRecord> purchaseRecords;
	private Set<Plan> plans;
	
	public OrderRecord(){
		
	}
	
	public OrderRecord (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.Double getNumber () {
		return number;
	}

	public void setNumber (java.lang.Double number) {
		this.number = number;
	}
	
	//
	public java.lang.Double getFinishNumber () {
		return finishNumber==null?0:finishNumber;
	}

	public void setFinishNumber (java.lang.Double num) {
		this.finishNumber = num;
	}

	public Double getNotFinishNumber(){
		return this.getNumber() - this.getFinishNumber();
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	
	//material
	public Material getMaterial () {
		return material;
	}

	public void setMaterial (Material m) {
		this.material = m;
	}
	
	//deadline
	public Date getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(Date deadlineTime) {
		this.deadlineTime = deadlineTime;
	}
	
	//surface
	public Step getSurface() {
		return surface;
	}

	public void setSurface(Step surface) {
		this.surface = surface;
	}
	
	//get default surface主要用于判断
	public Integer getDefaultSurfaceId(){
		if(this.surface!=null)
			return surface.getId();
		else if(this.getMaterial().getSurface()!=null)
			return this.getMaterial().getSurface().getId();
		else 
			return null;
		
	}
	
	public Order getOrd () {
		return order;
	}

	public void setOrd (Order order) {
		this.order = order;
	}
	
	//flows
	public Set<BatchFlow> getFlows() {
		return flows;
	}

	public void setFlows(Set<BatchFlow> flows) {
		this.flows = flows;
	}
	
	//sell records
	public Set<OrderRecord> getSellRecords() {
		return sellRecords;
	}

	public void setSellRecords(Set<OrderRecord> sellRecords) {
		this.sellRecords = sellRecords;
	}
	
	//purchase records
	public Set<OrderRecord> getPurchaseRecords() {
		return purchaseRecords;
	}

	public void setPurchaseRecords(Set<OrderRecord> purchaseRecords) {
		this.purchaseRecords = purchaseRecords;
	}

	//用于订单弃核时，判断是否还有关联的采购订单
	public String getPurchaseOrderSerials(){
		if(purchaseRecords == null || purchaseRecords.size() == 0)
			return null;
		StringBuilder result = new StringBuilder();
		for(OrderRecord record: purchaseRecords){
			result.append(record.getOrd().getSerial()+" ");
		}
		return result.toString();
	}
	
	//plans
	public Set<Plan> getPlans(){
		return this.plans;
	}
	
	public void setPlans(Set<Plan> plans){
		this.plans = plans;
	}
	
	/*public String getPlansDetail(){
		StringBuilder result = new StringBuilder();
		for(Plan plan : plans){
			result.append(plan.getDetail()+"<br>");
		}
		return result.toString();
	}*/
	
	public String getPlanSerials(){
		StringBuilder result = new StringBuilder();
		for(Plan plan : plans){
			result.append(plan.getSerial() + " ");
		}
		return result.toString();
	}
	
	//sell records ids
	public String getIds() {
		//两个地方会调用到，一个是init detail时，需要获取，一个是save或update order时，需要获取
		if(StringUtils.isBlank(this.ids) && sellRecords != null){
			String result = new String();
			for(OrderRecord record: sellRecords){
				String id = record.getId().toString();
				result = StringUtils.isBlank(result)?id:result + "," + id;
			}
			return result;
		}
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
	//sell record infos: 用于原料对应的客户订单
	public String getSellRecordInfos(){
		if(sellRecords == null || sellRecords.size() == 0)
			return null;
		StringBuilder result = new StringBuilder();
		for(OrderRecord record: sellRecords){
			result.append(record.getFullInfo()+"<br>");
		}
		return result.toString();
	}
	
	public String getCirSerials(){
		StringBuilder result = new StringBuilder();
		for(BatchFlow flow : flows){
			result.append(flow.getCir().getSerial() + " ");
		}
		return result.toString();
	}
	
	public String getInfo(){
		return String.format("%s / %s", this.getMaterial().getNameSpec(), this.getOrd().getInfo());
	}
	
	public String getFullInfo(){
		return String.format("%s / %s%s / %s", this.getMaterial().getNameSpec(), StrUtils.doubleTrans(this.getNumber()), this.getMaterial().getUnit(), this.getOrd().getInfo());
	}
}
