package com.berp.mrp.action;

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

import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.StepDao;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.Step;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class StepAct {
	
	//type=0 navTab list  1 plan process select  2 surface select 
	@RequestMapping("/v_step_list.do")
	public String stepList(Integer type, Integer pageNum, Integer numPerPage, String searchName, String searchStatus, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Boolean surface = null;
		if(type!=null && type == 2)
			surface = true;
		Pagination pagination = stepDao.getPage(searchName, pageNum, numPerPage, surface);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("type", type);
		return "pages/manu/step_list";
	}
	
	@RequestMapping("/v_step_multi_list.do")
	public String stepMultiList(String searchName, HttpServletRequest request, ModelMap model) {
		
		List<Step> steps = stepDao.getList(searchName);
		model.addAttribute("steps", steps);
		model.addAttribute("searchName", searchName);
		return "pages/manu/step_multi_list";
	}
	
	@RequestMapping("/v_step_add.do")
	public String stepAdd(HttpServletRequest request, ModelMap model) {
		model.addAttribute("openMode", "add");
		Step step = new Step();
		
		model.addAttribute("step", step);
		return "pages/manu/step_detail";
	}
	
	@RequestMapping("/o_step_save.do")
	public void stepSave(Step step, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		stepDao.save(step);
		reload(response);
	}

	@RequestMapping("/v_step_edit.do")
	public String stepEdit(Integer stepId, HttpServletRequest request, ModelMap model) {
		model.addAttribute("openMode", "edit");
		
		Step step = stepDao.findById(stepId);
		model.addAttribute("step", step);
		return "pages/manu/step_detail";
	}
	
	@RequestMapping("/o_step_update.do")
	public void stepUpdate(Step step, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		stepDao.update(step);
		reload(response);
	}
	
	@RequestMapping("/o_step_delete.do")
	public void stepDelete(Integer stepId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			stepDao.deleteById(stepId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_step_list.do?type=0", "工序").toString());
	}
	
	private void reload(HttpServletResponse response){
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_step_list.do?type=0", "工序").toString());
	}
	
	@Autowired
	private StepDao stepDao;
}
