package com.berp.framework.web;

import javax.servlet.http.HttpServletRequest;

import com.berp.core.entity.User;

/**
 * 提供一些CMS系统中使用到的共用方法
 * 
 * 比如获得会员信息,获得后台站点信息
 * 
 * @author liufang
 * 
 */
public class RequestInfoUtils {
	/**
	 * 用户KEY
	 */
	public static final String USER_KEY = "_user_key";
	//public static final String TOP_DEPARTMENT_KEY = "_user_top_department_key";
	/**
	 * 站点KEY
	 */
	public static final String SITE_KEY = "_site_key";
	
	//added by stone
	public static final String CHANNEL_SITE_KEY = "_channel_site_key";

	/**
	 * 获得用户
	 * 
	 * @param request
	 * @return
	 */
	public static User getUser(HttpServletRequest request) {
		return (User) request.getAttribute(USER_KEY);
	}

	/**
	 * 获得用户ID
	 * 
	 * @param request
	 * @return
	 */
	public static Integer getUserId(HttpServletRequest request) {
		User user = getUser(request);
		if (user != null) {
			return user.getId();
		} else {
			return null;
		}
	}

	/**
	 * 设置用户
	 * 
	 * @param request
	 * @param user
	 */
	public static void setUser(HttpServletRequest request, User user) {
		request.setAttribute(USER_KEY, user);
	}

//	public static Department getTopDepartment(HttpServletRequest request) {
//		return (Department)request.getAttribute(TOP_DEPARTMENT_KEY);
//	}
	
//	public static void setTopDepartment(HttpServletRequest request, Department depart) {
//		request.setAttribute(TOP_DEPARTMENT_KEY, depart);
//	}
}
