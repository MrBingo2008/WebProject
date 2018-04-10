package com.berp.core.dao;

import java.util.List;

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
import com.berp.core.entity.User;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.framework.security.BadCredentialsException;
import com.berp.framework.security.PwdEncoder;
import com.berp.framework.security.UsernameNotFoundException;
import com.berp.framework.web.session.SessionProvider;


@Service
@Transactional
public class UserDao extends HibernateBaseDao<User, Integer> {
	
	//用于session记录信息
	public static final String USER_ID = "user_id";
	
	public User findById(Integer id) { 
		User entity = get(id);
		return entity;
	}

	public User findByUsername(String username) {
		return findUniqueByProperty("name", username);
	}
	
	public User save(User bean, Integer [] roleIds) {
		this.resetPassword(bean, bean.getPassword());
		getSession().save(bean);
		if(roleIds != null)
			for(Integer roleId : roleIds){
				Role role = roleDao.findById(roleId);
				bean.addToRoles(role);
			}
		return bean;
	}

	public User deleteById(Integer id) {
		User entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}

	public User update(User bean, Integer [] roleIds){
		Updater<User> updater = new Updater<User>(bean);
		bean = updateByUpdater(updater);
		resetPassword(bean, bean.getPassword());
		bean.getRoles().clear();
		if(roleIds != null)
			for(Integer roleId : roleIds){
				Role role = roleDao.findById(roleId);
				bean.addToRoles(role);
			}
		return bean;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getList() {
		Finder f = Finder.create("from User");
		return find(f);
	}	
	
	public Pagination getPage(String name, Integer pageNo, Integer pageSize) {
		
		Finder f = Finder.create("from User");
		
		if (name != null && name != "") {
			f.append(" where name like :name");
			f.setParam("name", "%"+name+"%");
		} else {
			f.append(" where 1=1");
		}
	
		return find(f, pageNo, pageSize);
	}
	
	public User resetPassword(User user, String password){
		user.setPassword(pwdEncoder.encodePassword(password));
		return user;
	}
	
	public User login(String username, String password, String ip, String userType,
			HttpServletRequest request, HttpServletResponse response, SessionProvider sessionProvider)
			throws UsernameNotFoundException, BadCredentialsException {
		User user = this.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("用户名不存在: " + username);
		}
		
		if (checkPassword(user, password) == false) {
			throw new BadCredentialsException("密码错误");
		}
		
		sessionProvider.setAttribute(request, response, userType, user.getId());
		return user; 
	}
	
	public boolean checkPassword(User user, String password){
		if(pwdEncoder.isPasswordValid(user.getPassword(), password))
			return true;
		return false;
	}
	
	@Override
	protected Class<User> getEntityClass() {
		return User.class;
	}
	
	@Autowired
	private PwdEncoder pwdEncoder;
	
	@Autowired
	private RoleDao roleDao;
}