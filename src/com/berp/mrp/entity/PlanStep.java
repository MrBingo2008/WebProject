package com.berp.mrp.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.berp.core.entity.Company;

public class PlanStep {
	private Integer id;
	private Integer priority;
	private String name;
	private Integer type = 0;
	
	private Integer status = 0;
	private Plan plan;
	private Step step;
	
	private java.util.Date finishTime;
	private Double number;
	private Double arriveNumber;

	private Set<RawBatchFlow> rawFlows;
	private List<PlanStepNumber> stepNumbers;

	public static enum Status{notFinish, finish};
	
	public PlanStep(){
	}
	
	public PlanStep (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}
	
	//priority
	public java.lang.Integer getPriority () {
		return priority;
	}
	
	public void setPriority (java.lang.Integer priority) {
		this.priority = priority;
	}

	//name
	public java.lang.String getName () {
		return name;
	}

	public void setName (java.lang.String name) {
		this.name = name;
	}
	
	//type
	public java.lang.Integer getType () {
		return type;
	}

	public void setType (java.lang.Integer type) {
		this.type = type;
	}
	
	//status
	public java.lang.Integer getStatus() {
		return status;
	}

	public void setStatus(java.lang.Integer status) {
		this.status = status;
	}
	
	//plan
	public Plan getPlan(){
		return this.plan;
	}
	
	public void setPlan(Plan plan){
		this.plan = plan;
	}
	
	//finish time
	public java.util.Date getFinishTime () {
		return finishTime;
	}

	public void setFinishTime (java.util.Date time) {
		this.finishTime = time;
	}
	
	//number
	public java.lang.Double getNumber() {
		//如果加if(number==null) number = 0.00,这样会反映到数据库
		if(number==null) 
			number = 0.00;
		return number;
	}

	public void setNumber(java.lang.Double number) {
		this.number = number;
	}
	
	//for outside step
	public Double getArriveNumber() {
		if(arriveNumber == null)
			arriveNumber = 0.00;
		return arriveNumber;
	}

	public void setArriveNumber(Double arriveNumber) {
		this.arriveNumber = arriveNumber;
	}
	
	public Double getNotOutNumber(){
		return this.getPlan().getNumber() - this.getNumber();
	}
	
	public Double getNotArriveNumber(){
		return this.getNumber() - this.getArriveNumber();
	}
	
	//step
	public Step getStep() {
		return step;
	}

	public void setStep(Step step) {
		this.step = step;
	}
	
	public Boolean getIsLastApply(){
		List<PlanStep> steps = plan.getSteps();
		int currentIndex = 0;
		for(int i=0;i<steps.size();i++){
			if(steps.get(i).getStatus() == PlanStep.Status.notFinish.ordinal())
			{
				currentIndex = i;
				break; 
			}
		}
		if(currentIndex > 0)
			currentIndex --;
		else//没找到，currentIndex = 0，就是全部都完成，就选最后一个
			currentIndex = steps.size() - 1;
		if(steps.get(currentIndex).getId().equals(this.getId()))
			return true;
		else
			return false;
	}
	
	//raw flows
	public Set<RawBatchFlow> getRawFlows() {
		return rawFlows;
	}

	public void setRawFlows(Set<RawBatchFlow> rawFlows) {
		this.rawFlows = rawFlows;
	}
	
	//plan step numbers
	public List<PlanStepNumber> getStepNumbers() {
		return stepNumbers;
	}

	public void setStepNumbers(List<PlanStepNumber> stepNumbers) {
		this.stepNumbers = stepNumbers;
	}

}
