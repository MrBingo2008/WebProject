package com.berp.mrp.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.entity.User;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Plan;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class BatchFlowDao extends HibernateBaseDao<BatchFlow, Integer> {
	
	public BatchFlow save(BatchFlow bean) {
		getSession().saveOrUpdate(bean);
		return bean;
	}
	
	public BatchFlow findById(Integer id) { 
		BatchFlow entity = get(id);
		return entity;
	}
	
	public BatchFlow deleteById(Integer id) {
		BatchFlow entity = get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	@SuppressWarnings("unchecked")
	public List<BatchFlow> getList(Integer materialId, Integer direction, Integer status, Double lessLeftNumber, Integer planId, Integer type) {
		Finder f = Finder.create("select bean from BatchFlow bean where 1=1 ");
		
		if (materialId != null) {
			f.append(" and bean.material.id=:materialId");
			f.setParam("materialId", materialId);
		}

		if(direction!=null)
		{
			f.append(" and bean.direct=:direction");
			f.setParam("direction", direction);
		}
		
		if(status!=null){
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}
		
		if(lessLeftNumber!=null){
			f.append(" and bean.leftNumber>:lessLeftNumber");
			f.setParam("lessLeftNumber", lessLeftNumber);
		}
		
		if(planId!=null){
			f.append(" and bean.plan.id=:planId");
			f.setParam("planId", planId);
		}
		
		if(type!=null)
		{
			f.append(" and bean.type=:type");
			f.setParam("type", type);
		}
		
		return find(f);
	}
	
	//这个专门为stat设计的，因为设计到时间，所以需要关联到plan或cir，如果同时设定plan和cir的时间的话，需要plan和cir均不为空，无法满足这样的条件
	@SuppressWarnings("unchecked")
	public List<BatchFlow> getListByCir(Integer status, Date start, Date end) {
		Finder f = Finder.create("select bean from BatchFlow bean where bean.cir !=null ");
		if(status!=null){
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}
		
		if(start != null){
			f.append(" and bean.cir.billTime >=:start");
			f.setParam("start", start);
		}
				
		if(end != null){
			Calendar c = Calendar.getInstance();
			c.setTime(end);
			c.add(Calendar.DAY_OF_MONTH, 1);
			f.append(" and bean.cir.billTime <=:end");
			f.setParam("end", c.getTime());
		}
		return find(f);
	}
	
	@SuppressWarnings("unchecked")
	public List<BatchFlow> getListByPlan(Integer planId, Integer status, Date start, Date end) {
		Finder f = Finder.create("select bean from BatchFlow bean where bean.plan !=null ");

		if(planId!=null){
			f.append(" and bean.plan.id=:id");
			f.setParam("id", planId);
		}
		
		if(status!=null){
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}
		
		if(start != null){
			f.append(" and bean.plan.createTime >=:start");
			f.setParam("start", start);
		}
				
		if(end != null){
			Calendar c = Calendar.getInstance();
			c.setTime(end);
			c.add(Calendar.DAY_OF_MONTH, 1);
			f.append(" and bean.plan.createTime <=:end");
			f.setParam("end", c.getTime());
		}
		return find(f);
	}
	
	public BatchFlow updateLeftNumber (Integer batchFlowId, Double costNumber) throws Exception{
		BatchFlow batch = findById(batchFlowId);
		
		//如果有setProperty的操作，都会反映到数据库
		//Updater有min mid max三种级别，通过级别来判断是否setProperty
		//为什么这两行会被注释掉？
		//如果是弃核的话，会丢失这两个数据
		batch.setNumPerBox(null);
		batch.setBoxNum(null);
		
		Double leftNum = batch.getLeftNumber()==null?0:batch.getLeftNumber();
		if(leftNum == null || leftNum - costNumber < 0)
			throw new Exception(batch.getSerial() + "剩余数量不足");
		batch.setLeftNumber(leftNum - costNumber);
		return batch;
	}
	
	@Override
	protected Class<BatchFlow> getEntityClass() {
		return BatchFlow.class;
	}
}