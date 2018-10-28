package com.berp.mrp.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.entity.Company;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.RawBatch;
import com.berp.mrp.entity.RawBatchFlow;


@Service
@Transactional
public class RawBatchFlowDao extends HibernateBaseDao<RawBatchFlow, Integer> {
	
	public RawBatchFlow save(RawBatchFlow bean) {
		getSession().save(bean);
		return bean;
	}

	public RawBatchFlow findById(Integer id) { 
		RawBatchFlow entity = get(id);
		return entity;
	}

	public RawBatchFlow findByPlan(Plan plan) {
		return findUniqueByProperty("plan", plan);
	}
	
	public RawBatchFlow deleteById(Integer id) {
		RawBatchFlow entity = get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public Pagination getPage(Integer type, Integer status1, Integer status2, String name, Integer maxId, Integer pageNo, Integer pageSize) {
		Finder f = Finder.create("select bean from RawBatchFlow bean  where 1=1");
	
		if(type!=null)
		{
			f.append(" and bean.type=:type");
			f.setParam("type", type);
			/*
			if(type == 0)
				f.append(" and bean.plan.status=3");
			else
				f.append(" and bean.parent.plan.status=3");*/
		}
		if(status1!=null && status2 ==null){
			f.append(" and bean.status = :statuts1");
			f.setParam("status1", status1);
		}else if(status1!=null && status2!=null){
			f.append(" and bean.status between :status1 and :status2");
			f.setParam("status1", status1);
			f.setParam("status2", status2);
		}
		
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.material.name like :name or bean.material.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		
		if (maxId != null) {
			f.append(" and bean.id<:maxId");
			f.setParam("maxId", maxId);
		}
		
		f.append(" order by bean.id desc");
		if(maxId == null && pageNo !=null)
			return find(f, pageNo, pageSize);
		else
			return find(f, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List<RawBatchFlow> getList(Integer parentId, Integer type, Integer status1, Integer status2, Integer outsideCompanyId) {
		Finder f = Finder.create("select bean from RawBatchFlow bean where 1=1");
		if(type!=null)
		{
			f.append(" and bean.type=:type");
			f.setParam("type", type);
		}
		if(parentId!=null)
		{
			f.append(" and bean.parent.id=:parentId");
			f.setParam("parentId", parentId);
		}
		if(status1!=null && status2 ==null){
			f.append(" and bean.status =: statuts1");
			f.setParam("status1", status1);
		}else if(status1!=null && status2!=null){
			f.append(" and bean.status between :status1 and :status2");
			f.setParam("status1", status1);
			f.setParam("status2", status2);
		}
		if(outsideCompanyId!=null)
		{
			f.append(" and bean.cir.company.id=:outsideCompanyId");
			f.setParam("outsideCompanyId", outsideCompanyId);
		}
		return find(f);
	}
	
	public RawBatchFlow updateLeftNumber (Integer batchId, Double costNumber) throws Exception{
		RawBatchFlow flow = findById(batchId);
		Double leftNumber = flow.getLeftNumber();
		if(leftNumber == null || leftNumber - costNumber < 0)
			throw new Exception(flow.getSerial() + "剩余数量不足");
		flow.setLeftNumber(leftNumber - costNumber);
		return flow;
	}
	
	//这个暂时不用
	public RawBatchFlow updateArriveNumber (Integer parentFlowId, Double arriveNumber, Company outsideCompany) throws Exception{
		RawBatchFlow batch = findById(parentFlowId);
		Double notArriveNum = batch.getNotArriveNumber();
		if(notArriveNum == null || notArriveNum - arriveNumber < 0)
			throw new Exception(String.format("%s未回收数为%.2f%s，已超出其范围。", batch.getSerial(), notArriveNum, batch.getMaterial().getUnit()));
		
		List<RawBatchFlow> flows = this.getList(parentFlowId, 1, 1, 2, outsideCompany.getId());
		if(flows == null || flows.size() == 0){
			String text = String.format("'%s'加工%s的未回收数为0%s，已超出其范围。", outsideCompany.getName(), batch.getSerial(), batch.getMaterial().getUnit());
			throw new Exception(text);
		}
		
		//用于提示
		Double companyNotFinishNumber = 0.00;
		Double remainNumber = arriveNumber;
		for(int i = 0;i<flows.size() && remainNumber > 0;i++){
			RawBatchFlow flow = flows.get(i);
			
			companyNotFinishNumber += flow.getNotArriveNumber();
			
			Double finishNumber = flow.getArriveNumber();
			Double newNumber = finishNumber +remainNumber;
			
			if(newNumber<flow.getNumber()){
				//order.setStatus(Order.Status.partFinish.ordinal());
				flow.setArriveNumber(newNumber);
				flow.setStatus(2);
				remainNumber = 0.00;
			}
			else if(newNumber.equals(flow.getNumber())){
				flow.setArriveNumber(flow.getNumber());
				flow.setStatus(3);
				remainNumber = 0.00;
			}else{
				if(i < flows.size() - 1){
					flow.setArriveNumber(flow.getNumber());
					flow.setStatus(3);
					remainNumber = newNumber - flow.getNumber();
				}else{
					String text = String.format("'%s'加工%s的未回收数为%.2f%s，已超出其范围。", outsideCompany.getName(), batch.getSerial(), companyNotFinishNumber, batch.getMaterial().getUnit());
					throw new Exception(text);
				}
			}
			
			//更新第二级flow的cir状态
			cirDao.updateStatusByRawFlow(flow.getCir().getId());
		}
		
		batch.setArriveNumber(batch.getArriveNumber() + arriveNumber);
		//两个Double的对比要用equal...
		if(batch.getArriveNumber().equals(batch.getNumber()))
			batch.setStatus(3);
		else if(batch.getArriveNumber()<batch.getNumber()&&batch.getArriveNumber()>0)
			batch.setStatus(2);
		return batch;
	}
	
	//更新到达数量
	//注意下跟cirDao的分工，cirDao可能主要处理他底下的flow的更新，以及plan和step的更新，而rawBatchFlowDao处理相关联flow的更新
	//arriveNumber可以为负数，支持cancelApproval
	
	//主要用于outsideIn更新， 更新parent和step
	public RawBatchFlow updateArriveNumber (Integer parentFlowId, Double arriveNumber) throws Exception{
		
		//更新发货单记录
		RawBatchFlow flow = findById(parentFlowId);
		Double notArriveNum = flow.getNotArriveNumber();
		if(notArriveNum <0)
			throw new Exception(String.format("系统异常，%s的未到货数为负数。", flow.getMaterial().getName()));
		else if(arriveNumber > notArriveNum)
			throw new Exception(String.format("%s未回收数为%.2f%s，已超出其范围。", flow.getSerial(), notArriveNum, flow.getMaterial().getUnit()));
		else if(arriveNumber<notArriveNum){
			Double temp = flow.getArriveNumber() + arriveNumber;
			flow.setArriveNumber(temp);
			if(temp > 0)
				flow.setStatus(2);
			else 
				flow.setStatus(1);
		}
		else{
			flow.setArriveNumber(flow.getNumber());
			flow.setStatus(3);
		}
		//更新发货单
		cirDao.updateStatusByRawFlow(flow.getCir().getId());
				
		//更新爷爷节点flow的状态
	    /*RawBatchFlow flowParent = flow.getParent(); 
		flowParent.setArriveNumber(flowParent.getArriveNumber() + arriveNumber);
		//两个Double的对比要用equal...
		if(flowParent.getArriveNumber().equals(flowParent.getNumber()))
			flowParent.setStatus(3);
		else if(flowParent.getArriveNumber()<flowParent.getNumber()&&flowParent.getArriveNumber()>0)
			flowParent.setStatus(2);
		else
			flowParent.setStatus(1);*/
		
		PlanStep step = flow.getPlanStep(); 
		//step.setNumber(step.getNumber() + arriveNumber);
		step.setArriveNumber(step.getArriveNumber() + arriveNumber);
		//两个Double的对比要用equal...
		if(step.getArriveNumber().equals(step.getNumber()) && step.getArriveNumber()>= step.getPlan().getNumber())
			step.setStatus(3);
		else if(step.getArriveNumber()>0)
			step.setStatus(2);
		else
			step.setStatus(1);
		
		return flow;
	}
	
	@Override
	protected Class<RawBatchFlow> getEntityClass() {
		return RawBatchFlow.class;
	}
	
	@Autowired
	private CirDao cirDao;
}