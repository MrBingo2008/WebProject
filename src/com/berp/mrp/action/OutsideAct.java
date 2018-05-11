package com.berp.mrp.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.CirDao;
import com.berp.mrp.entity.RawBatchFlow;
import com.berp.mrp.web.PageListPara;
import com.berp.mrp.entity.Cir;
import com.berp.core.dao.UserDao;
import com.berp.core.entity.User;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.RequestInfoUtils;
import com.berp.framework.web.ResponseUtils;

@Controller
public class OutsideAct extends CirAct {
	
	@RequestMapping("/v_outsideOut_add.do")
	public String outsideOutAdd(HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", "outsideOut");
		model.addAttribute("type", 1);
		model.addAttribute("openMode", "add");
		
		String serial = String.format("WJGCH-%s", DateUtils.getCurrentDayString());
		Integer maxSerial = cirDao.getMaxSerial(serial);
		String defaultSerial = String.format("WJGCH-%s-%03d", DateUtils.getCurrentDayString(), maxSerial+1);
		
		Cir cir = new Cir();
		cir.setSerial(defaultSerial);
		
		cir.setCreateTime(new Date());
		User user = RequestInfoUtils.getUser(request);
		cir.setCreateUser(user);
		
		model.addAttribute("cir", cir);
		return "pages/cir/cir_outside_detail";
	}

	@RequestMapping("/o_outsideOut_save.do")
	public void outsideOutSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cir.getRawFlows()==null || cir.getRawFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		for(RawBatchFlow flow : cir.getRawFlows()){
			flow.setType(RawBatchFlow.Type.outsideOut.ordinal());
			flow.setDirect(-1);
			flow.setCir(cir);
			flow.setStatus(cir.getStatus());
		}
		cir.setType(Cir.CirType.outsideOut.ordinal());
		try{
			cir = cirDao.save(cir);
		}catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		JSONObject object = DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_outsideOut_list.do", "查询外加工出货单");
		ResponseUtils.renderJson(response, object.toString());
	}

	@RequestMapping("/v_outsideOut_view.do")
	public String outsideOutView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		listPara.addToModel(model);
		
		model.addAttribute("cirType", "outsideOut");
		model.addAttribute("type", 1);
		model.addAttribute("openMode", "view");
		Cir cir = cirDao.findById(cirId);
		model.addAttribute("cir", cir);
		return "pages/cir/cir_outside_detail";
	}
	
	@RequestMapping("/v_outsideOut_edit.do")
	public String outsideOutEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		listPara.addToModel(model);
		
		model.addAttribute("cirType", "outsideOut");
		model.addAttribute("type", 1);
		
		model.addAttribute("openMode", "edit");
		Cir cir = cirDao.findById(cirId);
		model.addAttribute("cir", cir);
		return "pages/cir/cir_outside_detail";
	}
	
	@RequestMapping("/o_outsideOut_update.do")
	public void outsideOutUpdate(PageListPara listPara,Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cir.getRawFlows()==null || cir.getRawFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		for(RawBatchFlow flow : cir.getRawFlows()){
			flow.setType(RawBatchFlow.Type.outsideOut.ordinal());
			flow.setDirect(-1);
			flow.setCir(cir);
			flow.setStatus(cir.getStatus());
		}
		try{
			cir.setType(Cir.CirType.outsideOut.ordinal());
			cir = cirDao.update(cir);
		}catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		
		JSONObject object = DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_outsideOut_list.do?"+listPara.getUrlPara(), "查询外加工出货单");
		ResponseUtils.renderJson(response, object.toString());
	}
	
	@RequestMapping("/o_outsideOut_cancelApproval.do")
	public void outsideOutCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.outsideOutCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_outsideOut_edit.do?cirId=%d&%s", cirId, listPara.getUrlPara()), "编辑外加工出货单").toString());
	}
	
	@RequestMapping("/o_outsideOut_delete.do")
	public void outsideOutDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			cirDao.deleteById(cirId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_outsideOut_list.do", "查询外加工出货单").toString());
	}
	
	@RequestMapping("/v_outsideOut_list.do")
	public String outsideOutList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "outsideOut", Cir.CirType.outsideOut.ordinal(), request, model);
	}
	
	//outsideIn
	@RequestMapping("/v_outsideIn_add.do")
	public String outsideInAdd(HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", "outsideIn");
		model.addAttribute("type", 2);
		
		model.addAttribute("openMode", "add");
		
		String serial = String.format("WJGDH-%s", DateUtils.getCurrentDayString());
		Integer maxSerial = cirDao.getMaxSerial(serial);
		String defaultSerial = String.format("WJGDH-%s-%03d", DateUtils.getCurrentDayString(), maxSerial+1);
		
		Cir cir = new Cir();
		cir.setSerial(defaultSerial);
		
		cir.setCreateTime(new Date());
		User user = RequestInfoUtils.getUser(request);
		cir.setCreateUser(user);
		
		model.addAttribute("cir", cir);
		return "pages/cir/cir_outside_detail";
	}

	@RequestMapping("/o_outsideIn_save.do")
	public void outsideInSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cir.getRawFlows()==null || cir.getRawFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		for(RawBatchFlow flow : cir.getRawFlows()){
			flow.setType(RawBatchFlow.Type.outsideIn.ordinal());
			flow.setCir(cir);
			flow.setStatus(cir.getStatus());
		}
		cir.setType(Cir.CirType.outsideIn.ordinal());
		try{
			cir = cirDao.save(cir);
		}catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		JSONObject object = DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_outsideIn_list.do", "查询外加工到货单");
		ResponseUtils.renderJson(response, object.toString());
	}
	
	@RequestMapping("/v_outsideIn_view.do")
	public String outsideInView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		listPara.addToModel(model);
		
		model.addAttribute("cirType", "outsideIn");
		model.addAttribute("type", 2);
		model.addAttribute("openMode", "view");
		Cir cir = cirDao.findById(cirId);
		model.addAttribute("cir", cir);
		return "pages/cir/cir_outside_detail";
	}
	
	@RequestMapping("/v_outsideIn_edit.do")
	public String purchaseInEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		listPara.addToModel(model);
		
		model.addAttribute("cirType", "outsideIn");
		model.addAttribute("type", 2);
		
		model.addAttribute("openMode", "edit");
		Cir cir = cirDao.findById(cirId);
		model.addAttribute("cir", cir);
		return "pages/cir/cir_outside_detail";
	}
	
	@RequestMapping("/o_outsideIn_update.do")
	public void outsideInUpdate(PageListPara listPara,Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cir.getRawFlows()==null || cir.getRawFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		for(RawBatchFlow flow : cir.getRawFlows()){
			flow.setType(RawBatchFlow.Type.outsideIn.ordinal());
			flow.setCir(cir);
			flow.setStatus(cir.getStatus());
		}
		try{
			cir.setType(Cir.CirType.outsideIn.ordinal());
			cir = cirDao.update(cir);
		}catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		
		JSONObject object = DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_outsideIn_list.do?"+listPara.getUrlPara(), "查询外加工到货单");
		ResponseUtils.renderJson(response, object.toString());
	}

	@RequestMapping("/o_outsideIn_cancelApproval.do")
	public void outsideInCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.outsideInCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_outsideIn_edit.do?cirId=%d&%s", cirId, listPara.getUrlPara()), "编辑外加工到货单").toString());
	}
	
	@RequestMapping("/o_outsideIn_delete.do")
	public void outsideInDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			cirDao.deleteById(cirId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_outsideIn_list.do", "查询外加工到货单").toString());
	}
	
	@RequestMapping("/v_outsideIn_list.do")
	public String outsideInList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "outsideIn", Cir.CirType.outsideIn.ordinal(), request, model);
	}
}
