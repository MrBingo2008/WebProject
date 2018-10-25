package com.berp.mrp.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.web.PageListPara;

@Controller
public class CheckAct extends CirAct {
	//check in
	@RequestMapping("/v_checkIn_list.do")
	public String checkInList(Integer useSession, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return this.cirList(useSession, searchName, searchRecordName, searchStatus, pageNum, numPerPage, "checkIn", Cir.CirType.checkIn.ordinal(), null, request, response, model);	
	}
	
	@RequestMapping("/v_checkIn_add.do")
	public String checkInAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("checkIn", 1, "PDBY", request, model);
	}
	
	@RequestMapping("/o_checkIn_save.do")
	public void checkInSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		/*if(DateUtils.getYear()!=2018){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("保存失败，系统授权过期!").toString());
			return;
		}*/
		this.cirSave(cir, Cir.CirType.checkIn.ordinal(), "v_checkIn_list.do", "查询盘点报溢单", request, response, model);
	}

	@RequestMapping("/v_checkIn_view.do")
	public String checkInView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirView(listPara, cirId, "checkIn", 1, request, model);
	}
	
	@RequestMapping("/v_checkIn_edit.do")
	public String checkInEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirEdit(listPara, cirId, "checkIn", 1, request, model);
	}
	
	@RequestMapping("/o_checkIn_update.do")
	public void checkInUpdate(PageListPara listPara, Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.checkIn.ordinal(), "v_checkIn_list.do?"+listPara.getUrlPara(), "查询盘点报溢单", request, response, model);
	}
	
	//区别于update， update调用cirDao的通用函数，然后再辨别调用业务函数
	//而这个直接调用业务函数
	@RequestMapping("/o_checkIn_cancelApproval.do")
	public void checkInCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.checkInCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_checkIn_edit.do?cirId=%d&%s",cirId, listPara.getUrlPara()), "编辑盘点报溢单").toString());
	}
	
	@RequestMapping("/o_checkIn_delete.do")
	public void checkInDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId, "v_checkIn_list.do", "查询盘点报溢", request, response, model);
	}
	
	//check out
	@RequestMapping("/v_checkOut_list.do")
	public String checkOutList(Integer useSession, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return this.cirList(useSession, searchName, searchRecordName, searchStatus, pageNum, numPerPage, "checkOut", Cir.CirType.checkOut.ordinal(), null, request, response, model);
	}
	
	@RequestMapping("/v_checkOut_add.do")
	public String checkOutAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("checkOut", 2, "PDBS", request, model);
	}
	
	@RequestMapping("/o_checkOut_save.do")
	public void checkOutSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.checkOut.ordinal(), "v_checkOut_list.do", "查询盘点报损单", request, response, model);
	}

	@RequestMapping("/v_checkOut_view.do")
	public String checkOutView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirView(listPara, cirId, "checkOut", 2, request, model);
	}
	
	@RequestMapping("/v_checkOut_edit.do")
	public String checkOutEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirEdit(listPara, cirId, "checkOut", 2, request, model);
	}
	
	@RequestMapping("/o_checkOut_update.do")
	public void checkOutUpdate(PageListPara listPara, Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.checkOut.ordinal(), "v_checkOut_list.do?"+listPara.getUrlPara(), "查询盘点报损单", request, response, model);
	}
	
	@RequestMapping("/o_checkOut_cancelApproval.do")
	public void checkOutCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.checkOutCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_checkOut_edit.do?cirId=%d&%s",cirId, listPara.getUrlPara()), "编辑盘点报损单").toString());
	}

	@RequestMapping("/o_checkOut_delete.do")
	public void checkOutDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId, "v_checkOut_list.do", "查询盘点报损单", request, response, model);
	}
}
