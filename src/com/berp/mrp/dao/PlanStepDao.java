package com.berp.mrp.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.Step;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class PlanStepDao extends HibernateBaseDao<PlanStep, Integer> {
	
	public PlanStep findById(Integer id) { 
		PlanStep entity = get(id);
		return entity;
	}
	
	public PlanStep updateFinish(PlanStep bean, int stepIndex){
		
		Updater<PlanStep> updater = new Updater<PlanStep>(bean);
		updater.setUpdateMode(Updater.UpdateMode.MIN);
		updater.include("finishTime");
		updater.include("number");
		updater.include("weight");
		updater.include("status");
		bean = updateByUpdater(updater);
		/*
		Plan plan = bean.getPlan();
		
		if(stepIndex == 0){
			Batch batch = new Batch(plan.getSerial(), bean.getNumber(), plan.getMaterial(), plan);
			batchDao.save(batch);
		}else
		{
			Batch batch = batchDao.findByPlan(plan);
			batch.setNumber(bean.getNumber());
		}*/
		
		return bean;
	}
	
	@Override
	protected Class<PlanStep> getEntityClass() {
		return PlanStep.class;
	}
	
	@Autowired
	private BatchDao batchDao;
}