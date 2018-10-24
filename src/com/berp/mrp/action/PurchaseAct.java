package com.berp.mrp.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.web.PageListPara;
import com.berp.framework.util.StrUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.framework.web.session.SessionProvider;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.MaterialRecordPara;

@Controller
public class PurchaseAct extends CirAct {
	
	public static final String PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST = "purchaseOrderTodoMaterialRecordList";
	
	//purchase order
	@RequestMapping("/v_purchase_order_list.do")
	//type表示select和list模式
	public String orderList(Integer type, Integer useSession, String searchName, String searchRecordName, String searchCompanyName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return this.orderList(useSession, searchName, searchRecordName,searchCompanyName, searchStatus, pageNum, numPerPage, "purchase", type, request, response, model);
	}
	
	@RequestMapping("/v_purchase_order_add.do")
	public String orderAdd(HttpServletRequest request, ModelMap model) {
		return this.orderAdd("purchase", "CGDD", null, request, model);
	}
	
	@RequestMapping("/o_purchase_order_save.do")
	public void orderSave(Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		//if(order.getSellOrder().getId() == null)
		//	order.setSellOrder(null);
		this.orderSave(order, 1, "v_purchase_order_list.do?type=0", "查询采购订单", request, response, model);
	}

	@RequestMapping("/v_purchase_order_view.do")
	public String orderView(Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderView(orderId, "purchase", request, model);
	}
	
	@RequestMapping("/v_purchase_order_edit.do")
	public String orderEdit(Integer orderId, HttpServletRequest request, ModelMap model) {
		return this.orderEdit(orderId, "purchase", request, model);
	}
	
	@RequestMapping("/o_purchase_order_update.do")
	public void orderUpdate(Order order, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		//if(order.getSellOrder().getId() == null)
		//	order.setSellOrder(null);
		this.orderUpdate(order, 1, "v_purchase_order_list.do?type=0&useSession=1", "查询采购订单", request, response, model);
	}
	
	@RequestMapping("/o_purchase_order_cancelApproval.do")
	public void orderCancelApproval(Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderCancelApprovalBase(orderId, "v_purchase_order_edit.do?orderId="+orderId, "编辑采购订单", request, response, model);
	}
	
	@RequestMapping("/o_purchase_order_delete.do")
	public void orderDelete(Integer orderId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.orderDeleteBase(orderId, "v_purchase_order_list.do?type=0", "查询采购订单", request, response, model);
	}
	
	
	@RequestMapping("/v_purchase_order_todo_list.do")
	public String todoList(String materialIdString, String recordIdString, String numberString, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(StringUtils.isBlank(materialIdString)){
			sessionProvider.setAttribute(request, response, PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST, null);
			//sessionProvider.setAttribute(request, response, PURCHASE_ORDER_TODO_RECORD_LIST, null);
		}else{
			List<MaterialRecordPara> mrps = (List<MaterialRecordPara>)sessionProvider.getAttribute(request, PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST);
			if(mrps == null)
				mrps = new ArrayList<MaterialRecordPara>();
			Integer [] materialIds = StrUtils.getIntegersFromString(materialIdString);
			Integer [] recordIds = StrUtils.getIntegersFromString(recordIdString);
			Double [] numbers = StrUtils.getDoublesFromString(numberString);
			for(int i = 0;i< materialIds.length;i++){
				
				if(containMaterialRecord(mrps, materialIds[i], recordIds[i]))
					continue;
				
				Material m = materialDao.findById(materialIds[i]);
				OrderRecord r = recordDao.findById(recordIds[i]);
				MaterialRecordPara p = new MaterialRecordPara();
				p.setMaterialInfo(m.getNameSpec());
				p.setMaterialId(m.getId());
				p.setMaterialNumber(numbers[i]);
				p.setRecordId(r.getId());
				p.setRecordInfo(r.getInfo());
				mrps.add(p);
			}
			
			sessionProvider.setAttribute(request, response, PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST, (Serializable) mrps);
			model.addAttribute("mrps", mrps);
		}
		
		return "pages/order/purchase_todo_list";
	}
	
	private boolean containMaterialRecord(List<MaterialRecordPara> mrps, Integer materialId, Integer recordId){
		if(mrps == null || mrps.size() ==0)
			return false;
		for(MaterialRecordPara mrp:mrps){
			if(mrp.getMaterialId().equals(materialId) && mrp.getRecordId().equals(recordId))
				return true;
		}
		return false;
	}
	
	@RequestMapping("/o_purchase_order_todo_clear.do")
	public void todoClear(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		sessionProvider.setAttribute(request, response, PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST, null);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJson("成功!").toString());
	}
	
	@RequestMapping("/v_purchase_order_todo_gen.do")
	public String orderGen(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		List<MaterialRecordPara> mrps = (List<MaterialRecordPara>)sessionProvider.getAttribute(request, PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST);
		sessionProvider.setAttribute(request, response, PURCHASE_ORDER_TODO_MATERIAL_RECORD_LIST, null);
		
		Map<Integer, List<MaterialRecordPara>> materialMrps = new HashMap<Integer, List<MaterialRecordPara>>();
		for(MaterialRecordPara mrp: mrps){
			Integer materialId = mrp.getMaterialId();
			if(!materialMrps.containsKey(materialId)){
				materialMrps.put(materialId, new ArrayList<MaterialRecordPara>());
			}
			materialMrps.get(materialId).add(mrp);	
		}
		return this.orderAdd("purchase", "CGDD", materialMrps, request, model);
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
	public String purchaseInList(Integer useSession, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return this.cirList(useSession, searchName, searchRecordName, searchStatus, pageNum, numPerPage, "purchaseIn", Cir.CirType.purchaseIn.ordinal(), null, request, response, model);	
	}
	
	@RequestMapping("/o_purchaseIn_delete.do")
	public void purchaseInDelete(Integer cirId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		this.cirDeleteBase(cirId,  "v_purchaseIn_list.do", "查询采购到货单", request, response, model);
	}
	
	//purchase back
	
	@RequestMapping("/v_purchaseBack_list.do")
	public String purchaseBackList(Integer useSession, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return this.cirList(useSession, searchName, searchRecordName, searchStatus, pageNum, numPerPage, "purchaseBack", Cir.CirType.purchaseBack.ordinal(), null, request, response, model);
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
	
	@Autowired
	private SessionProvider sessionProvider;

}
