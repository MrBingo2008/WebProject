package com.berp.mrp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.CirDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.MaterialRecordPara;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.web.PageListPara;
import com.berp.core.entity.User;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.util.StrUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.RequestInfoUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.framework.web.session.SessionProvider;

public class CirAct {
	
	public static final String ORDER_SEARCH_NAME = "order_search_name";
	public static final String ORDER_SEARCH_RECORD_NAME = "order_search_record_name";
	public static final String ORDER_SEARCH_COMPANY_NAME = "order_search_company_name";
	public static final String ORDER_SEARCH_STATUS = "order_search_status";
	public static final String ORDER_PAGE_NUM = "order_page_num";
	public static final String ORDER_NUM_PER_PAGE = "order_num_per_page";
	
	public static final String CIR_SEARCH_NAME = "cir_search_name";
	public static final String CIR_SEARCH_RECORD_NAME = "cir_search_record_name";
	public static final String CIR_SEARCH_STATUS = "cir_search_status";
	public static final String CIR_PAGE_NUM = "cir_page_num";
	public static final String CIR_NUM_PER_PAGE = "cir_num_per_page";
	
	public String orderList(Integer useSession, String searchName, String searchRecordName, String searchCompanyName, Integer searchStatus, Integer pageNum, Integer numPerPage, 
			String orderType, Integer type,
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		if(useSession !=null && useSession == 1){
			searchName = (String)sessionProvider.getAttribute(request, orderType + ORDER_SEARCH_NAME);
			searchRecordName = (String)sessionProvider.getAttribute(request, orderType + ORDER_SEARCH_RECORD_NAME);
			searchCompanyName = (String)sessionProvider.getAttribute(request, orderType + ORDER_SEARCH_COMPANY_NAME);
			searchStatus = (Integer)sessionProvider.getAttribute(request, orderType + ORDER_SEARCH_STATUS);
			pageNum = (Integer)sessionProvider.getAttribute(request, orderType + ORDER_PAGE_NUM);
			numPerPage = (Integer)sessionProvider.getAttribute(request, orderType + ORDER_NUM_PER_PAGE);
		}
		
		Pagination pagination = null;
		
		//type用于区分是否完成的订单
		if(type == null)
			type = 0;
		//2018-4-22：假设type>0都是选择未完成的订单
		if(type > 0){
			pagination = orderDao.getPage(orderType.equals("purchase")?1:2, searchName, searchRecordName, searchCompanyName, Order.Status.approval.ordinal(), Order.Status.partFinish.ordinal(), pageNum, numPerPage);
		}else
			pagination = orderDao.getPage(orderType.equals("purchase")?1:2, searchName, searchRecordName, searchCompanyName, searchStatus, null, pageNum, numPerPage);
		
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("searchRecordName", searchRecordName);
		model.addAttribute("searchCompanyName", searchCompanyName);
		model.addAttribute("searchStatus", searchStatus);
		
		model.addAttribute("orderType", orderType);
		model.addAttribute("type", type);
		
		sessionProvider.setAttribute(request, response, orderType + ORDER_SEARCH_NAME, searchName);
		sessionProvider.setAttribute(request, response, orderType + ORDER_SEARCH_RECORD_NAME, searchRecordName);
		sessionProvider.setAttribute(request, response, orderType + ORDER_SEARCH_COMPANY_NAME, searchCompanyName);
		sessionProvider.setAttribute(request, response, orderType + ORDER_SEARCH_STATUS, searchStatus);
		sessionProvider.setAttribute(request, response, orderType + ORDER_PAGE_NUM, pageNum);
		sessionProvider.setAttribute(request, response, orderType + ORDER_NUM_PER_PAGE, numPerPage);
		
		return "pages/order/order_list";
	}
	
	public String orderAdd(String orderType, String serialTitle, Map<Integer, List<MaterialRecordPara>> materialMrps, HttpServletRequest request, ModelMap model) {
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
		
		if(materialMrps!=null){
			List<OrderRecord> orderRecords = new ArrayList<OrderRecord>();
			Iterator<Entry<Integer, List<MaterialRecordPara>>> iterator = materialMrps.entrySet().iterator();
			while(iterator.hasNext()){
				Map.Entry<Integer, List<MaterialRecordPara>> entry = iterator.next();
				OrderRecord record = new OrderRecord();
				Material material = materialDao.findById(entry.getKey());
				record.setMaterial(material);
				
				if(material.getCompany()!=null)
					order.setCompany(material.getCompany());
				
				Double totalNum = 0.00;
				Set sellRecords = new HashSet<OrderRecord>();
				
				for(MaterialRecordPara mrp : entry.getValue()){
					totalNum += mrp.getMaterialNumber();
					sellRecords.add(recordDao.findById(mrp.getRecordId()));
				}
				//如果数量为0，则不需要填写
				if(totalNum > 0)
					record.setNumber(totalNum);
				record.setSellRecords(sellRecords);
				
				orderRecords.add(record);
			}
			
			order.setRecords(orderRecords);
		}
		model.addAttribute("order", order);
		
		return "pages/order/order_detail";
	}
	
	public void orderSave(Order order, Integer orderType, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(order.getRecords()==null || order.getRecords().size()<=0){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("明细不能为空").toString());
			return;
		}	
		for(OrderRecord record : order.getRecords()){
			if(record.getSurface() ==null || record.getSurface().getId() == null)
				record.setSurface(null);
			record.setOrd(order);
			record.setStatus(order.getStatus());
			
			//add 2018-06-22
			if(StringUtils.isBlank(record.getIds()) == false){
				Integer [] sellRecordIds = StrUtils.getIntegersFromString(record.getIds());
				Set<OrderRecord> sellRecords = new HashSet<OrderRecord>();
				for(Integer sellRecordId : sellRecordIds){
					sellRecords.add(recordDao.findById(sellRecordId));
				}
				record.setSellRecords(sellRecords);
			}
			
		}
		order.setType(orderType);
		try{
			orderDao.save(order);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(order.getStatus()==0?"保存失败!":"审核失败!"+ex.getMessage()).toString());
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson(order.getStatus()==0?"保存成功!":"审核成功!", returnUrl, returnTitle).toString());
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
			if(record.getSurface() ==null || record.getSurface().getId() == null)
				record.setSurface(null);
			
			record.setOrd(order);
			record.setStatus(order.getStatus());
			
			//add 2018-06-22
			if(StringUtils.isBlank(record.getIds()) == false){
				Integer [] sellRecordIds = StrUtils.getIntegersFromString(record.getIds());
				Set<OrderRecord> sellRecords = new HashSet<OrderRecord>();
				for(Integer sellRecordId : sellRecordIds){
					sellRecords.add(recordDao.findById(sellRecordId));
				}
				//record.getSellRecords().clear();
				record.setSellRecords(sellRecords);
			}
		}
		
		order.setType(type);
		try{
			orderDao.update(order);
		}catch (Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("已审核的订单无法删除."+ex.getMessage()).toString());
		}
		
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson(order.getStatus()==0?"保存成功!":"审核成功!", returnUrl, returnTitle).toString());
	}
	
	//sellAct 和 purchaseAct都用到这个函数，而且都差不多
	public void orderCancelApprovalBase(Integer orderId, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model){
		try{
			orderDao.cancelApproval(orderId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", returnUrl, returnTitle).toString());
	}
	
	public void orderDeleteBase(Integer orderId, String returnUrl, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		if(orderDao.findById(orderId).getStatus() >= Order.Status.approval.ordinal()){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("已审核的订单无法删除，请先弃核.").toString());
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
	public String cirAdd(String cirType, /*String orderType,*/ Integer direction, String serialTitle, HttpServletRequest request, ModelMap model) {
		//4种可能:purchaseIn/back sellOut/back
		model.addAttribute("cirType", cirType);
		//purchase, sell
		//model.addAttribute("orderType", orderType);
		//in out
		model.addAttribute("direction", direction);
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
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson(cir.getStatus()==0?"保存成功!":"审核成功!", returnUrl, returnTitle).toString());
	}
	
	public String cirView(PageListPara listPara, Integer cirId, String cirType, Integer direction, HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", cirType);
		model.addAttribute("openMode", "view");
		model.addAttribute("direction", direction);
		
		listPara.addToModel(model);
		
		Cir cir = cirDao.findById(cirId);	
		model.addAttribute("cir", cir);
		return "pages/cir/cir_flow_detail";
	}
	
	public String cirEdit(PageListPara listPara, Integer cirId, String cirType, Integer direction, HttpServletRequest request, ModelMap model) {
		model.addAttribute("cirType", cirType);
		model.addAttribute("openMode", "edit");
		model.addAttribute("direction", direction);
		
		listPara.addToModel(model);
		
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
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson(cir.getStatus()==0?"保存成功!":"审核成功!", returnUrl, returnTitle).toString());
		
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
	/*
	public void cirCancelApprovalBase(Integer cirId, String returnUrl, String returnTitle,  HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			cirDao.cancelApproval(cirId);
		}catch(Exception ex){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("弃核失败." + ex.getMessage()).toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("弃核成功!", returnUrl, returnTitle).toString());
	}*/
	
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
	
	//采用session的做法就是在list时，保存好search和page等一些信息，在detail页面返回list时，可以使用
	//其实search和page几个参数应该还要保留，因为dwz的pagination应该还会通过form以及参数来提交
	public String cirList(Integer useSession, String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage, 
			String cirType, Integer type, Integer type1, 
			HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		if(useSession !=null && useSession == 1){
			searchName = (String)sessionProvider.getAttribute(request, CIR_SEARCH_NAME);
			searchRecordName = (String)sessionProvider.getAttribute(request, CIR_SEARCH_RECORD_NAME);
			searchStatus = (Integer)sessionProvider.getAttribute(request, CIR_SEARCH_STATUS);
			pageNum = (Integer)sessionProvider.getAttribute(request, CIR_PAGE_NUM);
			numPerPage = (Integer)sessionProvider.getAttribute(request, CIR_NUM_PER_PAGE);
		}
		
		model.addAttribute("cirType", cirType);
		Pagination pagination = cirDao.getPage(type, type1, searchName, searchRecordName, searchStatus, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("searchRecordName", searchRecordName);
		model.addAttribute("searchStatus", searchStatus);
		
		sessionProvider.setAttribute(request, response, CIR_SEARCH_NAME, searchName);
		sessionProvider.setAttribute(request, response, CIR_SEARCH_RECORD_NAME, searchRecordName);
		sessionProvider.setAttribute(request, response, CIR_SEARCH_STATUS, searchStatus);
		sessionProvider.setAttribute(request, response, CIR_PAGE_NUM, pageNum);
		sessionProvider.setAttribute(request, response, CIR_NUM_PER_PAGE, numPerPage);
		return "pages/cir/cir_list";
	}

	/*protected String getUrlPara(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage){		
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isEmpty(searchName) == false)
			sb.append("&searchName="+searchName);
		if(StringUtils.isEmpty(searchRecordName) == false)
			sb.append("&searchRecordName="+searchRecordName);
		if(searchStatus != null)
			sb.append("&searchStatus="+searchStatus);
		if(pageNum != null)
			sb.append("&pageNum="+pageNum);
		if(numPerPage != null)
			sb.append("&numPerPage="+numPerPage);
		return sb.toString();
	}*/
	
	@Autowired
	protected CirDao cirDao;
	
	@Autowired
	protected OrderDao orderDao;
	
	@Autowired
	protected OrderRecordDao recordDao;
	
	@Autowired
	protected MaterialDao materialDao;
	
	@Autowired
	private SessionProvider sessionProvider;
}
