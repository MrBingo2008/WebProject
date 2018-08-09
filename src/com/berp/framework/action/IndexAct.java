package com.berp.framework.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.core.dao.CategoryDao;
import com.berp.core.entity.Category;
import com.berp.core.entity.User;
import com.berp.framework.web.ModelUtils;
import com.berp.framework.web.RequestInfoUtils;

@Controller
public class IndexAct {

	@RequestMapping("/index.do")
	public String index(HttpServletRequest request, ModelMap model) {
		
		User user = RequestInfoUtils.getUser(request);
		
		//前端变量都在controller里定义，在login.do(get)那里也有定义！
		ModelUtils.frontData(model, user);
		
		Category category = categoryDao.findById(2);
		JSONObject object = categoryDao.getCategoryTree(category);
		String test = object.toString();
		model.addAttribute("tree", test);
		
		return "index";
	}

	@RequestMapping("/v_personal.do")
	public String personal(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_personal";
	}

	@RequestMapping("/v_sell.do")
	public String order(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_sell";
	}
	
	@RequestMapping("/v_purchase.do")
	public String purchase(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_purchase";
	}
	
	@RequestMapping("/v_manu.do")
	public String manu(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_manu";
	}
	
	@RequestMapping("/v_lib.do")
	public String lib(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_lib";
	}
	
	@RequestMapping("/v_queryStat.do")
	public String queryStat(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_queryStat";
	}
	
	@RequestMapping("/v_data_setting.do")
	public String dataSetting(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_data_setting";
	}
	
	@RequestMapping("/v_system_setting.do")
	public String systemSetting(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_system_setting";
	}
	
	@RequestMapping("/grf/1a.do")
	public String test(HttpServletRequest request, ModelMap model) {
		return "pages/left_bar_system_setting";
	}
	
	
	@Autowired
	private CategoryDao categoryDao;
}
