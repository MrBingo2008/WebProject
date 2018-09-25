package com.berp.mrp.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.dao.CompanyDao;
import com.berp.core.entity.Company;
import com.berp.framework.hibernate3.Finder;
import com.berp.framework.hibernate3.HibernateBaseDao;
import com.berp.framework.hibernate3.Updater;
import com.berp.framework.page.Pagination;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Process;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional(rollbackFor=Exception.class)
public class OrderRecordDao extends HibernateBaseDao<OrderRecord, Integer> {

	public OrderRecord findById(Integer id) { 
		OrderRecord entity = get(id);
		return entity;
	}
	
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
		Finder f = Finder.create("select bean from OrderRecord bean where 1=1 ");
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
	
	//maxId和pageNum，只能选一个，而且如果是maxId的话，有可能刚开始时是null，所以最后要判断pageNum, 这个设计不大合理
	public Pagination getPage(Integer type, String name, Integer status, Integer status1, Integer maxId, Integer pageNo, Integer pageSize) {
		
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("select bean from OrderRecord bean where 1=1");
		if (type != null) {
			f.append(" and bean.ord.type=:type");
			f.setParam("type", type);
		}
		if (maxId != null) {
			f.append(" and bean.id<:maxId");
			f.setParam("maxId", maxId);
		}
		//notEmpty 包括null和""，notBlank还包括" "
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.ord.name like :name or bean.ord.serial like :name or bean.material.serial like :name or bean.material.customerSerial like :name or bean.material.name like :name) ");
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
		if(pageNo !=null)
			return find(f, pageNo, pageSize);
		else
			return find(f, pageSize);
	}
	
	//这个现在暂时不用
	public void updateFinishNumber (Company company, Material material, Double number) throws Exception{
		
		List<OrderRecord> records = findByCompanyAndMaterial(company.getId(), material.getId(), null, null, 1, 2, null, null);
		if(records == null || records.size()<=0)
			throw new Exception(String.format("'%s'的%s未完成数为0%s，已超出其范围。", company.getName(), material.getName(), material.getUnit()));
		
		Double companyNotFinishNumber =0.00;
		
		Double remainNumber = number;
		for(int i = 0;i<records.size() && remainNumber > 0;i++){
			OrderRecord record = records.get(i);
			
			companyNotFinishNumber += record.getNotFinishNumber();
			
			Double finishNumber = record.getFinishNumber();
			Double newNumber = finishNumber +remainNumber;
			
			if(newNumber<record.getNumber()){
				//order.setStatus(Order.Status.partFinish.ordinal());
				record.setFinishNumber(newNumber);
				record.setStatus(2);
				remainNumber = 0.00;
			}
			else if(newNumber.equals(record.getNumber())){
				//order.setStatus(Order.Status.finish.ordinal());
				record.setFinishNumber(record.getNumber());
				record.setStatus(3);
				remainNumber = 0.00;
			}else{
				if(i < records.size() - 1){
					//order.setStatus(Order.Status.finish.ordinal());
					record.setFinishNumber(record.getNumber());
					record.setStatus(3);
					remainNumber = newNumber - record.getNumber();
				}else{
					throw new Exception(String.format("'%s'的%s未完成数为%.0f%s，已超出其范围。", record.getOrd().getCompany().getName(), record.getMaterial().getName(), companyNotFinishNumber, record.getMaterial().getUnit()));
				}
			}
			//刚修改record的数据，就马上去修改order的status(status需要靠record来判断)，应该是没问题
			orderDao.updateStatusForCir(record.getOrd().getId());
		}
	}
	
	//收到货是以record为主的
	public OrderRecord updateFinishNumber (BatchFlow flow) throws Exception{
		
		OrderRecord record = findById(flow.getRecord().getId());
		Double finishNumber = record.getFinishNumber();
		Double newFinishNumber = finishNumber + flow.getNumber();
		
		if(newFinishNumber<record.getNumber()){
			//order.setStatus(Order.Status.partFinish.ordinal());
			record.setFinishNumber(newFinishNumber);
			record.setStatus(2);
		}
		else if(newFinishNumber >= record.getNumber()){
			record.setFinishNumber(newFinishNumber);
			record.setStatus(3);
		}else{
			//2018-6-27
			//throw new Exception(String.format("'%s'的%s未完成数为%.0f%s，已超出其范围。", record.getOrd().getSerial(), record.getMaterial().getName(), record.getNotFinishNumber(), record.getMaterial().getUnit()));	
		}
		//刚修改record的数据，就马上去修改order的status(status需要靠record来判断)，应该是没问题
		orderDao.updateStatusForCir(record.getOrd().getId());
		return record;
	}
	
	//用于cir的弃核
	public OrderRecord cancelFinishNumber (BatchFlow flow) throws Exception{
		
		OrderRecord record = findById(flow.getRecord().getId());
		Double finishNumber = record.getFinishNumber();
		Double newFinishNumber = finishNumber - flow.getNumber();
		
		if(newFinishNumber == 0){
			//order.setStatus(Order.Status.partFinish.ordinal());
			record.setFinishNumber(newFinishNumber);
			int status = Order.Status.approval.ordinal();
			record.setStatus(status);
		}
		else if(newFinishNumber < record.getNumber()){
			record.setFinishNumber(newFinishNumber);
			record.setStatus(Order.Status.partFinish.ordinal());
		}else{
			//throw new Exception(String.format("'%s'的%s完成数为%.0f%s，已超出其范围。", record.getOrd().getSerial(), record.getMaterial().getName(), record.getFinishNumber(), record.getMaterial().getUnit()));
			//2018-6-27
			record.setFinishNumber(newFinishNumber);
			record.setStatus(Order.Status.finish.ordinal());
		}
		//刚修改record的数据，就马上去修改order的status(status需要靠record来判断)，应该是没问题
		orderDao.updateStatusForCir(record.getOrd().getId());
		
		return record;
	}
	
	@Override
	protected Class<OrderRecord> getEntityClass() {
		return OrderRecord.class;
	}
	
	@Autowired
	private OrderDao orderDao;
}