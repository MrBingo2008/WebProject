package com.berp.mrp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.Process;
import com.berp.mrp.entity.ProcessStep;
import com.berp.mrp.entity.RawBatchFlow;
import com.berp.mrp.entity.Step;
import com.berp.core.dao.UserDao;
import com.berp.core.entity.Category;
import com.berp.core.entity.User;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.RequestInfoUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class ManuAct {
	
	@RequestMapping("/v_plan_gen_list.do")
	public String planGenList(Integer pageNum, Integer numPerPage, String searchName, HttpServletRequest request, ModelMap model) {
		Pagination pagination = orderRecordDao.getPage(2, searchName, 1, 2, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		return "pages/manu/plan_gen_list";
	}

	@RequestMapping("/v_plan_list.do")
	public String planList(Integer pageNum, Integer numPerPage, String searchName, String searchProductName, Integer searchStatus, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Pagination pagination = planDao.getPage(searchName, searchProductName, searchStatus, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("searchProductName", searchProductName);
		model.addAttribute("searchStatus", searchStatus);
		return "pages/manu/plan_list";
	}
	
	@RequestMapping("/v_process_items.do")
	public String processItems(Integer recordId,  String test, HttpServletRequest request, ModelMap model) {
		OrderRecord orderRecord = orderRecordDao.findById(recordId);
		Material material = orderRecord.getMaterial();
		
		Process process = new Process();
		List<Category> parents = material.getParent().getNodeList();
		for(Category category : parents){
			List<Process> processes = processDao.getListByCategory(category.getId());
			if(processes != null && processes.size()>0){
				process = processes.get(0);
				break;
			}
		}
		
		List<PlanStep> steps = new ArrayList<PlanStep>();
		if(process.getSteps()!=null)
			for(ProcessStep step: process.getSteps()){
				PlanStep ps = new PlanStep();
				ps.setName(step.getName());
				ps.setType(step.getType());
				steps.add(ps);
			}
	    Step surface = orderRecord.getSurface()!=null?orderRecord.getSurface():material.getSurface();
		if(surface!=null)
		{
			PlanStep ps1 = new PlanStep();
			ps1.setName(surface.getName());
			ps1.setType(surface.getType());
			steps.add(ps1);
		}
		model.addAttribute("steps", steps);
		return "pages/manu/process_items";
	}
	
	@RequestMapping("/v_plan_gen.do")
	public String planGen(Integer orderRecordId, Integer materialId, HttpServletRequest request, ModelMap model) {
		model.addAttribute("openMode", "add");
		
		String serial = String.format("SCRW-%s", DateUtils.getCurrentDayString());
		Integer maxSerial = planDao.getMaxSerial(serial);
		String defaultSerial = String.format("SCRW-%s-%03d", DateUtils.getCurrentDayString(), maxSerial+1);
		
		OrderRecord orderRecord = orderRecordDao.findById(orderRecordId);
		Material material = materialDao.findById(materialId);
		
		Process process = new Process();
		List<Category> parents = material.getParent().getNodeList();
		for(Category category : parents){
			List<Process> processes = processDao.getListByCategory(category.getId());
			if(processes != null && processes.size()>0){
				process = processes.get(0);
				break;
			}
		}
		
		Plan plan = new Plan();
		plan.setSerial(defaultSerial);
		plan.setStatus(0);
		
		plan.setCreateTime(new Date());
		User user = RequestInfoUtils.getUser(request);
		plan.setCreateUser(user);
		
		plan.setMaterial(material);
		plan.setRecord(orderRecord);
		plan.setNumber(orderRecord.getNumber());
		
		List<PlanStep> steps = new ArrayList<PlanStep>();
		if(process.getSteps()!=null)
			for(ProcessStep step: process.getSteps()){
				PlanStep ps = new PlanStep();
				ps.setName(step.getName());
				ps.setType(step.getType());
				steps.add(ps);
			}
		plan.setSteps(steps);
		model.addAttribute("plan", plan);
		
		return "pages/manu/plan_detail";
	}
	
	@RequestMapping("/v_plan_add.do")
	public String planAdd(Integer orderRecordId, Integer materialId, HttpServletRequest request, ModelMap model) {
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

		model.addAttribute("plan", plan);
		
		return "pages/manu/plan_detail";
	}

	@RequestMapping("/v_plan_view.do")
	public String planView(Integer planId, HttpServletRequest request, ModelMap model) {
		
		model.addAttribute("openMode", "view");
		
		Plan plan = planDao.findById(planId);
		model.addAttribute("plan", plan);
		return "pages/manu/plan_detail";
	}
	
	@RequestMapping("/v_plan_edit.do")
	public String planEdit(Integer planId, HttpServletRequest request, ModelMap model) {
		
		model.addAttribute("openMode", "edit");
		
		Plan plan = planDao.findById(planId);
		model.addAttribute("plan", plan);
		model.addAttribute("nowDate", new Date());
		return "pages/manu/plan_detail";
	}
	
	@RequestMapping("/o_plan_save.do")
	public void planSave(Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(plan.getSteps()==null || plan.getSteps().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("工艺流程不能为空").toString());
			return;
		}
		
		for(PlanStep step : plan.getSteps()){
			step.setPlan(plan);
		}
		plan = planDao.save(plan);

		reload(response, plan.getId());
	}
	
	@RequestMapping("/o_plan_update.do")
	public void planUpdate(Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(plan.getSteps()==null || plan.getSteps().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("工艺流程不能为空").toString());
			return;
		}
		
		for(PlanStep step : plan.getSteps()){
			step.setPlan(plan);
		}
		planDao.update(plan);

		reload(response, plan.getId());
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
	public void materialUpdate(Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
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
		reload(response, plan.getId());
	}
	
	@RequestMapping("/o_plan_material_approval.do")
	public void materialApproval(Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
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
		reload(response, plan.getId());
	}
	
	//step update
	@RequestMapping("/o_plan_step_update.do")
	public void stepUpdate(Plan plan, Integer stepNo, HttpServletRequest request, HttpServletResponse response, ModelMap model) {				
		
		planDao.updateStep(plan, stepNo);
		reload(response, plan.getId());
	}

	//package
	@RequestMapping("/o_plan_in_update.do")
	public void planInUpdate(Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
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
		
		reload(response, plan.getId());
	}
	
	@RequestMapping("/o_plan_in_approval.do")
	public void planInApproval(Plan plan, HttpServletRequest request, HttpServletResponse response, ModelMap model) {		
		if(plan.getPackageFlows()==null || plan.getPackageFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("入库明细不能为空").toString());
			return;
		}	
		for(BatchFlow flow: plan.getPackageFlows()){
			flow.setPlan(plan);
			flow.setMaterial(planDao.findById(plan.getId()).getMaterial());
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
		reload(response, plan.getId());
	}
	
	private void reload(HttpServletResponse response, Integer id){
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_plan_edit.do?planId="+id, "修改生产任务").toString());
	}
	
	@Autowired
	private PlanDao planDao;
	
	@Autowired
	private ProcessDao processDao;
	
	@Autowired
	private OrderRecordDao orderRecordDao;
	
	@Autowired
	private MaterialDao materialDao;
}
