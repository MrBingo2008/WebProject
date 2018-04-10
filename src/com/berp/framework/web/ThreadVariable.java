package com.berp.framework.web;

import com.berp.core.entity.User;

/**
 * CMS线程变量
 * 
 * @author liufang
 * 
 */
public class ThreadVariable {
	/**
	 * 当前用户线程变量
	 */
	private static ThreadLocal<User> cmsUserVariable = new ThreadLocal<User>();

	/**
	 * 获得当前用户
	 * 
	 * @return
	 */
	public static User getUser() {
		return cmsUserVariable.get();
	}

	/**
	 * 设置当前用户
	 * 
	 * @param user
	 */
	public static void setUser(User user) {
		cmsUserVariable.set(user);
	}

	/**
	 * 移除当前用户
	 */
	public static void removeUser() {
		cmsUserVariable.remove();
	}
}
