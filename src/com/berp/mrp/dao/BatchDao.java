package com.berp.mrp.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.entity.User;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Batch;
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
public class BatchDao extends HibernateBaseDao<Batch, Integer> {
	
	public Batch save(Batch bean) {
		getSession().save(bean);
		return bean;
	}
	
	public Batch findById(Integer id) { 
		Batch entity = get(id);
		return entity;
	}
	
	public Batch findByPlan(Plan plan) {
		return findUniqueByProperty("plan", plan);
	}
	
	@SuppressWarnings("unchecked")
	public List<Batch> getList(Integer materialId) {
		Finder f = Finder.create("select bean from Batch bean");
		
		if (materialId != null) {
			f.append(" where bean.material.id=:materialId");
			f.setParam("materialId", materialId);
		} else {
			f.append(" where 1=1");
		}
		return find(f);
	}
	
	public Batch updateLeftNumber (Integer batchId, Double costNumber) throws Exception{
		Batch batch = findById(batchId);
		Double leftNum = batch.getLeftNum();
		if(leftNum == null || leftNum - costNumber < 0)
			throw new Exception(batch.getSerial() + "剩余数量不足");
		batch.setLeftNum(leftNum - costNumber);
		return batch;
	}
	
	@Override
	protected Class<Batch> getEntityClass() {
		return Batch.class;
	}
}