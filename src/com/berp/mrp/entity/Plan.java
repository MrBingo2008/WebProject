package com.berp.mrp.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.berp.core.entity.Company;

public class Plan extends BaseBill {
	
	private Set<OrderRecord> sellRecords;
	private String ids;
	
	private Material material;
	private Double number;
	private Double packageNumber = 0.00;

	private List<PlanStep> steps;
	
	protected List<BatchFlow> flows;
	private List<BatchFlow> materialFlows;
	private List<BatchFlow> packageFlows;
	
	private List<Batch> batchs;
	
	//用来表示当前审核过的最近的实际生产数量
	//material approval后，就生成，step approval后, step.number会更新到raw batch flow里
	//plan in approval后，更新rawBatchFlow
	//弃核：
	//plan in cancel后 rawBatchFlow要设回step的值
	//step cancel approval后，要设回到上一个step的number值，如果没有上一个step，则设回到plan number
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
	
	//pacakge number
	public Double getPackageNumber() {
		return packageNumber;
	}

	public void setPackageNumber(Double packageNumber) {
		this.packageNumber = packageNumber;
	}
	
	public Double getCurrentNumber(){
		if(this.rawBatchFlow!=null)
			return rawBatchFlow.getNumber();
		else
			return number;
	}
	
	//sell records
	public Set<OrderRecord> getSellRecords() {
		return sellRecords;
	}

	public void setSellRecords(Set<OrderRecord> sellRecords) {
		this.sellRecords = sellRecords;
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
	
	//sell record infos: 用于对应的客户订单
	public String getSellRecordInfos(){
		if(sellRecords == null || sellRecords.size() == 0)
			return null;
		StringBuilder result = new StringBuilder();
		for(OrderRecord record: sellRecords){
			result.append(record.getFullInfo()+"<br>");
		}
		return result.toString();
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

	public PlanStep getPreStep(PlanStep step){
		Integer i = 0;
		boolean find = false;
		for(;i<steps.size();i++){
			if(steps.get(i).getId().equals(step.getId())){
				find=true;
				break;
			}
		}
		if(find && i > 0)
			return steps.get(i-1);
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
	//主要用于plan_detail.html获取和设置materialFlows，以及updateMaterial时用于获取旧的flow
	//如果materialFlows有值，优先考虑，否则再考虑flows的
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
	
	public String getStatusShow(){
		if(this.getStatus() > 0)
			return String.format("已审核, %f入库", this.getPackageNumber());
		else
			return "未审核";
	}
}
