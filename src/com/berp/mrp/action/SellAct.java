package com.berp.mrp.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Order;


@Controller
public class SellAct extends CirAct {
	
	//只需要type就可以，cirType根据它再定
	@RequestMapping("/v_sell_order_list.do")
	public String orderList(Integer type, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.orderList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "sell", type, request, model);
	}
	
	@RequestMapping("/v_sell_order_add.do")
	public String orderAdd(HttpServletRequest request, ModelMap model) {
		return this.orderAdd("sell", "KHDD", request, model);
	}
	
	@RequestMapping("/o_sell_order_save.do")
	public void orderSave(Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderSave(order, 2, "v_sell_order_list.do?type=0", "查询客户订单", request, response, model);
	}
	
	@RequestMapping("/v_sell_order_view.do")
	public String orderView(Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderView(orderId, "sell", request, model);
	}
	
	@RequestMapping("/v_sell_order_edit.do")
	public String orderEdit(Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderEdit(orderId, "sell", request, model);
	}
	
	@RequestMapping("/o_sell_order_update.do")
	public void orderUpdate(Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderUpdate(order, 2, "v_sell_order_list.do?type=0", "查询客户订单", request, response, model);
	}
	
	@RequestMapping("/o_sell_order_cancelApproval.do")
	public void orderCancelApproval(Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderCancelApprovalBase(orderId, "v_sell_order_edit.do?orderId="+orderId, "编辑客户订单", request, response, model);
	}
	
	@RequestMapping("/o_sell_order_delete.do")
	public void orderDelete(Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderDeleteBase(orderId, "v_sell_order_list.do?type=0", "查询客户订单", request, response, model);
	}
	
	//sell out
	@RequestMapping("/v_sellOut_add.do")
	public String sellOutAdd(HttpServletRequest request, ModelMap model) {
		return this.cirAdd("sellOut", /*"sell",*/ 2, "KHFH", request, model);
	}
	
	@RequestMapping("/o_sellOut_save.do")
	public void sellOutSave(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirSave(cir, Cir.CirType.sellOut.ordinal(), "v_sellOut_list.do", "查询客户发货单", request, response, model);
	}

	@RequestMapping("/v_sellOut_view.do")
	public String sellOutView(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirView(cirId, "sellOut", 2, request, model);
	}
	
	@RequestMapping("/v_sellOut_edit.do")
	public String sellOutEdit(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirEdit(cirId, "sellOut", 2, request, model);
	}
	
	@RequestMapping("/o_sellOut_update.do")
	public void sellOutUpdate(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.sellOut.ordinal(), "v_sellOut_list.do", "查询客户发货单", request, response, model);
	}
	
	@RequestMapping("/v_sellOut_list.do")
	public String sellOutList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		return this.cirList(searchName, searchRecordName, searchStatus, pageNum, numPerPage, "sellOut", Cir.CirType.sellOut.ordinal(), request, model);	
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
	public String sellBackView(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirView(cirId, "sellBack", 1, request, model);
	}
	
	@RequestMapping("/v_sellBack_edit.do")
	public String sellBackEdit(Integer cirId, HttpServletRequest request, ModelMap model) {
		return this.cirEdit(cirId, "sellBack", 1, request, model);
	}
	
	@RequestMapping("/o_sellBack_update.do")
	public void sellBackUpdate(Cir cir, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirUpdate(cir, Cir.CirType.sellBack.ordinal(), "v_sellBack_list.do", "查询客户退货单", request, response, model);
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
