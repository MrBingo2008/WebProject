package com.berp.mrp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.entity.Cir;

@Controller
public class CheckAct extends CirAct {
	//check in
	@RequestMapping("/v_checkIn_add.do")
	public String checkInAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("checkIn", 1, "PDBY", request, model);
	}
	
	@RequestMapping("/o_checkIn_save.do")
	public void checkInSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.checkIn.ordinal(), "v_checkIn_list.do", "查询盘点报溢单", request, response, model);
	}

	@RequestMapping("/v_checkIn_view.do")
	public String checkInView(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirView(cirId, "checkIn", 1, request, model);
	}
	
	@RequestMapping("/v_checkIn_edit.do")
	public String checkInEdit(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirEdit(cirId, "checkIn", 1, request, model);
	}
	
	@RequestMapping("/o_checkIn_update.do")
	public void checkInUpdate(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.checkIn.ordinal(), "v_checkIn_list.do", "查询盘点报溢单", request, response, model);
	}
	
	@RequestMapping("/v_checkIn_list.do")
	public String checkInList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "checkIn", Cir.CirType.checkIn.ordinal(), request, model);	
	}
	
	@RequestMapping("/o_checkIn_delete.do")
	public void checkInDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId, "v_checkIn_list.do", "查询盘点报溢", request, response, model);
	}
	
	//check out
	@RequestMapping("/v_checkOut_add.do")
	public String checkOutAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("checkOut", 2, "PDBS", request, model);
	}
	
	@RequestMapping("/o_checkOut_save.do")
	public void checkOutSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.checkOut.ordinal(), "v_checkOut_list.do", "查询盘点报损单", request, response, model);
	}

	@RequestMapping("/v_checkOut_view.do")
	public String checkOutView(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirView(cirId, "checkOut", 2, request, model);
	}
	
	@RequestMapping("/v_checkOut_edit.do")
	public String checkOutEdit(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirEdit(cirId, "checkOut", 2, request, model);
	}
	
	@RequestMapping("/o_checkOut_update.do")
	public void checkOutUpdate(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.checkOut.ordinal(), "v_checkOut_list.do", "查询盘点报损单", request, response, model);
	}
	
	@RequestMapping("/v_checkOut_list.do")
	public String checkOutList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "checkOut", Cir.CirType.checkOut.ordinal(), request, model);
	}
	
	@RequestMapping("/o_checkOut_delete.do")
	public void checkOutDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId, "v_checkOut_list.do", "查询盘点报损单", request, response, model);
	}
}
