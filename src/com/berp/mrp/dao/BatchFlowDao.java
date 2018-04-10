package com.berp.mrp.dao;

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
		getSession().save(bean);
		return bean;
	}
	
	public BatchFlow findById(Integer id) { 
		BatchFlow entity = get(id);
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
	
	public BatchFlow updateLeftNumber (Integer batchFlowId, Double costNumber) throws Exception{
		BatchFlow batch = findById(batchFlowId);
		
		//如果有setProperty的操作，都会反映到数据库
		//Updater有min mid max三种级别，通过级别来判断是否setProperty
		//batch.setNumPerBox(null);
		//batch.setBoxNum(null);
		
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