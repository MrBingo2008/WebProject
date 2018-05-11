package com.berp.mrp.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berp.core.entity.Company;
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
import com.berp.mrp.entity.Step;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Cir.CirType;
import com.berp.mrp.entity.Material;

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
			//这里不能重新查询flow，因为如果是update cir的时候，flows会重新建，如果重新查的话，在内存里就会有相同Id，但是不一样的flow
			//所以专门在flow设一个defaultSurfaceId，从页面那里接收过来
			//BatchFlow f = flowDao.findById(flow.getId());
			Step recordSurface  =record.getSurface();
			Integer recordSurfaceId = recordSurface.getId();
			Integer flowSurfaceId = flow.getDefaultSurfaceId();
			if(recordSurface!=null && (flowSurfaceId ==null || !recordSurfaceId.equals(flowSurfaceId)) ){
				throw new Exception(String.format("'%s'订单的%s表面处理为%s，与该批次的产品不一致。", ord.getSerial(), record.getMaterial().getName(), record.getSurface().getName()));
			}
			flowDao.updateLeftNumber(flow.getParent().getId(), flow.getNumber());
			recordDao.updateFinishNumber(flow);
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, flow.getNumber());
		}
	}

	public void sellOutCancelApproval(Integer cirId) throws Exception{
		Cir cir = findById(cirId);
		
		List<BatchFlow> flows = cir.getFlows();
		cir.setStatus(0);
		for(BatchFlow flow:flows){
			flow.setStatus(0);
			//要再测测这两个函数的异常情况
			recordDao.cancelFinishNumber(flow);
			flowDao.updateLeftNumber(flow.getParent().getId(), -flow.getNumber());
			materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), null, -flow.getNumber());
			
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
	
	public void sellBackCancelApproval(Integer cirId) throws Exception{
		Cir cir = findById(cirId);
		
		List<BatchFlow> flows = cir.getFlows();
		for(BatchFlow flow: flows){
			if(flow.getFlows()!=null && flow.getFlows().size()>0){
				throw new Exception("请先删除相关联的单据" + flow.getFlowsParentSerial());
			}
		}
		cir.setStatus(0);
		for(BatchFlow flow:flows){
			flow.setStatus(0);
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, null);
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
			//setStatus在PurchaseAct里设置，但是弃核的话，是在cirDao里设置
			//flow.setStatus(1);
			Double number = flow.getNumber();
			
			flowDao.updateLeftNumber(flow.getParent().getId(), number);
			materialDao.updateNumber(flow.getMaterial().getId(), -number, null, null);
		}
	}

	public void purchaseInCancelApproval(Integer cirId) throws Exception{
		Cir cir = findById(cirId);
		
		List<BatchFlow> flows = cir.getFlows();
		for(BatchFlow flow: flows){
			if(flow.getFlows()!=null && flow.getFlows().size()>0){
				throw new Exception("请先删除相关联的单据" + flow.getFlowsParentSerial());
			}
		}
		cir.setStatus(0);
		for(BatchFlow flow:flows){
			flow.setStatus(0);
			//要再测测这两个函数的异常情况
			recordDao.cancelFinishNumber(flow);
			materialDao.updateNumber(flow.getMaterial().getId(), - flow.getNumber(), - flow.getNumber(), null);
			
		}
	}
	
	//统一到一个函数
	public void purchaseBackCancelApproval(Integer cirId) throws Exception{
		Cir cir = findById(cirId);
		
		cir.setStatus(0);
		for(BatchFlow flow:cir.getFlows()){
			flow.setStatus(0);
			flowDao.updateLeftNumber(flow.getParent().getId(), - flow.getNumber());
			materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), null, null);
		}
	}
	
	private void outsideOutUpdate(Cir bean) throws Exception{
		List<RawBatchFlow> rawFlows = bean.getRawFlows();
		for(RawBatchFlow rawFlow : rawFlows){
			//rawFlow.setStatus(1);
			rawFlowDao.updateLeftNumber(rawFlow.getParent().getId(), rawFlow.getNumber());
		}
	}
	
	public void outsideOutCancelApproval(Integer cirId) throws Exception{
		Cir cir = this.findById(cirId);
		List<RawBatchFlow> rawFlows = cir.getRawFlows();
		
		for(RawBatchFlow rawFlow : rawFlows){
			if(rawFlow.getChildren()!=null && rawFlow.getChildren().size()>0)
				throw new Exception(String.format("请先删除相关联的到货批次'%s'", rawFlow.getChildrenString()));
			rawFlow.setStatus(0);
			rawFlowDao.updateLeftNumber(rawFlow.getParent().getId(), -rawFlow.getNumber());
		}
		
		cir.setStatus(0);
	}
	
	/*
	 * |-----leftnumber---->
	 * |                      <---arriveNumber---|
	 * */
	private void outsideInUpdate(Cir bean) throws Exception{
		List<RawBatchFlow> rawFlows = bean.getRawFlows();
		
		for(RawBatchFlow rawFlow : rawFlows){
			RawBatchFlow parent1Flow = rawFlowDao.findById(rawFlow.getParent().getId());
			Company flowCompany = parent1Flow.getCir().getCompany(); 
			if(flowCompany.getId().equals(bean.getCompany().getId()) == false){
				throw new Exception(String.format("出货批次%s的外加工单位为%s，与本到货单%s不一致。", rawFlow.getCir().getSerial(), flowCompany.getName(), bean.getCompany().getName()));	
			}
			//rawFlow.setStatus(1);
			
			//更新前两级的flow
			RawBatchFlow parentFlow = rawFlowDao.updateArriveNumber(rawFlow.getParent().getId(), rawFlow.getNumber());
			
			Plan plan = parentFlow.getPlan();
			PlanStep planStep = plan.getCurrentStep();
			planStep.setNumber(parentFlow.getArriveNumber());
			
			if(parentFlow.getArriveNumber().equals(parentFlow.getNumber())){
				
				if(planStep.getStep().getType() != 1)
					throw new Exception("生产数据不一致");
				planStep.setFinishTime(bean.getCreateTime());
				//planStep.setNumber(parentFlow.getNumber());
				planStep.setStatus(PlanStep.Status.finish.ordinal());
				
				//外加工完成，归零，弃核时又要重设回去
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

	public void outsideInCancelApproval(Integer cirId) throws Exception{
		
		Cir cir = this.findById(cirId);
		List<RawBatchFlow> rawFlows = cir.getRawFlows();
		
		for(RawBatchFlow rawFlow : rawFlows){
			Plan plan = rawFlow.getParent().getParent().getPlan();
			if(plan.getStatus() == Plan.Status.packageFinish.ordinal())
				throw new Exception(String.format("生产任务'%s'已入库, 请先弃核入库.", plan.getSerial()));
			
			PlanStep currentStep = rawFlow.getPlanStep();
			//两种情况：1，处于当前的step，2，处于最后一个apply的step：接下来要不就是没有step了，有step的话，分两种情况
			boolean isCurrentStep = currentStep.equals(plan.getCurrentStep());
			boolean isLastApply = currentStep.getIsLastApply();
			PlanStep nextStep = plan.getNextStep(rawFlow.getPlanStep());
			
			//boolean next1 = plan.getCurrentStep() == null && plan.getStatus().equals(Plan.Status.manuFinish.ordinal());
			//boolean next2 = plan.getCurrentStep() != null && plan.getCurrentStep().getStep().getType() == 0;
			//boolean next3 = plan.getCurrentStep() != null && plan.getCurrentStep().getStep().getType() == 1 && plan.getRawBatchFlow().getArriveNumber() == 0;
			
			if(isCurrentStep || 
					isLastApply && ( nextStep ==null || nextStep.getStep().getType() == 0 && nextStep.getStatus() == 0 || nextStep.getStep().getType() == 1 && nextStep.getStatus() ==0 && plan.getRawBatchFlow().getArriveNumber() == 0) ){
			
				//删除plan in
				plan.setFlows(plan.getMaterialFlows());
				
				rawFlow.setStatus(0);
				
				//这里有问题，如果完成的数量跟预计的数量不一致就麻烦
				if(currentStep.getStatus() == 1){
					plan.getRawBatchFlow().setNumber(currentStep.getNumber());
					plan.getRawBatchFlow().setArriveNumber(currentStep.getNumber());
					plan.getRawBatchFlow().setLeftNumber(0.00);
				}
				
				//更新所关联的发货单和生产任务
				RawBatchFlow parent = rawFlowDao.updateArriveNumber(rawFlow.getParent().getId(), -rawFlow.getNumber());
				
				//更新生产工序，不能在updateArriveNumber的函数里实现，因为他是个通用函数
				if(plan.getRawBatchFlow().getNumber() > plan.getRawBatchFlow().getArriveNumber()){
					rawFlow.getPlanStep().setStatus(0);
					rawFlow.getPlanStep().setNumber(plan.getRawBatchFlow().getArriveNumber());
					plan.setStatus(Plan.Status.outside.ordinal());
				}
				
			}else
				throw new Exception(String.format("目前生产任务'%s'的状态不处于该外加工'%s'，请将生产任务退回到该状态后，再弃核。", plan.getSerial(), rawFlow.getPlanStep().getStep().getName()));
		}
		
		cir.setStatus(0);
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
	
	public void checkInCancelApproval(Integer cirId) throws Exception{
		Cir cir = findById(cirId);
		
		List<BatchFlow> flows = cir.getFlows();
		for(BatchFlow flow: flows){
			if(flow.getFlows()!=null && flow.getFlows().size()>0){
				throw new Exception("请先删除相关联的单据" + flow.getFlowsParentSerial());
			}
		}
		cir.setStatus(0);
		for(BatchFlow flow:flows){
			flow.setStatus(0);
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, null);
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
	
	public void checkOutCancelApproval(Integer cirId) throws Exception{
		Cir cir = findById(cirId);
		
		cir.setStatus(0);
		for(BatchFlow flow:cir.getFlows()){
			flow.setStatus(0);
			flowDao.updateLeftNumber(flow.getParent().getId(), - flow.getNumber());
			materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), null, null);
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
			else
				cir.setStatus(1);
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