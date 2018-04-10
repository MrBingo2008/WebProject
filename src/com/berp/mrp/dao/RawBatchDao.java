package com.berp.mrp.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.RawBatch;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class RawBatchDao extends HibernateBaseDao<RawBatch, Integer> {
	
	public RawBatch save(RawBatch bean) {
		getSession().save(bean);
		return bean;
	}
	
	public RawBatch findById(Integer id) { 
		RawBatch entity = get(id);
		return entity;
	}
	
	public RawBatch findByPlan(Plan plan) {
		return findUniqueByProperty("plan", plan);
	}
	
	@SuppressWarnings("unchecked")
	public List<RawBatch> getList() {
		Finder f = Finder.create("select bean from RawBatch bean");
		/*
		if (materialId != null) {
			f.append(" where bean.material.id=:materialId");
			f.setParam("materialId", materialId);
		} else {
			f.append(" where 1=1");
		}*/
		return find(f);
	}
	
	public RawBatch updateLeftNumber (Integer batchId, Double costNumber) throws Exception{
		RawBatch batch = findById(batchId);
		Double leftNum = batch.getLeftNum();
		if(leftNum == null || leftNum - costNumber < 0)
			throw new Exception(batch.getSerial() + "剩余数量不足");
		batch.setLeftNum(leftNum - costNumber);
		return batch;
	}
	
	public RawBatch updateArriveNumber (Integer batchId, Double arriveNumber) throws Exception{
		RawBatch batch = findById(batchId);
		Double notArriveNum = batch.getNotArriveNum();
		if(notArriveNum == null || notArriveNum - arriveNumber < 0)
			throw new Exception(batch.getSerial() + "超出未归还数量");
		batch.setArriveNum(batch.getArriveNum() + arriveNumber);
		return batch;
	}
	
	@Override
	protected Class<RawBatch> getEntityClass() {
		return RawBatch.class;
	}
}