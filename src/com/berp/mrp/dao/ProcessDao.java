package com.berp.mrp.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Process;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class ProcessDao extends HibernateBaseDao<Process, Integer> {
	
	public Process findById(Integer id) { 
		Process entity = get(id);
		return entity;
	}

	//@Transactional
	public Process save(Process bean) {
		getSession().save(bean);
		return bean;
	}

	public Process update(Process bean){
		Updater<Process> updater = new Updater<Process>(bean);
		bean = updateByUpdater(updater);
		
		String hql = "delete from ProcessStep bean where bean.process.id is null";
		getSession().createQuery(hql).executeUpdate();
		return bean;
	}
	
	public Process deleteById(Integer id) {
		Process entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public List<Process> getListByCategory(Integer categoryId){
		Finder f = Finder.create("select bean from Process bean where 1=1");
		if(categoryId!=null)
		{
			f.append(" and bean.category.id=:categoryId");
			f.setParam("categoryId", categoryId);
		}
		return find(f);
	}
	
	public Pagination getPage(String name, Integer pageNo, Integer pageSize) {
		
		pageNo = pageNo == null?1:pageNo;
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("from Process bean where 1=1");
		if(name != null && name != "")
		{
			f.append(" and (bean.name like :name or bean.serial like :name) ");
			f.setParam("name", name);
		}
		f.append(" order by bean.id desc");
		return find(f, pageNo, pageSize);
	}
	
	@Override
	protected Class<Process> getEntityClass() {
		return Process.class;
	}
}