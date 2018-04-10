package com.berp.mrp.dao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Room;

@Service
@Transactional
public class RoomDao extends HibernateBaseDao<Room, Integer> {
	
	public Room findById(Integer id) { 
		Room entity = get(id);
		return entity;
	}

	//@Transactional
	public Room save(Room bean) {
		getSession().save(bean);
		return bean;
	}

	public Room deleteById(Integer id) {
		Room entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public Pagination getPage(Integer parentId, Integer type, Integer pageNo, Integer pageSize) {
		//stone: 注意，要多个select bean，要不然选出来有category
		Finder f = Finder.create("select bean from Room bean ");
		
		if (parentId != null) {
			f.append(" join bean.parent category, Category parent");
			f.append(" where category.lft between parent.lft and parent.rgt");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", parentId);
		} else {
			f.append(" where 1=1");
		}
		
		if (type != null) {
			f.append(" and bean.type=:type");
			f.setParam("type", type);
		}
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List getList() {
		String hql = "from Room";
		return find(hql);
	}
	
	@SuppressWarnings("unchecked")
	public List<Room> getList(Integer parentId) {
		Finder f = Finder.create("select bean from Room bean");
		
		if (parentId != null) {
			f.append(" join bean.parent category, Category parent");
			f.append(" where category.lft between parent.lft and parent.rgt");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", parentId);
		} else {
			f.append(" where 1=1");
		}
		//f.append(" order by bean.priority asc");
		return find(f);
	}
	
	@Override
	protected Class<Room> getEntityClass() {
		return Room.class;
	}
}