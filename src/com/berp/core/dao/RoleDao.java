package com.berp.core.dao;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.entity.Category;
import com.berp.core.entity.Role;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.hibernate3.Updater.UpdateMode;
import com.berp.framework.page.Pagination;
import com.berp.framework.security.BadCredentialsException;
import com.berp.framework.security.PwdEncoder;
import com.berp.framework.security.UsernameNotFoundException;
import com.berp.framework.web.session.SessionProvider;

@Service
@Transactional
public class RoleDao extends HibernateBaseDao<Role, Integer> {
	
	public Role findById(Integer id) { 
		Role entity = get(id);
		return entity;
	}
	
	public Role save(Role bean, Set<String> perms) {
		if(bean.getSuper())
			perms.clear();
		
		bean.setPerms(perms);
		getSession().save(bean);
		return bean;
	}

	public Role update(Role bean, Set<String> perms) {
		if(bean.getSuper())
			perms.clear();
		
		Updater<Role> updater = new Updater<Role>(bean);
		updater.setUpdateMode(UpdateMode.MAX);
		bean = updateByUpdater(updater);
		bean.setPerms(perms);
		return bean;
	}
	
	public Role deleteById(Integer id) {
		Role entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public Pagination getPage(Integer pageNo, Integer pageSize) {
		
		pageNo = pageNo == null?1:pageNo;
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("from Role bean order by bean.id asc");
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getList() {
		Finder f = Finder.create("from Role");
		return find(f);
	}	
	
	@Override
	protected Class<Role> getEntityClass() {
		return Role.class;
	}
	
	@Autowired
	private PwdEncoder pwdEncoder;
}