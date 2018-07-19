package com.berp.mrp.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Process;
import com.berp.mrp.entity.RawBatchFlow;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional(rollbackFor=Exception.class)
public class OrderDao extends HibernateBaseDao<Order, Integer> {

	public Order findById(Integer id) { 
		Order entity = get(id);
		return entity;
	}

	//@Transactional
	public Order save(Order bean) throws Exception{
		getSession().save(bean);
		if(bean.getStatus() == 1)
			this.updateMaterialQuatity(bean);
		return bean;
	}

	public Order deleteById(Integer id) {
		Order entity = get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public Order update(Order bean) throws Exception{
		Updater<Order> updater = new Updater<Order>(bean);
		bean = updateByUpdater(updater);
		
		if(bean.getStatus() == 1)
			this.updateMaterialQuatity(bean);
		
		//update order时，对于records来说，先update，然后再把外键清空，然后再为赋值外键，如果record不再属于order，则不会赋值外键
		//所以还要手动删除， 但是之前删除直接用hql的delete，这样是无法删除record的sellRecords
		//因此改为delete(entity)，这样会自动删除sell records，不需要手动去删除
		recordDao.deleteOrderNull();
		
		//对于delete order来说，就不一样了，会直接delete record
		
		return bean;
	}
	
	//更新material的notPurchaseIn和notSellOut数据
	private void updateMaterialQuatity(Order bean) throws Exception{
		Integer type = bean.getType();
		List<OrderRecord> orders = bean.getRecords();
		if(type == 1){
			for(OrderRecord record : orders){
				materialDao.updateNotPurchaseInNumber(record.getMaterial().getId(), record.getNumber());
			}
		}else if(type == 2){
			for(OrderRecord record : orders){
				materialDao.updateNotSellOutNumber(record.getMaterial().getId(), record.getNumber());
			}
		}
	}
	
	//做个测试：orderDao和materialDao都不加rollback=exception，然后materialDao.updateNotSellNumber后抛出异常，结果updateNotSellNumber生效
	//如果orderDao加rollback=exception，而materialDao不加，然后materialDao.updateNotSellNumber后抛出异常，结果updateNotSellNumber不生效，这是嵌套后，以外面一个为主的意思吧？
	//更新material的notPurchaseIn和notSellOut数据
	public void cancelApproval(Integer orderId) throws Exception{
		Order order = findById(orderId);
		
		Integer type = order.getType();
		List<OrderRecord> orders = order.getRecords();
		//1=采购
		if(type == 1){
			for(OrderRecord record : orders){
				if(record.getFlows() != null && record.getFlows().size()>0)
					throw new Exception("请先删除相关联的到货单" + record.getCirSerials());
			}
		}else if(type == 2){
			for(OrderRecord record : orders){
				if(record.getFlows() != null && record.getFlows().size()>0)
					throw new Exception("请先删除相关联的发货单" + record.getCirSerials());
				
				if(record.getPlans() != null && record.getPlans().size()>0)
					throw new Exception("请先删除相关联的生产任务" + record.getPlanSerials());
				
				if(record.getPurchaseRecords()!=null && record.getPurchaseRecords().size()>0)
					throw new Exception("请先删除相关联的采购订单" + record.getPurchaseOrderSerials());
			}
		}
		/*
		if(order.getPurchaseOrders()!=null && order.getPurchaseOrders().size()>0)
			throw new Exception("请先删除相关联的采购订单" + order.getPurchaseOrderSerials());*/
		
		order.setStatus(0);
		if(type == 1){
			for(OrderRecord record : orders){
				materialDao.updateNotPurchaseInNumber(record.getMaterial().getId(), -record.getNumber());
			}
		}else if(type == 2){
			for(OrderRecord record : orders){
				//也会throw exception
				materialDao.updateNotSellOutNumber(record.getMaterial().getId(), -record.getNumber());
			}
		}
	}
	
	//根据record的收到货情况，更新order的状态
	public void updateStatusForCir(Integer id){
		Order order = this.findById(id);
		boolean isFinish = true;
		boolean isPart = false;
		
		List<OrderRecord> records = order.getRecords();
		if(records!=null){
			for(OrderRecord record: records){
				if(!record.getStatus().equals(3))
					isFinish = false;
				
				if(record.getStatus()>1)
					isPart = true;
			}
			
			if(isFinish == true)
				order.setStatus(3);
			else if(isPart == true)
				order.setStatus(2);
			else
				order.setStatus(1);
		}
	}
	
	public Integer getMaxSerial(String serial){
		serial = "%"+serial+"%";
		String hql = "select max(bean.serial) from Order bean where bean.serial like ?";
		
		//如果是findUnique(),则hql里必须使用 ?, 而不是:serial，findUnique()应该是没有定义参数名，直接顺序代入
		String max = (String)this.findUnique(hql, serial);
		Integer num = 0;
		try{
			num = Integer.valueOf(max.split("-")[2]);
		}catch(Exception ex){
		}
		return num;
	}
	
	@SuppressWarnings("unchecked")
	public List<Order> getList(Integer type) {
		Finder f = Finder.create("from Order bean");
		f.append(" where bean.type=:type order by bean.id desc");
		f.setParam("type", type);
		return find(f);
	}
	
	public Pagination getPage(Integer type, String name, String recordName, String companyName, Integer status, Integer status1, Integer pageNo, Integer pageSize) {
		
		pageNo = pageNo == null?1:pageNo;
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("select distinct bean from Order bean left join bean.records record where 1=1");
		if (type != null) {
			f.append(" and bean.type=:type");
			f.setParam("type", type);
		}
		//notEmpty 包括null和""，notBlank还包括" "
		if(StringUtils.isNotBlank(name))
		{
			f.append(" and (bean.name like :name or bean.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		
		if(StringUtils.isNotBlank(recordName)){
			f.append(" and (record.material.name like :recordName or record.material.serial like :recordName or record.material.customerSerial like :recordName) ");
			f.setParam("recordName", "%"+ recordName +"%");
		}
		
		if(StringUtils.isNotBlank(companyName)){
			f.append(" and bean.company.name like :companyName");
			f.setParam("companyName", "%"+ companyName +"%");
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
	}
	
	public int countByStatus(Integer type, Integer status1, Integer status2) {
		String hql = "select count(*) from Order bean where bean.type=:type and bean.status >=:status1 and bean.status<=:status2";
		Query query = getSession().createQuery(hql);
		query.setParameter("type", type);
		query.setParameter("status1", status1);
		query.setParameter("status2", status2);
		int result = ((Number) query.iterate().next()).intValue();
		return result;
	}
	
	@Override
	protected Class<Order> getEntityClass() {
		return Order.class;
	}
	
	@Autowired
	OrderRecordDao recordDao;
	
	@Autowired
	private MaterialDao materialDao;
}