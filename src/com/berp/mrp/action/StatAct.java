package com.berp.mrp.action;

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

import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.dao.StepDao;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.Plan;
import com.berp.mrp.entity.PlanStep;
import com.berp.mrp.entity.Step;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class StatAct {
	
	@RequestMapping("/v_stat_list.do")
	public String stepList(HttpServletRequest request, ModelMap model) {
		
		Map<Integer, Material> materialsMap = new HashMap<Integer, Material>();
		List<Material> materials = materialDao.getList();
		for(Material m : materials)
			materialsMap.put(m.getId(), m);
		
		model.addAttribute("materials", materials);
		return "pages/queryStat/stat_list";
	}
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private OrderRecordDao recordDao;
}
