package com.berp.mrp.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.BatchFlowDao;
import com.berp.mrp.dao.CirDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.dao.StepDao;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.Step;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class StatAct {
	
	@RequestMapping("/v_stat_list.do")
	public String statList(Date startDate, Date endDate, HttpServletRequest request, ModelMap model) {
		
		List<Material> materials = materialDao.getList();
		
		List<OrderRecord> records = recordDao.findByCompanyAndMaterial(null, null, null, null, 1, 3, startDate, endDate);
		for(OrderRecord record: records){
			Material material = record.getMaterial();
			if(record.getOrd().getType() == 1)
				material.setPurchaseOrder(material.getPurchaseOrder() + record.getNumber());
			else
				material.setSellOrder(material.getSellOrder() + record.getNumber());
		}

		List<BatchFlow> flows = flowDao.getListByCir(1, startDate, endDate);
		flows.addAll(flowDao.getListByPlan(null, 1, startDate, endDate));
		
		for(BatchFlow flow : flows){
			Material material = flow.getMaterial();
			if(flow.getType() == BatchFlow.Type.purchaseIn.ordinal())
				material.setPurchaseIn(material.getPurchaseIn() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.purchaseBack.ordinal())
				material.setPurchaseBack(material.getPurchaseBack() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.sellOut.ordinal())
				material.setSellOut(material.getSellOut() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.sellBack.ordinal())
				material.setSellBack(material.getSellBack() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.planMaterial.ordinal())
				material.setPlanMaterial(material.getPlanMaterial() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.planIn.ordinal())
				material.setPlanIn(material.getPlanIn() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.checkIn.ordinal())
				material.setCheckIn(material.getCheckIn() + flow.getNumber());
			
			else if(flow.getType() == BatchFlow.Type.checkOut.ordinal())
				material.setCheckOut(material.getCheckOut() + flow.getNumber());
			
			
		}
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("materials", materials);
		return "pages/queryStat/stat_list";
	}
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private OrderRecordDao recordDao;
	
	@Autowired
	private BatchFlowDao flowDao;
}
