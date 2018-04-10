package com.berp.mrp.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.CirDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderDao;
import com.berp.mrp.dao.PlanDao;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Cir;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.Plan;
import com.berp.core.dao.UserDao;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class PersonalAct {
	@RequestMapping("/v_personal_tips.do")
	public String CheckInAdd(HttpServletRequest request, ModelMap model) {
		
		model.addAttribute("sellNotApprovalNumber", orderDao.countByStatus(2, Order.Status.notApproval.ordinal(), Order.Status.notApproval.ordinal()));
		model.addAttribute("sellNotFinishNumber", orderDao.countByStatus(2, Order.Status.approval.ordinal(), Order.Status.partFinish.ordinal()));
		model.addAttribute("sellOutNotApprovalNumber", cirDao.countByStatus(Cir.CirType.sellOut.ordinal(), 0, 0));
		model.addAttribute("sellBackNotApprovalNumber", cirDao.countByStatus(Cir.CirType.sellBack.ordinal(), 0, 0));
		
		model.addAttribute("purchaseNotApprovalNumber", orderDao.countByStatus(1, Order.Status.notApproval.ordinal(), Order.Status.notApproval.ordinal()));
		model.addAttribute("purchaseNotFinishNumber", orderDao.countByStatus(1, Order.Status.approval.ordinal(), Order.Status.partFinish.ordinal()));
		model.addAttribute("purchaseInNotApprovalNumber", cirDao.countByStatus(Cir.CirType.purchaseIn.ordinal(), 0, 0));
		model.addAttribute("purchaseBackNotApprovalNumber", cirDao.countByStatus(Cir.CirType.purchaseBack.ordinal(), 0, 0));
		
		model.addAttribute("planNotApprovalNumber", planDao.countByStatus(Plan.Status.edit.ordinal()));
		model.addAttribute("planNotMaterialNumber", planDao.countByStatus(Plan.Status.approval.ordinal()));
		model.addAttribute("planNotFinishNumber", planDao.countByStatus(Plan.Status.materialFinish.ordinal()));
		model.addAttribute("planNotOutsideOutNumber", planDao.countNotSideoutOut());
		model.addAttribute("planNotOutsideInNumber", planDao.countNotSideoutIn());
		model.addAttribute("planNotPackageNumber", planDao.countByStatus(Plan.Status.manuFinish.ordinal()));

		model.addAttribute("outsideOutNotApprovalNumber", cirDao.countByStatus(Cir.CirType.outsideOut.ordinal(), 0, 0));
		model.addAttribute("outsideInNotApprovalNumber", cirDao.countByStatus(Cir.CirType.outsideIn.ordinal(), 0, 0));
		
		model.addAttribute("checkInNotApprovalNumber", cirDao.countByStatus(Cir.CirType.checkIn.ordinal(), 0, 0));
		model.addAttribute("checkOutNotApprovalNumber", cirDao.countByStatus(Cir.CirType.checkOut.ordinal(), 0, 0));
		
		return "pages/personal/tips";
	}
	
	@Autowired
	private CirDao cirDao;
	
	@Autowired
	private PlanDao planDao;
	
	@Autowired
	private OrderDao orderDao;
}
