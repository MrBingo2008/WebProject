package com.berp.framework.hibernate3;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.berp.framework.page.Pagination;

//import com.jeecms.common.page.Pagination;
//import com.jeecms.common.util.MyBeanUtils;

/**
 * hibernate DAO基类
 * 
 * 提供hql分页查询，不带泛型，与具体实体类无关。
 */
public abstract class HibernateSimpleDao {
	/**
	 * 日志，可用于子类
	 */
	protected Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * HIBERNATE 的 order 属性
	 */
	protected static final String ORDER_ENTRIES = "orderEntries";

	/**
	 * 通过HQL查询对象列表
	 * 
	 * @param hql
	 *            hql语句
	 * @param values
	 *            数量可变的参数
	 */
	@SuppressWarnings("unchecked")
	protected List find(String hql, Object... values) {
		return createQuery(hql, values).list();
	}

	protected Object findUnique(String hql, Object... values) {
		return createQuery(hql, values).uniqueResult();
	}

	//stone: 把int改为Integer后部署到tomcat会变得很慢，卡在c3p0那里，不知道为啥!
	@SuppressWarnings("unchecked")
	protected Pagination find(Finder finder, int pageNo, int pageSize) {
		int totalCount = countQueryResult(finder);
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	
	@SuppressWarnings("unchecked")
	protected Pagination find(Finder finder, int pageSize) {
		int totalCount = countQueryResult(finder);
		Pagination p = new Pagination(1, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	
	/*
	protected Pagination findByGroup(Finder finder,String selectSql, int pageNo, int pageSize) {
		return findByTotalCount(finder, pageNo, pageSize,  countQueryResultByGroup(finder,selectSql));
	}*/

	@SuppressWarnings("unchecked")
	protected List find(Finder finder) {
		Query query = finder.createQuery(getSession());
		List list = query.list();
		return list;
	}
	
	protected Query createQuery(String queryString, Object... values) {
		Assert.hasText(queryString);
		Query queryObject = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject;
	}

	/*
	@SuppressWarnings("unchecked")
	protected Pagination findByCriteria(Criteria crit, int pageNo, int pageSize) {
		CriteriaImpl impl = (CriteriaImpl) crit;
		// 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();
		List<CriteriaImpl.OrderEntry> orderEntries;
		try {
			orderEntries = (List) MyBeanUtils
					.getFieldValue(impl, ORDER_ENTRIES);
			MyBeanUtils.setFieldValue(impl, ORDER_ENTRIES, new ArrayList());
		} catch (Exception e) {
			throw new RuntimeException(
					"cannot read/write 'orderEntries' from CriteriaImpl", e);
		}

		int totalCount = ((Number) crit.setProjection(Projections.rowCount())
				.uniqueResult()).intValue();
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}

		// 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
		crit.setProjection(projection);
		if (projection == null) {
			crit.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (transformer != null) {
			crit.setResultTransformer(transformer);
		}
		try {
			MyBeanUtils.setFieldValue(impl, ORDER_ENTRIES, orderEntries);
		} catch (Exception e) {
			throw new RuntimeException(
					"set 'orderEntries' to CriteriaImpl faild", e);
		}
		crit.setFirstResult(p.getFirstResult());
		crit.setMaxResults(p.getPageSize());
		p.setList(crit.list());
		return p;
	}*/

	protected int countQueryResult(Finder finder) {
		Query query = getSession().createQuery(finder.getRowCountHql());
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return ((Number) query.iterate().next()).intValue();
	}
	
	/*
	protected int countQueryResultByGroup(Finder finder,String selectSql) {
		Query query = getSession().createQuery(finder.getRowCountTotalHql(selectSql));
		setParamsToQuery(finder, query);
		return ((Number) query.iterate().next()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	private Pagination findByTotalCount(Finder finder, int pageNo, int pageSize,int totalCount) {
		Pagination p = new Pagination(pageNo, pageSize, totalCount);
		if (totalCount < 1) {
			p.setList(new ArrayList());
			return p;
		}
		Query query = getSession().createQuery(finder.getOrigHql());
		finder.setParamsToQuery(query);
		query.setFirstResult(p.getFirstResult());
		query.setMaxResults(p.getPageSize());
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		List list = query.list();
		p.setList(list);
		return p;
	}
	
	private Query setParamsToQuery(Finder finder,Query query){
		finder.setParamsToQuery(query);
		if (finder.isCacheable()) {
			query.setCacheable(true);
		}
		return query;
	}*/

	protected SessionFactory sessionFactory;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}
}
