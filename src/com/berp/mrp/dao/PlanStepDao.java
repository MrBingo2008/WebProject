package com.berp.mrp.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
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
	
	//采用id, findById(id)来更新，比较合理
	//如果采用PlanStep bean, Updater的话，那调用这个函数时可能先findById，那么session里会不会有两个bean，从而造成冲突
	public PlanStep updateNumber(Integer id, Double deltaNumber, Double deltaArriveNumber){
		
		PlanStep bean = this.findById(id);
		
		bean.setNumber(bean.getNumber() + deltaNumber);
		bean.setArriveNumber(bean.getArriveNumber() + deltaArriveNumber);
		
		return bean;
	}
	
	public PlanStep updateFinish(PlanStep bean, int stepIndex){
		
		Updater<PlanStep> updater = new Updater<PlanStep>(bean);
		updater.setUpdateMode(Updater.UpdateMode.MIN);
		updater.include("finishTime");
		updater.include("number");
		updater.include("weight");
		updater.include("status");
		bean = updateByUpdater(updater);
		
		return bean;
	}
	
	//maxId和pageNum，只能选一个，而且如果是maxId的话，有可能刚开始时是null，所以最后要判断pageNum, 这个设计不大合理
	//专门用于outsideOut not finish吧？
	public Pagination getPage(Integer type, String name, Integer status, Integer status1, Boolean notFinish, Integer maxId, Integer pageNo, Integer pageSize) {
		
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("select bean from PlanStep bean where 1=1");
		if (type != null) {
			f.append(" and bean.step.type=:type");
			f.setParam("type", type);
		}
		if (maxId != null) {
			f.append(" and bean.id<:maxId");
			f.setParam("maxId", maxId);
		}
		
		if(notFinish != null && notFinish == true){
			f.append(" and bean.number < bean.plan.number");
		}
		
		//notEmpty 包括null和""，notBlank还包括" "
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.plan.name like :name or bean.plan.serial like :name or bean.plan.material.serial like :name or bean.plan.material.customerSerial like :name or bean.plan.material.name like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		if (status != null && status1 == null) {
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}else if(status!=null && status1!=null){
			f.append(" and bean.status>=:status and bean.status <=:status1");
			f.setParam("status", status);
			f.setParam("status1", status1);
		}
		
		f.append(" order by bean.id desc");
		if(maxId == null && pageNo !=null)
			return find(f, pageNo, pageSize);
		else
			return find(f, pageSize);
	}
	
	@Override
	protected Class<PlanStep> getEntityClass() {
		return PlanStep.class;
	}
	
	@Autowired
	private BatchDao batchDao;
}