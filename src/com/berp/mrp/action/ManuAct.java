package com.berp.mrp.action;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.CirDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.dao.PlanDao;
import com.berp.mrp.dao.PlanStepDao;
import com.berp.mrp.dao.ProcessDao;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.MaterialRecordPara;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.Process;
import com.berp.mrp.entity.ProcessStep;
import com.berp.mrp.entity.RawBatchFlow;
import com.berp.mrp.entity.RecordPara;
import com.berp.mrp.entity.Step;
import com.berp.mrp.web.PageListPara;
import com.dhtmlx.connector.BaseConnector;
import com.dhtmlx.connector.SchedulerConnector;
import com.dhtmlx.connector.scheduler_rec_behavior;
import com.berp.core.dao.UserDao;
import com.berp.core.entity.Category;
import com.berp.core.entity.User;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.util.StrUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.RequestInfoUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.framework.web.session.SessionProvider;


@Controller
public class ManuAct {
	public static final String PLAN_TODO_RECORD_LIST = "planTodoRecordList";
	
	@RequestMapping("/v_plan_list.do")
	public String planList(Integer pageNum, Integer numPerPage, String searchName, String searchRecordName, Integer searchStatus, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Pagination pagination = planDao.getPage(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("searchRecordName", searchRecordName);
		model.addAttribute("searchStatus", searchStatus);
		return "pages/manu/plan_list";
	}
	
	//这个可以再优化和合并
	private Process getProcess(String ids){
		
		if(StringUtils.isBlank(ids))
			return null;
		
		Integer [] recordIds = StrUtils.getIntegersFromString(ids);
		
		Material material = null;
		Step surface = null;
		for(Integer id : recordIds){
			OrderRecord orderRecord = orderRecordDao.findById(id);
			Material m = orderRecord.getMaterial();
			
			if(material == null)
				material = m;
			
			if(!material.getId().equals(m.getId())){
				//不返回个html也可以
				//ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("订单产品不一致.").toString());
				return null;
			}
			
			Step newSurface = orderRecord.getSurface();
			if(newSurface == null)
				newSurface = material.getSurface();
			
			if(surface == null)
				surface = newSurface;
			
			if(newSurface !=null){
				if(!surface.getId().equals(newSurface.getId())){
					//ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("订单产品表面处理不一致.").toString());
					return null;
				}
			}
			
		}
		
		Process process = material.getProcess();
		
		if(surface!=null){
			ProcessStep ps = new ProcessStep();
			ps.setStep(surface);
			
			//2018-5-12，如果没有生产流程，但是订单有指定的表面处理工序，此时steps就是null的，这种情况不生成流程
			//if(process.getSteps()==null)
			//	process.setSteps(new ArrayList<ProcessStep>());
			
			if(process!=null && process.getSteps()!=null && process.getSteps().size()>0){
				int lastIndex = process.getSteps().size()-1;
				ProcessStep lastStep = process.getSteps().get(lastIndex);
				if(lastStep.getStep().getSurface() == true)
					process.getSteps().remove(lastIndex);
				
				process.getSteps().add(ps);
			}
			
		}
		return process;
	}
	
	@RequestMapping("/v_process_items.do")
	public String processItems(String ids, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Process process = this.getProcess(ids);
		if(process!=null && process.getSteps()!=null)
			model.addAttribute("steps", process.getSteps());
		return "pages/manu/process_items";
	}
	
	@RequestMapping("/v_plan_todo_list.do")
	public String todoList(String recordIdString, String numberString, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(StringUtils.isBlank(recordIdString)){
			sessionProvider.setAttribute(request, response, PLAN_TODO_RECORD_LIST, null);
		}else{
			List<RecordPara> rps = (List<RecordPara>)sessionProvider.getAttribute(request, PLAN_TODO_RECORD_LIST);
			if(rps == null)
				rps = new ArrayList<RecordPara>();
			Integer [] recordIds = StrUtils.getIntegersFromString(recordIdString);
			Double [] numbers = StrUtils.getDoublesFromString(numberString);
			
			Integer existId = null;
			Integer surfaceId = null;
			
			for(int i = 0;i< recordIds.length;i++){
				
				OrderRecord r = orderRecordDao.findById(recordIds[i]);
				Integer materialId = r.getMaterial().getId(); 
				if(existId == null)
					existId = materialId;
				else if(!existId.equals(materialId))
				{
					ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("订单产品不一致.").toString());
					return null;
				}
				
				if(surfaceId == null)
					surfaceId = r.getDefaultSurfaceId();
				else if(r.getDefaultSurfaceId() != null && !surfaceId.equals(r.getDefaultSurfaceId()))
				{
					ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("订单产品表面处理不一致.").toString());
					return null;
				}
				
				if(containRecord(rps, recordIds[i]))
					continue;
				
				
				RecordPara p = new RecordPara();
				p.setRecordId(r.getId());
				p.setRecordInfo(r.getInfo());
				p.setNumber(numbers[i]);
				rps.add(p);
			}
			
			sessionProvider.setAttribute(request, response, PLAN_TODO_RECORD_LIST, (Serializable) rps);
			model.addAttribute("rps", rps);
		}
		
		return "pages/order/plan_todo_list";
	}
	
	@RequestMapping("/v_plan_todo_gen.do")
	public String orderGen(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<RecordPara> rps = (List<RecordPara>)sessionProvider.getAttribute(request, PLAN_TODO_RECORD_LIST);
		sessionProvider.setAttribute(request, response, PLAN_TODO_RECORD_LIST, null);
		return this.add(rps, request, response, model);
	}
	
	private boolean containRecord(List<RecordPara> rps, Integer recordId){
		if(rps == null || rps.size() ==0)
			return false;
		for(RecordPara rp:rps){
			if(rp.getRecordId().equals(recordId))
				return true;
		}
		return false;
	}
	
	@RequestMapping("/v_plan_add.do")
	public String planAdd(HttpServletRequest request, HttpServletResponse response, ModelMap model){
		return this.add(null, request, response, model);
	}
	
	private String add(List<RecordPara> rps, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		model.addAttribute("openMode", "add");
		
		String serial = String.format("SCRW-%s", DateUtils.getCurrentDayString());
		Integer maxSerial = planDao.getMaxSerial(serial);
		String defaultSerial = String.format("SCRW-%s-%03d", DateUtils.getCurrentDayString(), maxSerial+1);
		
		Plan plan = new Plan();
		plan.setSerial(defaultSerial);
		plan.setStatus(0);
		
		plan.setCreateTime(new Date());
		User user = RequestInfoUtils.getUser(request);
		plan.setCreateUser(user);

		if(rps!=null && rps.size()>0){
			Set sellRecords = new HashSet<OrderRecord>();
			Double num = 0.00;
			for(RecordPara rp: rps){
				OrderRecord record = orderRecordDao.findById(rp.getRecordId());
				sellRecords.add(record);
				num += rp.getNumber();
				plan.setMaterial(record.getMaterial());
			}
			plan.setNumber(num);
			plan.setSellRecords(sellRecords);
		}
		model.addAttribute("plan", plan);
		
		return "pages/manu/plan_detail";
	}

	@RequestMapping("/v_plan_view.do")
	public String planView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer planId, HttpServletRequest request, ModelMap model) {
		
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		listPara.addToModel(model);
		
		model.addAttribute("openMode", "view");
		
		Plan plan = planDao.findById(planId);
		model.addAttribute("plan", plan);
		return "pages/manu/plan_detail";
	}
	
	@RequestMapping("/v_plan_edit.do")
	public String planEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer planId, HttpServletRequest request, ModelMap model) {
		
		//一种是 return html，可以用model.add，一种是return redirect，参数只能放在url里
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		listPara.addToModel(model);
		
		model.addAttribute("openMode", "edit");
		
		Plan plan = planDao.findById(planId);
		
		List<PlanStep> steps = plan.getSteps();
		for(PlanStep step : steps){
			step.getStep().getType();
		}
		model.addAttribute("plan", plan);
		model.addAttribute("nowDate", new Date());
		return "pages/manu/plan_detail";
	}
	
	private Set<OrderRecord> getSellRecords(String ids){
		Integer [] sellRecordIds = StrUtils.getIntegersFromString(ids);
		Set<OrderRecord> sellRecords = new HashSet<OrderRecord>();
		Integer materialId = null;
		Integer surfaceId = null;
		
		for(Integer sellRecordId : sellRecordIds){
			OrderRecord record = orderRecordDao.findById(sellRecordId);
			Material material = record.getMaterial();
			if(materialId == null)
				materialId = material.getId();
			else if(!materialId.equals(material.getId()))
				return null;	
			
			if(surfaceId == null)
				surfaceId = record.getDefaultSurfaceId();
			else if(record.getDefaultSurfaceId()!=null && !surfaceId.equals(record.getDefaultSurfaceId()))
				return null;
			
			sellRecords.add(record);
		}
		return sellRecords;
	}
	
	private String saveProcess(Plan newPlan){
		Material material = materialDao.findById(newPlan.getMaterial().getId());
		String processSaveMessage = "";
		if(newPlan.getStatus() == 1 && material.getProcess() == null){
			Process process = new Process();
			process.setName(String.format("生产流程-%s(自动生成)", material.getNameSpec()));
			process.setSteps(new ArrayList<ProcessStep>());
			for(PlanStep step:newPlan.getSteps()){
				ProcessStep pStep = new ProcessStep();
				pStep.setStep(step.getStep());
				process.getSteps().add(pStep);
			}
			process.setSerial(processDao.getNextSerial());
			Process nProcess = processDao.save(process);
			material.setProcess(nProcess);
			materialDao.update(material);
			
			processSaveMessage = String.format("自动保存流程:%s。", nProcess.getName());
		}
		return processSaveMessage;
	}
	
	@RequestMapping("/o_plan_save.do")
	public void planSave(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		//这里确保plan.getIds不为空
		Set<OrderRecord> sellRecords = this.getSellRecords(plan.getIds());
		if(sellRecords == null){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("订单产品或表面处理不一致").toString());
			return;
		}
		
		plan.setSellRecords(sellRecords);
		
		if(plan.getSteps()==null || plan.getSteps().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("工艺流程不能为空").toString());
			return;
		}
		
		for(PlanStep step : plan.getSteps()){
			step.setPlan(plan);
			step.setStatus(plan.getStatus());
		}
		plan = planDao.save(plan);
		String message = this.saveProcess(plan);
		reload(response, plan.getStatus() == 0?"保存成功":"审核成功。"+message, plan.getId(), listPara);
	}
	
	@RequestMapping("/o_plan_update.do")
	public void planUpdate(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Set<OrderRecord> sellRecords = this.getSellRecords(plan.getIds());
		if(sellRecords == null){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("订单产品或表面处理不一致").toString());
			return;
		}
		
		plan.setSellRecords(sellRecords);
		
		if(plan.getSteps()==null || plan.getSteps().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("工艺流程不能为空").toString());
			return;
		}
		
		for(PlanStep step : plan.getSteps()){
			step.setPlan(plan);
			step.setStatus(plan.getStatus());
		}
		//这样获取的newPlan，里面的material.process一样是空的，所以还是要重新获取
		Plan newPlan = planDao.update(plan);
		String message = this.saveProcess(newPlan);
		reload(response, plan.getStatus() == 0?"保存成功":"审核成功。"+ message, plan.getId(), listPara);
	}
	
	
	@RequestMapping("/o_plan_cancelApproval.do")
	public void planCancelApproval(PageListPara listPara,
			Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			Plan bean = planDao.findById(plan.getId());
			if(bean.getMaterialFlows()!=null && bean.getMaterialFlows().size()>0)
				throw new Exception("请先删除下料信息");
			for(PlanStep step: bean.getSteps()){
				if(step.getStep().getType() == 0){
					if(step.getStepNumbers()!=null && step.getStepNumbers().size()>0)
						throw new Exception("请先删除生产信息");
				}else{
					if(step.getRawFlows()!=null && step.getRawFlows().size()>0)
						throw new Exception("请先删除外相关加工单据");
				}
			}
			if(bean.getPackageFlows()!=null &&  bean.getPackageFlows().size()>0)
				throw new Exception("请先删除入库信息");
			
			planDao.cancelBasic(plan.getId());
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		reload(response, "弃核成功", plan.getId(), listPara);
		//ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", "v_plan_edit.do?planId="+plan.getId(), "修改生产任务").toString());
	}
	
	@RequestMapping("/o_plan_delete.do")
	public void planDelete(Integer planId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(planDao.findById(planId).getStatus() >= Plan.Status.approval.ordinal()){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("已审核的生产任务无法删除.").toString());
			return;
		}
		try{
			planDao.deleteById(planId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_plan_list.do", "查询生产任务").toString());

	}
	
	//material update
	@RequestMapping("/o_plan_material_update.do")
	public void materialUpdate(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
		if(plan.getMaterialFlows()==null || plan.getMaterialFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("下料明细不能为空").toString());
			return;
		}
		
		for(BatchFlow flow: plan.getMaterialFlows()){
			flow.setDirect(-1);
			flow.setStatus(0);
			flow.setPlan(plan);
			flow.setType(BatchFlow.Type.planMaterial.ordinal());
		}
		
		try {
			planDao.updateMaterial(plan);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		reload(response, "保存成功", plan.getId(), listPara);
	}
	
	@RequestMapping("/o_plan_material_approval.do")
	public void materialApproval(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
		if(plan.getMaterialFlows()==null || plan.getMaterialFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("下料明细不能为空").toString());
			return;
		}
		
		if(plan.getMaterialFlows()!=null)
			for(BatchFlow flow: plan.getMaterialFlows()){
				flow.setDirect(-1);
				flow.setStatus(1);
				flow.setPlan(plan);
				flow.setType(BatchFlow.Type.planMaterial.ordinal());
			}
		plan.setStatus(Plan.Status.materialFinish.ordinal());
		
		try{
			planDao.updateMaterial(plan);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		reload(response, "审核成功", plan.getId(), listPara);
	}
	
	//step numbers update
	@RequestMapping("/o_plan_schedule_update.do")
	public void planScheduleUpdate(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {				
		try{
			planDao.updateSchedule(plan);
			
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		reload(response, "保存成功", plan.getId(), listPara);
	}
	
	//package
	@RequestMapping("/o_plan_in_update.do")
	public void planInUpdate(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
		if(plan.getPackageFlows()==null || plan.getPackageFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("入库明细不能为空").toString());
			return;
		}
		
		for(BatchFlow flow: plan.getPackageFlows()){
			flow.setPlan(plan);
			//这里不是很合理,同样的在planDao的updateMaterial也有这种情况
			flow.setMaterial(planDao.findById(plan.getId()).getMaterial());
			flow.setType(BatchFlow.Type.planIn.ordinal());
			flow.setLeftNumber(flow.getNumber());
		}
		
		try{
			planDao.updatePlanIn(plan);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		reload(response, "保存成功", plan.getId(), listPara);
	}
	
	@RequestMapping("/o_plan_in_approval.do")
	public void planInApproval(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
		if(plan.getPackageFlows()==null || plan.getPackageFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("入库明细不能为空").toString());
			return;
		}	
		for(BatchFlow flow: plan.getPackageFlows()){
			flow.setPlan(plan);
			flow.setMaterial(planDao.findById(plan.getId()).getMaterial());
			
			List<PlanStep> steps = plan.getSteps();
			if(steps!=null && steps.size()>0){
				PlanStep lstep = stepDao.findById(steps.get(steps.size()-1).getId());;
				if(lstep.getStep().getSurface() == true)
					flow.setSurface(lstep.getStep());
			}
			
			flow.setType(BatchFlow.Type.planIn.ordinal());
			flow.setLeftNumber(flow.getNumber());
			flow.setStatus(1);
		}
		
		plan.setStatus(Plan.Status.packageFinish.ordinal());
		try{
			planDao.updatePlanIn(plan);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		reload(response, "审核成功", plan.getId(), listPara);
	}

	@RequestMapping("/o_plan_in_cancel_approval.do")
	public void planInCancelApproval(PageListPara listPara, Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			if(plan.getStatus() == Plan.Status.packageFinish.ordinal())
				planDao.cancelPlanIn(plan.getId());
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		reload(response, "弃核成功", plan.getId(), listPara);	
	}
	
	private void reload(HttpServletResponse response, String text, Integer id, PageListPara para){
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson(text, String.format("v_plan_edit.do?planId=%d&%s", id, para.getUrlPara()), "修改生产任务").toString());
	}
	
	//plan schedule
	@RequestMapping("/v_plan_schedule.do")
	public String planSchedule(HttpServletRequest request, ModelMap model) {
		return "pages/manu/plan_schedule";
	}
	
	@RequestMapping("/v_plan_schedule_init.do") 
	public void init(HttpServletRequest request, HttpServletResponse response, ModelMap model)
			throws JSONException  {
		
		BaseConnector.global_http_request=request;
		BaseConnector.global_http_response = response; 
		
		Connection conn=null; 
		try {
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/berp_db", "root", "root"); 
		} catch (Throwable e) {
			e.printStackTrace(); 
		}
		
		SchedulerConnector c = new SchedulerConnector(conn);
		c.event.attach(new scheduler_rec_behavior(c));
		//c.render_table("schedule_event","event_id","start_date,end_date,event_name,details");
		c.render_sql("select * from schedule_event ","event_id","user_id,start_date,end_date,event_name,details,property,rec_type,event_pid,event_length");
		try{
			conn.close();
		}catch(Exception ex){
			ex.printStackTrace(); 
		}
	}
	
	@Autowired
	private PlanDao planDao;
	
	@Autowired
	private ProcessDao processDao;
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private OrderRecordDao orderRecordDao;
	
	@Autowired
	private PlanStepDao stepDao;
	
	@Autowired
	private SessionProvider sessionProvider;
}
