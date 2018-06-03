package com.berp.mrp.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.framework.web.session.SessionProvider;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Order;
import com.berp.mrp.web.PageListPara;


@Controller
public class SellAct extends CirAct {
	
	//只需要type就可以，cirType根据它再定
	@RequestMapping("/v_sell_order_list.do")
	public String orderList(Integer type, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		//其实type可以不用作为参数
		return this.orderList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "sell", type, request, model);
	}
	
	@RequestMapping("/v_sell_order_add.do")
	public String orderAdd(HttpServletRequest request, ModelMap model) {
		return this.orderAdd("sell", "KHDD", request, model);
	}
	
	@RequestMapping("/o_sell_order_save.do")
	public void orderSave(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderSave(order, 2, 
				"v_sell_order_list.do?type=0&"+ this.getUrlPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage), 
				"查询客户订单", request, response, model);
	}
	
	@RequestMapping("/v_sell_order_view.do")
	public String orderView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderView(searchName, searchRecordName, searchStatus, pageNum, numPerPage,
				orderId, "sell", request, model);
	}
	
	@RequestMapping("/v_sell_order_edit.do")
	public String orderEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderEdit(searchName, searchRecordName, searchStatus, pageNum, numPerPage,
				orderId, "sell", request, model);
	}
	
	@RequestMapping("/o_sell_order_update.do")
	public void orderUpdate(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderUpdate(order, 2,
				"v_sell_order_list.do?type=0&"+ this.getUrlPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage),
				"查询客户订单", request, response, model);
	}
	
	@RequestMapping("/o_sell_order_cancelApproval.do")
	public void orderCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String url = "v_sell_order_edit.do?orderId="+orderId + this.getUrlPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		this.orderCancelApprovalBase(orderId, url , "编辑客户订单", request, response, model);
	}
	
	@RequestMapping("/o_sell_order_delete.do")
	public void orderDelete(Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		String url = "v_sell_order_list.do?type=0";//+ this.getUrlPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		//不需要参数，因为前台是ajax请求，会获取pagerForm的data一起提交，所以参数还在。如果加参数searchStatus, pageNum, numPerPage的话，会出现错误，中间有个逗号，说明有同名参数
		this.orderDeleteBase(orderId, url, "查询客户订单", request, response, model); 		
	}
	
	//sell out
	@RequestMapping("/v_sellOut_list.do")
	public String sellOutList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "sellOut", Cir.CirType.sellOut.ordinal(), request, model);	
	}
	
	@RequestMapping("/v_sellOut_add.do")
	public String sellOutAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("sellOut", /*"sell",*/ 2, "KHFH", request, model);
	}
	
	@RequestMapping("/o_sellOut_save.do")
	public void sellOutSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.sellOut.ordinal(), "v_sellOut_list.do", "查询客户发货单", request, response, model);
	}

	@RequestMapping("/v_sellOut_view.do")
	public String sellOutView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirView(listPara, cirId, "sellOut", 2, request, model);
	}
	
	@RequestMapping("/v_sellOut_edit.do")
	public String sellOutEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirEdit(listPara, cirId, "sellOut", 2, request, model);
	}
	
	@RequestMapping("/o_sellOut_update.do")
	public void sellOutUpdate(PageListPara listPara, Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.sellOut.ordinal(), "v_sellOut_list.do?"+listPara.getUrlPara(), "查询客户发货单", request, response, model);
	}
	
	@RequestMapping("/o_sellOut_cancelApproval.do")
	public void sellOutCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.sellOutCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_sellOut_edit.do?cirId=%d&%s",cirId, listPara.getUrlPara()), "编辑客户发货单").toString());
	}
	
	@RequestMapping("/o_sellOut_delete.do")
	public void sellOutDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId,  "v_sellOut_list.do", "查询客户发货单", request, response, model);
	}
	
	//sell back
	@RequestMapping("/v_sellBack_add.do")
	public String sellBackAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("sellBack", /*"sell",*/ 1, "KHTH", request, model);
	}
	
	@RequestMapping("/o_sellBack_save.do")
	public void sellBackSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.sellBack.ordinal(), "v_sellBack_list.do", "查询客户退货单", request, response, model);
	}
	
	@RequestMapping("/v_sellBack_view.do")
	public String sellBackView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirView(listPara, cirId, "sellBack", 1, request, model);
	}
	
	@RequestMapping("/v_sellBack_edit.do")
	public String sellBackEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirEdit(listPara, cirId, "sellBack", 1, request, model);
	}
	
	@RequestMapping("/o_sellBack_update.do")
	public void sellBackUpdate(PageListPara listPara, Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.sellBack.ordinal(), "v_sellBack_list.do?"+listPara.getUrlPara(), "查询客户退货单", request, response, model);
	}
	
	@RequestMapping("/o_sellBack_cancelApproval.do")
	public void sellBackCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.sellBackCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_sellBack_edit.do?cirId=%d&%s",cirId, listPara.getUrlPara()), "编辑客户退货单").toString());
	}
	
	@RequestMapping("/v_sellBack_list.do")
	public String sellBackList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "sellBack", Cir.CirType.sellBack.ordinal(), request, model);
	}
	
	@RequestMapping("/o_sellBack_delete.do")
	public void sellBackDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId, "v_sellBack_list.do", "查询客户退货单", request, response, model);
	}
	
}
