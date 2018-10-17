package com.berp.mrp.dao;

import java.util.ArrayList;
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
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Process;
import com.berp.mrp.entity.RawBatch;
import com.berp.mrp.entity.RawBatchFlow;
import com.berp.mrp.entity.Step;

/*
import com.jeecms.common.hibernate3.Finder;
import com.jeecms.common.hibernate3.HibernateBaseDao;
import com.jeecms.common.page.Pagination;
import com.jeecms.core.dao.CmsUserDao;
import com.jeecms.core.entity.CmsUser;*/

@Service
@Transactional(rollbackFor=Exception.class)
//默认是propagation = Propagation.REQUIRED, 全部只用一个transaction,而且好像要高版本的spring-tx才支持自定义
public class PlanDaoOld extends HibernateBaseDao<Plan, Integer> {

	public Plan findById(Integer id) { 
		Plan entity = get(id);
		return entity;
	}

	//@Transactional
	public Plan save(Plan bean) {
		getSession().save(bean);
		return bean;
	}
	
	public Plan update(Plan bean){
		Updater<Plan> updater = new Updater<Plan>(bean);
		//updater.exclude("steps");
		bean = updateByUpdater(updater);
		
		//cascade选择all，这里需要删除plan为空的step，
		//上面被转移的steps，hibernate会自动生产update plan.id=null的sql，然后继续执行下面的语句，就会删除了
		//但是这样会留有一些问题，就是如果有多个终端混合着运行，可能存在sql语句交叉运行
		String hql = "delete from PlanStep bean where bean.plan.id is null";
		getSession().createQuery(hql).executeUpdate();
		return bean;
	}
	
	public Plan deleteById(Integer id) {
		Plan entity = get(id);
		if (entity != null) {
			getSession().delete(entity);
		}
		return entity;
	}
	
	//弃核（基本信息）
	public void cancelBasic(Integer planId) throws Exception{
		Plan plan = this.findById(planId);
		plan.setFlows(new ArrayList<BatchFlow>());
		plan.setStatus(Plan.Status.edit.ordinal());
		
		String hql = "delete from BatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();
	}
	
	public Plan updateMaterial(Plan bean) throws Exception{
		
		bean.setFlows(new ArrayList<BatchFlow>());
		bean.getFlows().addAll(bean.getMaterialFlows());
		
		Updater<Plan> updater = new Updater<Plan>(bean);
		updater.setUpdateMode(Updater.UpdateMode.MIN);
		updater.include("flows");
		updater.include("status");
		bean = updateByUpdater(updater);
		
		//在这里嵌套transaction，但是仍然等同一个transaction，如果把这个放在plan update前面也是一样
		if(bean.getStatus() == Plan.Status.materialFinish.ordinal()){
			try{
				if(bean.getSteps().get(0).getStep().getType() == 1)
					bean.setStatus(Plan.Status.outside.ordinal());
				
				List<BatchFlow> flows = bean.getMaterialFlows();
				for(BatchFlow flow : flows){
					flow.setStatus(1);
					flowDao.updateLeftNumber(flow.getParent().getId(), (Double)flow.getNumber());
					materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, null);
				}
				//重新获取 material,以供rawFlow使用
				bean.setMaterial(this.get(bean.getId()).getMaterial());
				RawBatchFlow rawFlow = new RawBatchFlow(bean.getSerial(), bean.getNumber(), bean);
				//status=1为已审核
				rawFlow.setStatus(1);
				rawFlowDao.save(rawFlow);
			}catch(Exception ex){
				throw ex;
			}
		}
		
		String hql = "delete from BatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();
		return bean;
	}
	
	//弃核（生产下料）
	public void cancelPlanMaterial(Integer planId) throws Exception{
		Plan plan = this.findById(planId);
		List<PlanStep> steps = plan.getSteps();
		if(steps!=null && steps.size()>0){
			PlanStep firstStep= steps.get(0);
			if(firstStep.getType() == 0 && firstStep.getStatus() == 1)
				throw new Exception("请先弃核生产流程");
			else if(firstStep.getType() == 1 && firstStep.getRawFlows()!=null && firstStep.getRawFlows().size()>0)
				throw new Exception(String.format("请先弃核外加工'%s'单据", firstStep.getStep().getName()));
		}
		
		List<BatchFlow> materialFlows = plan.getMaterialFlows();
		for(BatchFlow flow: materialFlows){
			flow.setStatus(0);
			//注意，这里两个方向是不一样的
			flowDao.updateLeftNumber(flow.getParent().getId(), flow.getDirect() * flow.getNumber());
			materialDao.updateNumber(flow.getMaterial().getId(), - flow.getDirect() * flow.getNumber(), null, null);
		}

		plan.setStatus(Plan.Status.approval.ordinal());
		rawFlowDao.deleteById(plan.getRawBatchFlow().getId());
	}
	
	//处理plan_detail里的，与outsideIn区分
	public Plan updateStep(Plan bean, Integer stepIndex){
		List<PlanStep> steps = bean.getSteps();
		PlanStep step = steps.get(stepIndex);
		//审核步骤
		if(step.getStatus() == 1){
			
			//因为前台传过来的bean step信息不全，所以要重新获取，考虑重新修改前台页面
			Plan plan = this.findById(bean.getId());
			
			RawBatchFlow rawFlow = plan.getRawBatchFlow();
			//从plan获取rawFlow，这里rawFlow只要做set的动作就会反映到数据库吧？
			rawFlow.setNumber(step.getNumber());
			rawFlow.setLeftNumber(step.getNumber());
			if(stepIndex == steps.size()-1)
				bean.setStatus(Plan.Status.manuFinish.ordinal());
			//step type = 1表示委外
			else if(stepIndex < steps.size() - 1 && plan.getSteps().get(stepIndex+1).getStep().getType() == 1){
				//因为有可能之前有委外而且已经处理了
				//rawFlow的状态 0未审核 1已经审核 2部分到货 3全部到货
				rawFlow.setStatus(1);
				rawFlow.setArriveNumber(0.00);
				rawFlow.setLeftNumber(rawFlow.getNumber());
				bean.setStatus(Plan.Status.outside.ordinal());
			}
		}
		
		Updater<Plan> updater = new Updater<Plan>(bean);
		updater.setUpdateMode(Updater.UpdateMode.MIN);
		updater.include("status");
		bean = updateByUpdater(updater);
		
		stepDao.updateFinish(step, stepIndex);
		
		return bean;
	}
	
	//弃核（生产流程）
	public Plan cancelPlanStep(Integer stepId) throws Exception{
		PlanStep step = stepDao.findById(stepId);
		Plan plan = step.getPlan();
		
		//这个其实可以不要，因为cancel plan step是本厂生产的
		if(step.getStep().getType() == 1 && step.getRawFlows()!=null)
			throw new Exception("请先删除相关的外加工单据");
		
		PlanStep nextStep = plan.getNextStep(step);
		if(nextStep!=null){
			if(nextStep.getStatus() == 1)
				throw new Exception(String.format("请先弃核工序'%s'", nextStep.getStep().getName()));
			else{
				if(nextStep.getStep().getType() == 1 && nextStep.getRawFlows()!=null&& nextStep.getRawFlows().size()>0)
					throw new Exception(String.format("请先弃核外加工'%s'相关的单据", nextStep.getStep().getName()));
			}
		}
			
		step.setStatus(0);
		
		//重设生产数量 ，如果是外加工的，不需要重设，因为它改不了数量
		PlanStep preStep = plan.getPreStep(step);
		if(preStep!=null){
			plan.getRawBatchFlow().setNumber(preStep.getNumber());
			plan.getRawBatchFlow().setLeftNumber(preStep.getNumber());
			plan.getRawBatchFlow().setArriveNumber(0.00);
		}else
		{
			plan.getRawBatchFlow().setNumber(plan.getNumber());
			plan.getRawBatchFlow().setLeftNumber(plan.getNumber());
			plan.getRawBatchFlow().setArriveNumber(0.00);
		}
		//先要删除package flows
		//这个地方如果先setFlows(null)，再add(flowDao.find(materialFlowType, planId))，这样是不行的，set null会反映到持久层，导致所有materialFlows和planInFlows都清空，使用flowDao查找出来都是空的
		plan.setFlows(plan.getMaterialFlows());
		
		plan.setStatus(Plan.Status.materialFinish.ordinal());
		
		String hql = "delete from BatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();
		
		return plan;
	}
	
	//update step number
	public Plan updateStepNumber(Plan bean) throws Exception{
		
		Updater<Plan> updater = new Updater<Plan>(bean);
		updater.setUpdateMode(Updater.UpdateMode.MIN);
		updater.include("steps");
		
		bean = updateByUpdater(updater);
		
		return bean;
	}
	
	public Plan updatePlanIn(Plan bean) throws Exception{

		bean.setFlows(new ArrayList<BatchFlow>());
		bean.getFlows().addAll(flowDao.getList(null, null, null, null, bean.getId(), BatchFlow.Type.planMaterial.ordinal()));
		List<BatchFlow> flows = bean.getPackageFlows();
		
		Double manuNum = 0.00;
		
		if(bean.getStatus() == Plan.Status.packageFinish.ordinal()){
			for(BatchFlow flow:flows){
				manuNum += flow.getNumber();
				//status已经在Act里设置好
				materialDao.updateNumber(flow.getMaterial().getId(), flow.getNumber(), null, null);
			}
			
			//这个地方的plan无法获取rawBatchFlow，所以用这个
			RawBatchFlow rawBatchFlow = rawFlowDao.findByPlan(bean);
			rawBatchFlow.setNumber(manuNum);
			rawBatchFlow.setLeftNumber(manuNum);
			rawBatchFlow.setArriveNumber(0.00);
		}
		
		
		bean.getFlows().addAll(flows);
		
		Updater<Plan> updater = new Updater<Plan>(bean);
		updater.setUpdateMode(Updater.UpdateMode.MIN);
		updater.include("flows");
		updater.include("status");
		//updater.include("rawBatchFlow");
		
		bean = updateByUpdater(updater);
		
		String hql = "delete from BatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();
		return bean;
	}
	
	//弃核（生产入库）
	public void cancelPlanIn(Integer planId) throws Exception{
		Plan plan = this.findById(planId);
		List<BatchFlow> packageFlows = plan.getPackageFlows();
		for(BatchFlow flow: packageFlows){
			if(flow.getFlows()!=null && flow.getFlows().size()>0){
				throw new Exception("请先删除相关联的单据" + flow.getFlowsParentSerial());
			}
		}
		
		for(BatchFlow flow: packageFlows){
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, null);
			flow.setStatus(0);
		}
		
		//重设raw batch flow
		PlanStep lastStep =plan.getSteps().get(plan.getSteps().size()-1);
		plan.getRawBatchFlow().setNumber(lastStep.getNumber());
		plan.getRawBatchFlow().setLeftNumber(lastStep.getNumber());
		plan.getRawBatchFlow().setArriveNumber(0.00);
		
		plan.setStatus(Plan.Status.manuFinish.ordinal());
	}

	public Pagination getPage(String name, String productName, Integer status, Integer pageNo, Integer pageSize) {
		Finder f = Finder.create("from Plan bean where 1=1");
		if(name != null && name != "")
		{
			f.append(" and (bean.name like :name or bean.serial like :name) ");
			f.setParam("name", "%" + name + "%");
		}
		
		if(StringUtils.isNotEmpty(productName)){
			f.append(" and (bean.material.name like :productName or bean.material.serial like :productName or bean.material.customerSerial like :productName) ");
			f.setParam("productName", "%"+ productName +"%");
		}
		
		if (status != null) {
			f.append(" and bean.status=:status");
			f.setParam("status", status);
		}
	
		f.append(" order by bean.serial desc");
		
		return find(f, pageNo, pageSize);
	}
	
	public Integer getMaxSerial(String serial){
		serial = "%"+serial+"%";
		String hql = "select max(bean.serial) from Plan bean where bean.serial like ?";
		
		//如果是findUnique(),则hql里必须使用 ?, 而不是:serial，findUnique()应该是没有定义参数名，直接顺序代入
		String max = (String)this.findUnique(hql, serial);
		Integer num = 0;
		try{
			num = Integer.valueOf(max.split("-")[2]);
		}catch(Exception ex){
		}
		return num;
	}
	
	public int countByStatus(Integer status) {
		String hql = "select count(*) from Plan bean where bean.status =:status";
		Query query = getSession().createQuery(hql);
		query.setParameter("status", status);
		return ((Number) query.iterate().next()).intValue();
	}
	
	public int countNotSideoutOut() {
		String hql = "select count(*) from Plan bean where bean.status =3 and bean.rawBatchFlow.leftNumber >0";
		Query query = getSession().createQuery(hql);
		return ((Number) query.iterate().next()).intValue();
	}
	
	public int countNotSideoutIn() {
		String hql = "select count(*) from Plan bean where bean.status =3 and bean.rawBatchFlow.type =0 and bean.rawBatchFlow.number - bean.rawBatchFlow.leftNumber - bean.rawBatchFlow.arriveNumber >0";
		Query query = getSession().createQuery(hql);
		return ((Number) query.iterate().next()).intValue();
	}
	
	@SuppressWarnings("unchecked")
	public List getList() {
		String hql = "from Plan";
		return find(hql);
	}
	
	@Override
	protected Class<Plan> getEntityClass() {
		return Plan.class;
	}
	
	@Autowired
	CirDao cirDao;
	
	@Autowired
	MaterialDao materialDao;
	
	@Autowired
	OrderRecordDao recordDao;
	
	@Autowired
	private BatchFlowDao flowDao;
	
	@Autowired
	private RawBatchFlowDao rawFlowDao;
	
	@Autowired
	private PlanStepDao stepDao;
}