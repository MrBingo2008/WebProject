package com.berp.mrp.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;

public class Plan extends BaseBill {
	
	private OrderRecord orderRecord;
	private Material material;
	private Double number;
	
	private List<PlanStep> steps;
	
	protected List<BatchFlow> flows;
	private List<BatchFlow> materialFlows;
	private List<BatchFlow> packageFlows;
	
	private List<Batch> batchs;
	
	private RawBatchFlow rawBatchFlow;

	public static enum Status{edit, approval, materialFinish, outside, manuFinish, packageFinish};
	
	public Plan(){
	}
	
	//material
	public Material getMaterial(){
		return this.material;
	}
	
	public void setMaterial(Material material){
		this.material = material;
	}
	
	//number
	public void setNumber (java.lang.Double num) {
		this.number = num;
	}
	
	public java.lang.Double getNumber () {
		return number;
	}
	
	public Double getCurrentNumber(){
		if(this.rawBatchFlow!=null)
			return rawBatchFlow.getNumber();
		else
			return number;
	}
	
	//orderRecord
	public OrderRecord getRecord(){
		return this.orderRecord;
	}
	
	public void setRecord(OrderRecord orderRecord){
		this.orderRecord = orderRecord;
	}
	
	//steps
	public List<PlanStep> getSteps(){
		return this.steps;
	}
	
	public void setSteps(List<PlanStep> steps){
		this.steps = steps;
	}
	
	//current step
	public PlanStep getCurrentStep(){
		for(PlanStep step: steps){
			if(step.getStatus() == PlanStep.Status.notFinish.ordinal())
				return step;
		}
		return null;
	}
	
	public PlanStep getNextStep(PlanStep step){
		Integer i = 0;
		boolean find = false;
		for(;i<steps.size();i++){
			if(steps.get(i).getId().equals(step.getId())){
				find=true;
				break;
			}
		}
		if(find && i < steps.size() -1)
			return steps.get(i+1);
		return null;
	}

	//flows
	public List<BatchFlow> getFlows(){
		return this.flows;
	}
	
	public void setFlows(List<BatchFlow> fs){
		this.flows = fs;
	}
	
	//materialFlows
	public List<BatchFlow> getMaterialFlows(){
		if(materialFlows == null && flows!=null){
			materialFlows = new ArrayList<BatchFlow>();
			for(BatchFlow flow: flows){
				if(flow.getType() == BatchFlow.Type.planMaterial.ordinal())
					materialFlows.add(flow);
			}
		}
		return materialFlows;
	}
	
	public void setMaterialFlows(List<BatchFlow> fs){
		this.materialFlows = fs;
	}
	
	//packageFlows
	public List<BatchFlow> getPackageFlows(){
		if(packageFlows == null && flows!=null){
			packageFlows = new ArrayList<BatchFlow>();
			for(BatchFlow flow: flows){
				if(flow.getType() == BatchFlow.Type.planIn.ordinal())
					packageFlows.add(flow);
			}
		}
		return packageFlows;
	}
	
	public void setPackageFlows(List<BatchFlow> fs){
		this.packageFlows = fs;
	}
	
	//batchs
	public List<Batch> getBatchs(){
		return this.batchs;
	}
	
	public void setBatchs(List<Batch> bs){
		this.batchs = bs;
	}
	
	//rawBatchFlow
	public RawBatchFlow getRawBatchFlow(){
		return this.rawBatchFlow;
	}
	
	public void setRawBatchFlow(RawBatchFlow rb){
		this.rawBatchFlow = rb;
	}
	
	public String getDetail(){
		return String.format("%s:%s:%s", this.getMaterial().getAllSerial(), this.getStatus(), this.getNumber());
	}
}
