package com.berp.mrp.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.CirDao;
import com.berp.mrp.dao.OrderDao;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.core.entity.User;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.RequestInfoUtils;
import com.berp.framework.web.ResponseUtils;

public class CirAct {
	
	public String orderList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, 
			String orderType, Integer type,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("orderType", orderType);
			
		Pagination pagination = orderDao.getPage(orderType.equals("purchase")?1:2, searchName, searchRecordName, searchStatus, null, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("searchRecordName", searchRecordName);
		model.addAttribute("searchStatus", searchStatus);
		model.addAttribute("type", type);
		
		return "pages/order/order_list";
	}
	
	public String orderAdd(String orderType, String serialTitle, HttpServletRequest request, ModelMap model) {
		model.addAttribute("orderType", orderType);
		model.addAttribute("openMode", "add");
		
		String serial = String.format("%s-%s", serialTitle, DateUtils.getCurrentDayString());
		Integer maxSerial = orderDao.getMaxSerial(serial);
		String defaultSerial = String.format("%s-%s-%03d",serialTitle, DateUtils.getCurrentDayString(), maxSerial+1);
		
		Order order = new Order();
		order.setCreateTime(new Date());
		
		User user = RequestInfoUtils.getUser(request);
		order.setCreateUser(user);
		
		order.setSerial(defaultSerial);
		
		model.addAttribute("order", order);
		
		return "pages/order/order_detail";
	}
	
	public void orderSave(Order order, Integer type, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(order.getRecords()==null || order.getRecords().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}	
		for(OrderRecord record : order.getRecords()){
			record.setOrd(order);
			record.setStatus(order.getStatus());
		}
		order.setType(type);
		orderDao.save(order);
		
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", returnUrl, returnTitle).toString());
	}

	public String orderView(Integer orderId, String orderType, HttpServletRequest request, ModelMap model) {
		model.addAttribute("orderType", orderType);
		model.addAttribute("openMode", "view");
		
		model.addAttribute("order", orderDao.findById(orderId));
		return "pages/order/order_detail";
	}
	
	public String orderEdit(Integer orderId, String orderType, HttpServletRequest request, ModelMap model) {
		model.addAttribute("orderType", orderType);
		model.addAttribute("openMode", "edit");
		
		model.addAttribute("order", orderDao.findById(orderId));
		return "pages/order/order_detail";
	}
	
	public void orderUpdate(Order order, Integer type, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		if(order.getRecords()==null || order.getRecords().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}	
		
		for(OrderRecord record : order.getRecords()){
			record.setOrd(order);
			record.setStatus(order.getStatus());
		}
		
		order.setType(type);
		orderDao.update(order);
		
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", returnUrl, returnTitle).toString());
	}
	
	public void orderDeleteBase(Integer orderId, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(orderDao.findById(orderId).getStatus() >= Order.Status.approval.ordinal()){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("已审核的订单无法删除.").toString());
			return;
		}
		try{
			orderDao.deleteById(orderId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", returnUrl, returnTitle).toString());
	}
	
	//cir add
	public String cirAdd(String cirType, /*String orderType,*/ Integer materialSelectType, String serialTitle, HttpServletRequest request, ModelMap model) {
		//4种可能:purchaseIn/back sellOut/back
		model.addAttribute("cirType", cirType);
		//purchase, sell
		//model.addAttribute("orderType", orderType);
		//in out
		model.addAttribute("materialSelectType", materialSelectType);
		model.addAttribute("openMode", "add");
		
		String serial = String.format("%s-%s", serialTitle, DateUtils.getCurrentDayString());
		Integer maxSerial = cirDao.getMaxSerial(serial);
		String defaultSerial = String.format("%s-%s-%03d", serialTitle, DateUtils.getCurrentDayString(), maxSerial+1);
		
		Cir cir = new Cir();
		cir.setSerial(defaultSerial);
		
		cir.setCreateTime(new Date());
		User user = RequestInfoUtils.getUser(request);
		cir.setCreateUser(user);
		
		model.addAttribute("cir", cir);
		return "pages/cir/cir_flow_detail";
	}
	
	public void cirSave(Cir cir, Integer type, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cir.getFlows()==null || cir.getFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		
		for(BatchFlow batchFlow : cir.getFlows()){
			setBatchFlow(batchFlow, type, batchFlow.getNumber(), cir.getStatus());	
			batchFlow.setCir(cir);
		}
		
		cir.setType(type);
		try{
			cirDao.save(cir);
		}catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", returnUrl, returnTitle).toString());
	}
	
	public String cirView(Integer cirId, String cirType, Integer materialSelectType, HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", cirType);
		model.addAttribute("openMode", "view");
		model.addAttribute("materialSelectType", materialSelectType);
		
		Cir cir = cirDao.findById(cirId);	
		model.addAttribute("cir", cir);
		return "pages/cir/cir_flow_detail";
	}
	
	public String cirEdit(Integer cirId, String cirType, Integer materialSelectType, HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", cirType);
		model.addAttribute("openMode", "edit");
		model.addAttribute("materialSelectType", materialSelectType);
		
		Cir cir = cirDao.findById(cirId);	
		model.addAttribute("cir", cir);
		return "pages/cir/cir_flow_detail";
	}
	
	public void cirUpdate(Cir cir, Integer type, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cir.getFlows()==null || cir.getFlows().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}
		
		for(BatchFlow batchFlow : cir.getFlows()){
			setBatchFlow(batchFlow, type, batchFlow.getNumber(), cir.getStatus());	
			batchFlow.setCir(cir);
		}
		
		cir.setType(type);
		try{
			cirDao.update(cir);
		}catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", returnUrl, returnTitle).toString());
		
	}
	
	private void setBatchFlow(BatchFlow flow, Integer cirType, Double number, Integer status){
		if(cirType==Cir.CirType.purchaseIn.ordinal()){
			flow.setDirect(1);
			flow.setLeftNumber(flow.getNumber());
			flow.setType(BatchFlow.Type.purchaseIn.ordinal());
		}
		else if(cirType==Cir.CirType.purchaseBack.ordinal()){
			flow.setDirect(-1);
			flow.setType(BatchFlow.Type.purchaseBack.ordinal());
		}
		else if(cirType==Cir.CirType.sellOut.ordinal()){
			flow.setDirect(-1);
			flow.setType(BatchFlow.Type.sellOut.ordinal());
		}
		else if(cirType==Cir.CirType.sellBack.ordinal()){
			flow.setDirect(1);
			flow.setLeftNumber(flow.getNumber());
			flow.setType(BatchFlow.Type.sellBack.ordinal());
		}
		else if(cirType==Cir.CirType.checkIn.ordinal()){
			flow.setDirect(1);
			flow.setLeftNumber(flow.getNumber());
			flow.setType(BatchFlow.Type.checkIn.ordinal());
		}
		else if(cirType==Cir.CirType.checkOut.ordinal()){
			flow.setDirect(-1);
			flow.setType(BatchFlow.Type.checkOut.ordinal());
		}
		flow.setStatus(status);
	}
	
	public void cirDeleteBase(Integer cirId, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(cirDao.findById(cirId).getStatus() >= 1){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("已审核的订单无法删除.").toString());
			return;
		}
		try{
			cirDao.deleteById(cirId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", returnUrl, returnTitle).toString());
	}
	
	public String cirList(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, 
			String cirType, Integer type,
			HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", cirType);
		Pagination pagination = cirDao.getPage(type, searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("searchRecordName", searchRecordName);
		model.addAttribute("searchStatus", searchStatus);
		return "pages/cir/cir_list";
	}

	@Autowired
	protected CirDao cirDao;
	
	@Autowired
	protected OrderDao orderDao;
}
