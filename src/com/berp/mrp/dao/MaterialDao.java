package com.berp.mrp.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Order;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional
public class MaterialDao extends HibernateBaseDao<Material, Integer> {
	
	public Material findById(Integer id) { 
		Material entity = get(id);
		return entity;
	}

	//@Transactional
	public Material save(Material bean) {
		getSession().save(bean);
		return bean;
	}

	public Material update(Material bean){
		Updater<Material> updater = new Updater<Material>(bean);
		updater.exclude("leftNumber");
		updater.exclude("notPurchaseInNumber");
		updater.exclude("notSellOutNumber");
		
		//stone: 因为有可能清空
		updater.include("process");
		updater.include("surface");
		
		bean = updateByUpdater(updater);
		
		String hql = "delete from ProductMaterial bean where bean.product.id is null";
		getSession().createQuery(hql).executeUpdate();
		
		hql = "delete from MaterialAttach bean where bean.material.id is null";
		getSession().createQuery(hql).executeUpdate();
		
		return bean;
	}
	
	//这是针对出入库的
	//弃核也会调用到此方法
	public Material updateNumber (Integer materialId, Double number,  Double purchaseInNumber, Double sellOutNumber) throws Exception{
		Material material = this.findById(materialId);
		
		if(number!=null){
			Double leftNum = material.getLeftNumber();
			if(leftNum + number < 0)
				throw new Exception(material.getNameSpec() + "剩余数量不足");
			material.setLeftNumber(leftNum + number);
		}
		
		if(purchaseInNumber!=null){
			Double notPurchaseInNumber = material.getNotPurchaseInNumber();
			//如果是弃核的话，purchaseInNumber为负，所以应该不会throw exception，所以这个exception message不会用到
			if(notPurchaseInNumber - purchaseInNumber < 0)
				//throw new Exception(material.getInfo() + "超出采购订单数量");
				material.setNotPurchaseInNumber(0.00);
			else
				material.setNotPurchaseInNumber(notPurchaseInNumber - purchaseInNumber);
		}
		
		if(sellOutNumber!=null){
			Double notSellOutNumber = material.getNotSellOutNumber();
			if(notSellOutNumber - sellOutNumber < 0)
				throw new Exception(material.getNameSpec() + "超出客户订单数量");
			material.setNotSellOutNumber(notSellOutNumber - sellOutNumber);
		}
		return material;
	}
	
	//这是针对订单的
	public Material updateNotPurchaseInNumber (Integer materialId, Double notPurchaseInNumber){
		Material material = this.findById(materialId);
		if(notPurchaseInNumber!=null){
			material.setNotPurchaseInNumber(material.getNotPurchaseInNumber()+ notPurchaseInNumber);
		}
		return material;
	}
	
	public Material updateNotSellOutNumber (Integer materialId, Double notSellOutNumber) throws Exception{
		Material material = this.findById(materialId);
		if(notSellOutNumber!=null){
			if(material.getNotSellOutNumber() + notSellOutNumber < 0)
				throw new Exception(material.getNameSpec() + "未发货数量为负.");
			material.setNotSellOutNumber(material.getNotSellOutNumber()+ notSellOutNumber);
		}
		return material;
	}
	
	public Material deleteById(Integer id) {
		Material entity = super.get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public Pagination getPage(Integer parentId, String name, Integer pageNo, Integer pageSize) {
		//stone: 注意，要多个select bean，要不然选出来有category
		Finder f = Finder.create("select bean from Material bean ");
		
		if (parentId != null) {
			f.append(" join bean.parent category, Category parent");
			f.append(" where category.lft between parent.lft and parent.rgt");
			f.append(" and parent.id=:parentId");
			f.setParam("parentId", parentId);
		} else {
			f.append(" where 1=1");
		}
		
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.name like :name or bean.serial like :name or bean.customerSerial like :name or bean.spec like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		
		f.append(" order by bean.id desc");
		
		return find(f, pageNo, pageSize);
	}
	
	@SuppressWarnings("unchecked")
	public List getList() {
		String hql = "from Material";
		return find(hql);
	}
	
	@SuppressWarnings("unchecked")
	public List<Material> getList(Integer parentId) {
		Finder f = Finder.create("select bean from Material bean");
		
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
	protected Class<Material> getEntityClass() {
		return Material.class;
	}
}