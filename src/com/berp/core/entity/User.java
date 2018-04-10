package com.berp.core.entity;

import java.util.HashSet;
import java.util.Set;

public class User {
	
	private Integer id;
	private String name;
	private String password;
	private java.util.Date lastLoginTime;
	private java.lang.String lastLoginIp;
	private java.lang.Integer loginCount = 0;
	private java.util.Set<Role> roles;
	
	public User(){
		
	}
	
	public User (java.lang.Integer id) {
		this.setId(id);
	}
	
	public java.lang.Integer getId () {
		return id;
	}
	
	public void setId (java.lang.Integer id) {
		this.id = id;
	}

	public java.lang.String getName () {
		return name;
	}

	public void setName (java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getPassword () {
		return password;
	}

	public void setPassword (java.lang.String password) {
		this.password = password;
	}
	
	public java.util.Date getLastLoginTime () {
		return lastLoginTime;
	}

	public void setLastLoginTime (java.util.Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public java.lang.String getLastLoginIp () {
		return lastLoginIp;
	}

	public void setLastLoginIp (java.lang.String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}

	public java.lang.Integer getLoginCount () {
		return loginCount;
	}

	public void setLoginCount (java.lang.Integer loginCount) {
		this.loginCount = loginCount;
	}
	
	//roles
	public java.util.Set<Role> getRoles () {
		return roles;
	}

	public void setRoles (java.util.Set<Role> roles) {
		this.roles = roles;
	}
	
	public Set<Integer> getRoleIds(){
		Set<Integer> ids = new HashSet<Integer>();
		if(roles != null)
			for(Role role: roles){
				ids.add(role.getId());
			}
		return ids;
	}

	public void addToRoles(Role role) {
		if (role == null) {
			return;
		}
		Set<Role> set = getRoles();
		if (set == null) {
			set = new HashSet<Role>();
			setRoles(set);
		}
		set.add(role);
	}
	
	public Set<String> getPerms() {
		Set<Role> roles = getRoles();
		if (roles == null) {
			return null;
		}
		Set<String> allPerms = new HashSet<String>();
		for (Role role : getRoles()) {
			allPerms.addAll(role.getPerms());
		}
		return allPerms;
	}
	
	public boolean isSuper(){
		Set<Role> roles = getRoles();
		if (roles == null) {
			return false;
		}
		for (Role role : getRoles()) {
			if (role.getSuper()) {
				return true;
			}
		}
		return false;
	}
}
