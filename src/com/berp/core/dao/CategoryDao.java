package com.berp.core.dao;

import java.util.List;




//import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.berp.mrp.entity.Plan;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class CategoryDao extends HibernateBaseDao<Category, Integer> {
	
	public Category findById(Integer id) { 
		Category entity = get(id);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public List getList() {
		String hql = "from Category";
		return find(hql);
	}	
	
	@SuppressWarnings("unchecked")
	public List<Category> getList(Integer parentId) {
		Finder f = Finder.create("from Category bean where bean.parent.id = :parentId");
		f.setParam("parentId", parentId);
		f.append(" order by bean.priority asc");
		return find(f);
	}	
	
	
	public Pagination getPage(Integer parentId, Integer pageNo, Integer pageSize) {
		Finder f = Finder.create("from Category bean");
		
		if (parentId != null) {
			f.append(" where bean.parent.id=:parentId");
			f.setParam("parentId", parentId);
		} else {
			f.append(" where 1=1");
		}
		return find(f, pageNo, pageSize);
	}
	
	//@Transactional
	public Category save(Category bean) {
		Integer parentId = bean.getParentId();
		bean.setPriority(this.getMaxPriority(parentId));
		getSession().save(bean);
		return bean;
	}

	public Category deleteById(Integer id) {
		Category entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public Category update(Category bean){
		Updater<Category> updater = new Updater<Category>(bean);
		bean = updateByUpdater(updater);
		return bean;
	}

	//获取只有一个根节点的树
	public JSONObject getCategoryTree(Category category){
		JSONObject obj = new JSONObject();
		try{
			obj.put("id", category.getId());
			obj.put("name", category.getName());
			List<Category> categorys = category.getChildren();
			if(categorys!=null && categorys.size()>0){
				JSONArray childrenJSON = new JSONArray();
				for(Category child : category.getChildren()){
					if(child == null)
						continue;
					childrenJSON.put(getCategoryTree(child));
				}
				obj.put("children", childrenJSON);
			}
			else
				return obj;
		}catch(Exception ex){
		}
		return obj;
	}
	
	public Integer getMaxPriority(Integer parentId){
		String hql = "select max(bean.priority) from Category bean where bean.parent.id = ?";
		
		//如果是findUnique(),则hql里必须使用 ?, 而不是:serial，findUnique()应该是没有定义参数名，直接顺序代入
		Integer num;
		try{
			num = (Integer)this.findUnique(hql, parentId);
			num++;
		}catch(Exception ex){
			num = 0;
		}
		return num;
	}
	
	@Override
	protected Class<Category> getEntityClass() {
		return Category.class;
	}
}