package com.berp.mrp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.entity.Process;
import com.berp.mrp.entity.ProcessStep;
import com.berp.mrp.dao.ProcessDao;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class ProcessAct {
	//type=0 navTab list, >0 dialog
	@RequestMapping("/v_process_list.do")
	public String processList(Integer type, String searchName, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		Pagination pagination = processDao.getPage(searchName, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("type", type == null?0:type);
		return "pages/manu/process_list";
	}
	/*
	@RequestMapping("/v_process_list_select.do")
	public String processListSelect(HttpServletRequest request, ModelMap model) {
		Pagination p = processDao.getPage(null, null, null);
		model.addAttribute("pagination", p);
		return "pages/manu/process_list_select";
	}*/
	
	@RequestMapping("/v_process_add.do")
	public String processAdd(HttpServletRequest request, ModelMap model) {
		Process process = new Process();
		process.setSerial(processDao.getNextSerial());
		model.addAttribute("process", process);
		model.addAttribute("openMode", "add");
		return "pages/manu/process_detail";
	}
	
	@RequestMapping("/o_process_save.do")
	public void processSave(Process process, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(process.getSteps()==null || process.getSteps().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		for(ProcessStep step : process.getSteps())
			step.setProcess(process);
		processDao.save(process);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功", "v_process_list.do", "工艺流程").toString());
	}
	
	@RequestMapping("/v_process_edit.do")
	public String processEdit(Integer processId, HttpServletRequest request, ModelMap model) {
		Process process = processDao.findById(processId);
		model.addAttribute("process", process);
		model.addAttribute("openMode", "edit");
		return "pages/manu/process_detail";
	}
	
	@RequestMapping("/o_process_update.do")
	public void processUpdate(Process process, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(process.getSteps()==null || process.getSteps().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		for(ProcessStep step : process.getSteps())
			step.setProcess(process);
		processDao.update(process);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功", "v_process_list.do", "工艺流程").toString());
	}
	
	@RequestMapping("/o_process_delete.do")
	public void processDelete(Integer processId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			processDao.deleteById(processId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_process_list.do", "工艺流程").toString());
	}
	
	@Autowired
	private ProcessDao processDao;
}
