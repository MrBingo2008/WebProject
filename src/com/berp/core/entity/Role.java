package com.berp.core.entity;

import java.io.Serializable;
import java.util.Collection;

public class Role  implements Serializable {

	public static Integer[] fetchIds(Collection<Role> roles) {
		if (roles == null) {
			return null;
		}
		Integer[] ids = new Integer[roles.size()];
		int i = 0;
		for (Role r : roles) {
			ids[i++] = r.getId();
		}
		return ids;
	}
	
	// constructors
	public Role () {
		initialize();
	}

	/**
	 * Constructor for primary key
	 */
	public Role (java.lang.Integer id) {
		this.setId(id);
		initialize();
	}

	/**
	 * Constructor for required fields
	 */
	public Role (
		java.lang.Integer id,
		java.lang.String name,
		java.lang.Integer priority,
		java.lang.Boolean m_super) {

		this.setId(id);
		this.setName(name);
		//this.setPriority(priority);
		this.setSuper(m_super);
		initialize();
	}

	protected void initialize () {}



	private int hashCode = Integer.MIN_VALUE;

	// primary key
	private java.lang.Integer id;

	// fields
	private java.lang.String name;
	//private java.lang.Integer priority;
	private java.lang.Boolean m_super = false;

	// collections
	private java.util.Set<java.lang.String> perms;

	public java.lang.Integer getId () {
		return id;
	}

	public void setId (java.lang.Integer id) {
		this.id = id;
		this.hashCode = Integer.MIN_VALUE;
	}

	public java.lang.String getName () {
		return name;
	}

	public void setName (java.lang.String name) {
		this.name = name;
	}

	/*
	public java.lang.Integer getPriority () {
		return priority;
	}

	public void setPriority (java.lang.Integer priority) {
		this.priority = priority;
	}*/

	public java.lang.Boolean getSuper () {
		return m_super;
	}

	public void setSuper (java.lang.Boolean m_super) {
		this.m_super = m_super;
	}

	public java.util.Set<java.lang.String> getPerms () {
		return perms;
	}

	public void setPerms (java.util.Set<java.lang.String> perms) {
		this.perms = perms;
	}

	public boolean equals (Object obj) {
		if (null == obj) return false;
		if (!(obj instanceof Role)) return false;
		else {
			Role cmsRole = (Role) obj;
			if (null == this.getId() || null == cmsRole.getId()) return false;
			else return (this.getId().equals(cmsRole.getId()));
		}
	}

	public int hashCode () {
		if (Integer.MIN_VALUE == this.hashCode) {
			if (null == this.getId()) return super.hashCode();
			else {
				String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
				this.hashCode = hashStr.hashCode();
			}
		}
		return this.hashCode;
	}


	public String toString () {
		return super.toString();
	}
}