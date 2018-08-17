package com.berp.core.entity;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.berp.framework.hibernate3.NestedTree;


public class Category implements NestedTree<Integer> {
	
	private Integer id;
	private String name;
	private Integer priority = 0;

	private java.lang.Integer lft;
	private java.lang.Integer rgt;
	
	private Category parent;
	private List<Category> children;
	
	public Category(){
		
	}
	
	public Category (java.lang.Integer id) {
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
	
	//priority
	public java.lang.Integer getPriority () {
		return priority;
	}

	public void setPriority (java.lang.Integer priority) {
		this.priority = priority;
	}
	
	//left and right
	public java.lang.Integer getLft () {
		return lft;
	}

	public void setLft (java.lang.Integer lft) {
		this.lft = lft;
	}

	public java.lang.Integer getRgt () {
		return rgt;
	}

	public void setRgt (java.lang.Integer rgt) {
		this.rgt = rgt;
	}
	
	//parent
	public Category getParent () {
		return parent;
	}

	public void setParent (Category p) {
		this.parent = p;
	}
	
	public List<Category> getChildren () {
		return children;
	}

	public void setChildren (List<Category> children) {
		this.children = children;
	}
	
	
	//实现NestedTree接口
	/**
	 * 每个站点各自维护独立的树结构
	 * @see HibernateTree#getTreeCondition()
	 */
	public String getTreeCondition() {
		//return "bean.site.id=" + getSite().getId();
		return " ";
	}

	/**
	 * @see HibernateTree#getParentId()
	 */
	public Integer getParentId() {
		Category parent = getParent();
		if (parent != null) {
			return parent.getId();
		} else {
			return null;
		}
	}

	/**
	 * @see HibernateTree#getLftName()
	 */
	public String getLftName() {
		return DEF_LEFT_NAME;
	}

	/**
	 * @see HibernateTree#getParentName()
	 */
	public String getParentName() {
		return DEF_PARENT_NAME;
	}

	/**
	 * @see HibernateTree#getRgtName()
	 */
	public String getRgtName() {
		return DEF_RIGHT_NAME;
	}
	
	//从小到大
	public List<Category> getNodeList() {
		LinkedList<Category> list = new LinkedList<Category>();
		Category node = this;
		while (node != null) {
			list.addLast(node);
			node = node.getParent();
		}
		return list;
	}
	
	//从大到小
	public List<Category> getNodeListDesc() {
		LinkedList<Category> list = new LinkedList<Category>();
		Category node = this;
		while (node != null) {
			list.addFirst(node);
			node = node.getParent();
		}
		return list;
	}
	
	public String getFullname(){
		String fullname = "";
		Category node = this;
		while (node != null && node.getParent() != null) {
			fullname = StringUtils.isBlank(fullname)?node.getName() : node.getName() + "-" + fullname;
			node = node.getParent();
		}
		return fullname;
	}
}
