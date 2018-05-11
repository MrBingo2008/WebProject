package com.berp.mrp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.entity.Order;
import com.berp.mrp.web.PageListPara;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.mrp.entity.Cir;

@Controller
public class PurchaseAct extends CirAct {
	
	//purchase order
	@RequestMapping("/v_purchase_order_list.do")
	//type表示select和list模式
	public String orderList(Integer type, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.orderList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "purchase", type, request, model);
	}
	
	@RequestMapping("/v_purchase_order_add.do")
	public String orderAdd(HttpServletRequest request, ModelMap model) {
		return this.orderAdd("purchase", "CGDD", request, model);
	}
	
	@RequestMapping("/o_purchase_order_save.do")
	public void orderSave(Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(order.getSellOrder().getId() == null)
			order.setSellOrder(null);
		this.orderSave(order, 1, "v_purchase_order_list.do?type=0", "查询采购订单", request, response, model);
	}

	@RequestMapping("/v_purchase_order_view.do")
	public String orderView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderView(searchName, searchRecordName, searchStatus, pageNum, numPerPage,orderId, "purchase", request, model);
	}
	
	@RequestMapping("/v_purchase_order_edit.do")
	public String orderEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderEdit(searchName, searchRecordName, searchStatus, pageNum, numPerPage,orderId, "purchase", request, model);
	}
	
	@RequestMapping("/o_purchase_order_update.do")
	public void orderUpdate(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(order.getSellOrder().getId() == null)
			order.setSellOrder(null);
		this.orderUpdate(order, 1, "v_purchase_order_list.do?type=0"+ this.getUrlPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage), "查询采购订单", request, response, model);
	}
	
	@RequestMapping("/o_purchase_order_cancelApproval.do")
	public void orderCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderCancelApprovalBase(orderId, "v_purchase_order_edit.do?orderId="+orderId+ this.getUrlPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage), "编辑采购订单", request, response, model);
	}
	
	@RequestMapping("/o_purchase_order_delete.do")
	public void orderDelete(Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderDeleteBase(orderId, "v_purchase_order_list.do?type=0", "查询采购订单", request, response, model);
	}
	
	//purchase in, type(direction)表示方向，统一1为进，2为出
	@RequestMapping("/v_purchaseIn_add.do")
	public String purchaseInAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("purchaseIn", /*"purchase",*/ 1, "CGDH", request, model);
	}
	
	@RequestMapping("/o_purchaseIn_save.do")
	public void purchaseInSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.purchaseIn.ordinal(), "v_purchaseIn_list.do", "查询采购到货单", request, response, model);
	}
	
	@RequestMapping("/v_purchaseIn_view.do")
	public String purchaseInView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirView(listPara, cirId, "purchaseIn", 1, request, model);
	}
	
	@RequestMapping("/v_purchaseIn_edit.do")
	public String purchaseInEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirEdit(listPara, cirId, "purchaseIn", 1, request, model);
	}
	
	@RequestMapping("/o_purchaseIn_update.do")
	public void purchaseInUpdate(PageListPara listPara, Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.purchaseIn.ordinal(), "v_purchaseIn_list.do?"+listPara.getUrlPara(), "查询采购到货单", request, response, model);
	}
	
	@RequestMapping("/o_purchaseIn_cancelApproval.do")
	public void purchaseInCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.purchaseInCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_purchaseIn_edit.do?cirId=%d&%s",cirId, listPara.getUrlPara()), "编辑采购到货单").toString());
	}
	
	@RequestMapping("/v_purchaseIn_list.do")
	public String purchaseInList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "purchaseIn", Cir.CirType.purchaseIn.ordinal(), request, model);	
	}
	
	@RequestMapping("/o_purchaseIn_delete.do")
	public void purchaseInDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId,  "v_purchaseIn_list.do", "查询采购到货单", request, response, model);
	}
	
	//purchase back
	
	@RequestMapping("/v_purchaseBack_list.do")
	public String purchaseBackList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "purchaseBack", Cir.CirType.purchaseBack.ordinal(), request, model);
	}
	
	@RequestMapping("/v_purchaseBack_add.do")
	public String purchaseBackAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("purchaseBack", /*"purchase",*/ 2, "CGTH", request, model);
	}
	
	@RequestMapping("/o_purchaseBack_save.do")
	public void purchaseBackSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.purchaseBack.ordinal(), "v_purchaseBack_list.do", "查询采购退货单", request, response, model);
	}
	
	@RequestMapping("/v_purchaseBack_view.do")
	public String purchaseBackView(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirView(listPara, cirId, "purchaseBack", 2, request, model);
	}
	
	@RequestMapping("/v_purchaseBack_edit.do")
	public String purchaseBackEdit(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		return this.cirEdit(listPara, cirId, "purchaseBack", 2, request, model);
	}
	
	@RequestMapping("/o_purchaseBack_update.do")
	public void purchaseBackUpdate(PageListPara listPara, Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.purchaseBack.ordinal(), "v_purchaseBack_list.do?"+listPara.getUrlPara(), "查询采购退货单", request, response, model);
	}
	
	@RequestMapping("/o_purchaseBack_cancelApproval.do")
	public void purchaseBackCancelApproval(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage,
			Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		PageListPara listPara = new PageListPara(searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		try{
			cirDao.purchaseBackCancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", String.format("v_purchaseBack_edit.do?cirId=%d&%s",cirId, listPara.getUrlPara()), "编辑采购退货单").toString());
	}
	
	@RequestMapping("/o_purchaseBack_delete.do")
	public void purchaseBackDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId,  "v_purchaseBack_list.do", "查询采购退货单", request, response, model);
	}
}
