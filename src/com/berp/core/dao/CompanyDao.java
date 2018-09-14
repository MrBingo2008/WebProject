package com.berp.core.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.entity.Category;
import com.berp.core.entity.Company;
import com.berp.core.entity.User;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Cir;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class CompanyDao extends HibernateBaseDao<Company, Integer> {
	
	public Company findById(Integer id) { 
		Company entity = get(id);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List getList() {
		String hql = "from Company";
		return find(hql);
	}	
	
	//@Transactional
	public Company save(Company bean) {
		getSession().save(bean);
		return bean;
	}

	public Company update(Company bean){
		Updater<Company> updater = new Updater<Company>(bean);
		bean = updateByUpdater(updater);
		return bean;
	}
	
	public Company deleteById(Integer id) {
		Company entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public Pagination getPage(Integer parentId, String name, Integer pageNo, Integer pageSize) {
		//stone: 注意，要多个select bean，要不然选出来有category
		Finder f = Finder.create("select bean from Company bean ");
		
		if (parentId != null) {
			f.append(" join bean.parent category, Category parent");
			f.append(" where category.lft between parent.lft and parent.rgt");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", parentId);
		} else {
			f.append(" where 1=1");
		}
	
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and bean.name like :name");
			f.setParam("name", "%" + name + "%");
		}
		
		f.append(" order by bean.id desc");
		
		return find(f, pageNo, pageSize);
	}
	
	@Override
	protected Class<Company> getEntityClass() {
		return Company.class;
	}
}