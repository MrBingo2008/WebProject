package com.berp.mrp.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.mrp.entity.MaterialAttach;

@Service
@Transactional(rollbackFor=Exception.class)
public class MaterialAttachDao extends HibernateBaseDao<MaterialAttach, Integer> {

	public MaterialAttach findById(Integer id) { 
		MaterialAttach entity = get(id);
		return entity;
	}
	
	public MaterialAttach findByLocation(String location){
		return findUniqueByProperty("location", location);
	}
	/*
	public OrderRecord deleteById(Integer id) {
		OrderRecord entity = get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public void deleteOrderNull(){
		Finder f = Finder.create("select bean from OrderRecord bean where bean.ord.id is null");
		List<OrderRecord> toDelList = find(f);
		for(OrderRecord record: toDelList)
			getSession().delete(record);
	}
	
	public List<OrderRecord> findByCompanyAndMaterial(Integer companyId, Integer materialId, String name, Integer orderType, Integer status1, Integer status2, Date start, Date end){
		Finder f = Finder.create("select bean from OrderRecord bean where 1=1");
		if(companyId!=null)
		{
			f.append(" and bean.ord.company.id=:companyId");
			f.setParam("companyId", companyId);
		}
		if(materialId!=null){
			f.append(" and bean.material.id=:materialId");
			f.setParam("materialId", materialId);
		}
		
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.ord.name like :name or bean.ord.serial like :name or bean.material.serial like :name or bean.material.customerSerial like :name or bean.material.name like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		
		if(orderType!=null){
			f.append(" and bean.ord.type=:orderType");
			f.setParam("orderType", orderType);
		}
		
		if(status1!=null && status2 ==null){
			f.append(" and bean.status =: statuts1");
			f.setParam("status1", status1);
		}else if(status1!=null && status2!=null){
			f.append(" and bean.status between :status1 and :status2");
			f.setParam("status1", status1);
			f.setParam("status2", status2);
		}
		
		if(start != null){
			f.append(" and bean.ord.billTime >=:start");
			f.setParam("start", start);
		}
		
		if(end != null){
			Calendar c = Calendar.getInstance();
			c.setTime(end);
			c.add(Calendar.DAY_OF_MONTH, 1);
			f.append(" and bean.ord.billTime <=:end");
			f.setParam("end", c.getTime());
		}
		
		f.append(" order by bean.id desc");
		return find(f);
	}
	
	public Pagination getPage(Integer type, String name, Integer status, Integer status1, Integer pageNo, Integer pageSize) {
		
		pageNo = pageNo == null?1:pageNo;
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("select bean from OrderRecord bean where 1=1");
		if (type != null) {
			f.append(" and bean.ord.type=:type");
			f.setParam("type", type);
		}
		//notEmpty 包括null和""，notBlank还包括" "
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.ord.name like :name or bean.ord.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		if (status != null && status1 == null) {
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}else if(status!=null && status1!=null){
			f.append(" and bean.status>=:status and bean.status <=:status1");
			f.setParam("status", status);
			f.setParam("status1", status1);
		}
		
		f.append(" order by bean.id desc");
		return find(f, pageNo, pageSize);
	}*/
	
	@Override
	protected Class<MaterialAttach> getEntityClass() {
		return MaterialAttach.class;
	}
}