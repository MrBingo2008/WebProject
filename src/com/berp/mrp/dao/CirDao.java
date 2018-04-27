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
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.RawBatch;
import com.berp.mrp.entity.RawBatchFlow;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Cir.CirType;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional(rollbackFor=Exception.class)
public class CirDao extends HibernateBaseDao<Cir, Integer> {

	public Cir findById(Integer id) { 
		Cir entity = get(id);
		return entity;
	}

	public Cir save(Cir bean) throws Exception{
		getSession().save(bean);
		
		if(bean.getStatus() == 1)
			this.updateQuatity(bean);
		return bean;
	}
	
	public Cir update(Cir bean) throws Exception{
		Updater<Cir> updater = new Updater<Cir>(bean);
		bean = updateByUpdater(updater);
		
		if(bean.getStatus() == 1)
			this.updateQuatity(bean);
		
		String hql = "delete from BatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();
		
		hql = "delete from RawBatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();
		
		return bean;
	}
	
	private void updateQuatity(Cir bean) throws Exception{
		Integer type = bean.getType();
		if(type == Cir.CirType.sellOut.ordinal())
			this.sellOutUpdate(bean);
		if(type == Cir.CirType.sellBack.ordinal())
			this.sellBackUpdate(bean);
		else if(type == Cir.CirType.purchaseIn.ordinal())
			this.purchaseInUpdate(bean);
		else if(type == Cir.CirType.purchaseBack.ordinal())
			this.purchaseBackUpdate(bean);
		else if(type == Cir.CirType.outsideOut.ordinal())
			this.outsideOutUpdate(bean);
		else if(type == Cir.CirType.outsideIn.ordinal())
			this.outsideInUpdate(bean);
		else if(type == Cir.CirType.checkIn.ordinal())
			this.checkInUpdate(bean);
		else if(type == Cir.CirType.checkOut.ordinal())
			this.checkOutUpdate(bean);
	}
	
	private void sellOutUpdate(Cir bean) throws Exception{
		List<BatchFlow> flows = bean.getFlows();
		for(BatchFlow flow: flows){
			OrderRecord record = recordDao.findById(flow.getRecord().getId());
			Order ord = record.getOrd();
			if(ord.getCompany().getId().equals(bean.getCompany().getId()) == false){
				throw new Exception(String.format("'%s'订单的供应商为%s，与本发货单供应商%s不一致。", ord.getSerial(), ord.getCompany().getName(), bean.getCompany().getName()));	
			}
			BatchFlow f = flowDao.findById(flow.getId());
			if(record.getSurface()!=null && !record.getSurface().getId().equals(f.getDefaultSurfaceId())){
				throw new Exception(String.format("'%s'订单的%s表面处理为%s，与该批次的产品不一致。", ord.getSerial(), record.getMaterial().getName(), record.getSurface().getName()));
			}
			flowDao.updateLeftNumber(flow.getParent().getId(), flow.getNumber());
			recordDao.updateFinishNumber(flow);
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, flow.getNumber());
		}
	}

	private void sellBackUpdate(Cir bean) throws Exception{
		List<BatchFlow> flows = bean.getFlows();
		if(flows ==null)
			return;
		for(BatchFlow flow : flows){
			//flow.setStatus(1);
			materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), null, null);
		}
	}
	
	//更新order的record
	private void purchaseInUpdate(Cir bean) throws Exception{

		//这里要重新获取order，cir自带的order没有records，不用updateOrder的原因是怕commit有嵌套
		//Order order = orderDao.findById(bean.getOrder().getId());
		//List<OrderRecord> records = order.getRecords();
		List<BatchFlow> flows = bean.getFlows();
		if(flows ==null)
			return;
		for(BatchFlow flow : flows){
			//flow.setStatus(1);
			OrderRecord record = recordDao.findById(flow.getRecord().getId());
			Order ord = record.getOrd();
			if(ord.getCompany().getId().equals(bean.getCompany().getId()) == false){
				throw new Exception(String.format("'%s'订单的客户为%s，与本到货单客户%s不一致。", ord.getSerial(), ord.getCompany().getName(), bean.getCompany().getName()));	
			}
			recordDao.updateFinishNumber(flow);
			materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), flow.getNumber(), null);
		}
	}
	
	private void purchaseBackUpdate(Cir bean) throws Exception{
		List<BatchFlow> flows = bean.getFlows();
		for(BatchFlow flow: flows){
			//flow.setStatus(1);
			Double number = flow.getNumber();
			materialDao.updateNumber(flow.getMaterial().getId(), -number, null, null);
			flowDao.updateLeftNumber(flow.getParent().getId(), number);
		}
	}
	
	private void outsideOutUpdate(Cir bean) throws Exception{
		List<RawBatchFlow> rawFlows = bean.getRawFlows();
		for(RawBatchFlow rawFlow : rawFlows){
			//rawFlow.setStatus(1);
			rawFlowDao.updateLeftNumber(rawFlow.getParent().getId(), rawFlow.getNumber());
		}
	}
	
	private void outsideInUpdate(Cir bean) throws Exception{
		List<RawBatchFlow> rawFlows = bean.getRawFlows();
		
		for(RawBatchFlow rawFlow : rawFlows){
			//rawFlow.setStatus(1);
			
			//更新前两级的flow
			RawBatchFlow parentFlow = rawFlowDao.updateArriveNumber(rawFlow.getParent().getId(), rawFlow.getNumber(), bean.getCompany());
			if(parentFlow.getArriveNumber().equals(parentFlow.getNumber())){
				
				Plan plan = parentFlow.getPlan();
				PlanStep planStep = plan.getCurrentStep();
				if(planStep.getStep().getType() != 1)
					throw new Exception("生产数据不一致");
				planStep.setFinishTime(bean.getCreateTime());
				planStep.setNumber(parentFlow.getNumber());
				planStep.setStatus(PlanStep.Status.finish.ordinal());
				
				parentFlow.setLeftNumber(parentFlow.getNumber());
				parentFlow.setArriveNumber(0.00);
				
				//下一步有三种情况，第一是结束了，第二是仍然为委外，第三是self manu
				List<PlanStep> steps = plan.getSteps();
				if(steps.indexOf(planStep) == steps.size()-1){
					plan.setStatus(Plan.Status.manuFinish.ordinal());
				}
				else if(steps.get(steps.indexOf(planStep)+1).getStep().getType() == 1){
					//这是重设outside的一套动作
					parentFlow.setArriveNumber(0.00);
					parentFlow.setLeftNumber(parentFlow.getNumber());
					parentFlow.setStatus(1);
					plan.setStatus(Plan.Status.outside.ordinal());
				}
				else{
					plan.setStatus(Plan.Status.materialFinish.ordinal());
				}
			}
		}
	}

	private void checkInUpdate(Cir bean) throws Exception{
		List<BatchFlow> flows = bean.getFlows();
		if(flows ==null)
			return;
		for(BatchFlow flow : flows){
			//flow.setStatus(1);
			materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), null, null);
		}
	}
	
	private void checkOutUpdate(Cir bean) throws Exception{
		List<BatchFlow> flows = bean.getFlows();
		if(flows == null)
			return;
		for(BatchFlow flow : flows){
			//flow.setStatus(1);
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, null);
			flowDao.updateLeftNumber(flow.getParent().getId(), flow.getNumber());
		}
	}
	
	//这种delete抛出的异常应该是Exception，所以不会回滚，不过这里只有一个操作，回不回滚都应该无所谓
	public Cir deleteById(Integer id) {
		Cir entity = get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	public Pagination getPage(Integer type, String name, String itemName, Integer status, Integer pageNo, Integer pageSize) {
		
		pageNo = pageNo == null?1:pageNo;
		pageSize = pageSize == null?20:pageSize;
		
		Finder f = Finder.create("select distinct bean from Cir bean left join bean.flows flow where 1=1");
		if(type == Cir.CirType.outsideIn.ordinal() || type == Cir.CirType.outsideOut.ordinal())
			f = Finder.create("select distinct bean from Cir bean left join bean.rawFlows flow where 1=1");
		
		if (type != null) {
			f.append(" and bean.type=:type");
			f.setParam("type", type);
		}
		
		if(StringUtils.isNotEmpty(name))
		{
			f.append(" and (bean.name like :name or bean.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		
		if(StringUtils.isNotEmpty(itemName)){
			f.append(" and (flow.material.name like :itemName or flow.material.serial like :itemName or flow.material.customerSerial like :itemName) ");
			f.setParam("itemName", "%"+ itemName +"%");
		}
		
		if (status != null) {
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}
		
		f.append(" order by bean.id desc");
		return find(f, pageNo, pageSize);
	}
	
	public Integer getMaxSerial(String serial){
		serial = "%"+serial+"%";
		String hql = "select max(bean.serial) from Cir bean where bean.serial like ?";
		
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
	public List getList() {
		String hql = "from Cir";
		return find(hql);
	}
	
	public int countByStatus(Integer type, Integer status1, Integer status2) {
		String hql = "select count(*) from Cir bean where bean.type=:type and bean.status >=:status1 and bean.status<=:status2";
		Query query = getSession().createQuery(hql);
		query.setParameter("type", type);
		query.setParameter("status1", status1);
		query.setParameter("status2", status2);
		return ((Number) query.iterate().next()).intValue();
	}
	
	public void updateStatusByRawFlow(Integer id){
		Cir cir = this.findById(id);
		
		boolean isFinish = true;
		boolean isPart = false;
		
		List<RawBatchFlow> flows = cir.getRawFlows();
		if(flows!=null){
			for(RawBatchFlow flow: flows){
				if(!flow.getStatus().equals(3))
					isFinish = false;
				
				if(flow.getStatus()>1)
					isPart = true;
			}
			
			if(isFinish == true)
				cir.setStatus(3);
			else if(isPart == true)
				cir.setStatus(2);
		}
	}
	
	@Override
	protected Class<Cir> getEntityClass() {
		return Cir.class;
	}
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderRecordDao recordDao;

	@Autowired
	private BatchFlowDao flowDao;
	
	@Autowired
	private RawBatchFlowDao rawFlowDao;
	
	@Autowired
	private MaterialDao materialDao;
	
}