package com.berp.mrp.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Step;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class StepDao extends HibernateBaseDao<Step, Integer> {
	
	public Step findById(Integer id) { 
		Step entity = get(id);
		return entity;
	}

	//@Transactional
	public Step save(Step bean) {
		getSession().save(bean);
		return bean;
	}

	public Step update(Step bean){
		Updater<Step> updater = new Updater<Step>(bean);
		bean = this.updateByUpdater(updater);
		return bean;
	}
	
	public Step deleteById(Integer id) {
		Step entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public Pagination getPage(String name, Integer pageNo, Integer pageSize, Boolean surface) {
		Finder f = Finder.create("select bean from Step bean where 1=1");
		if(name != null && name != "")
		{
			f.append(" and (bean.name like :name or bean.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		if(surface!=null)
		{
			f.append(" and bean.surface=:surface");
			f.setParam("surface", surface);
		}
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List getList(String name) {
		Finder f = Finder.create("select bean from Step bean where 1=1");
		if(name != null && name != "")
		{
			f.append(" and (bean.name like :name or bean.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		return find(f);
	}
	
	@Override
	protected Class<Step> getEntityClass() {
		return Step.class;
	}
}