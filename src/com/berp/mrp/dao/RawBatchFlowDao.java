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

	public Pagination getPage(Integer type, Integer status1, Integer status2, String name, Integer pageNo, Integer pageSize) {
		Finder f = Finder.create("select bean from RawBatchFlow bean  where 1=1");
	
		if(type!=null)
		{
			f.append(" and bean.type=:type");
			f.setParam("type", type);
		}
		if(status1!=null && status2 ==null){
			f.append(" and bean.status =: statuts1");
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
		
		f.append(" order by bean.id desc");
		
		return find(f, pageNo, pageSize);
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
	
	//更新前两级的flow
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
	
	@Override
	protected Class<RawBatchFlow> getEntityClass() {
		return RawBatchFlow.class;
	}
	
	@Autowired
	private CirDao cirDao;
}