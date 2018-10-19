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
public class PlanDao extends HibernateBaseDao<Plan, Integer> {

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
	
	//update step number
	public Plan updateSchedule(Plan bean) throws Exception{
		Plan plan = this.findById(bean.getId());
		
		List<BatchFlow> materialFlows = plan.getMaterialFlows();
		
		for(BatchFlow flow: materialFlows){
			//注意，这里两个方向是不一样的
			flowDao.updateLeftNumber(flow.getParent().getId(), flow.getDirect() * flow.getNumber());
			materialDao.updateNumber(flow.getMaterial().getId(), - flow.getDirect() * flow.getNumber(), null, null);
			flowDao.deleteById(flow.getId());
		}
		
		plan.setFlows(new ArrayList<BatchFlow>());
		List<BatchFlow> flows = bean.getMaterialFlows();
		for(BatchFlow flow : flows){
			flow.setStatus(1);
			flow.setDirect(-1);
			//flow.setPlan();
			flow.setType(BatchFlow.Type.planMaterial.ordinal());
			flowDao.updateLeftNumber(flow.getParent().getId(), (Double)flow.getNumber());
			materialDao.updateNumber(flow.getMaterial().getId(), -flow.getNumber(), null, null);
			flowDao.save(flow);
		}
		
		plan.getFlows().addAll(bean.getMaterialFlows());
		/*
		String hql = "delete from BatchFlow bean where bean.plan.id is null and bean.cir.id is null";
		getSession().createQuery(hql).executeUpdate();*/
		
		return plan;
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