package com.berp.core.action;

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

import com.berp.mrp.dao.OrderDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.Material;
import com.berp.core.dao.CategoryDao;
import com.berp.core.dao.CompanyDao;
import com.berp.core.entity.Category;
import com.berp.core.entity.Company;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class CompanyAct {
	
	@RequestMapping("/v_company.do")
	//parentId用于默认打开某个类型的单位
	public String company(Integer type, Integer parentId, HttpServletRequest request, ModelMap model) {
		Category category = categoryDao.findById(2);
		JSONObject object = categoryDao.getCategoryTree(category);
		model.addAttribute("tree", object.toString());
		model.addAttribute("type", type);
		model.addAttribute("parentId", parentId);
		
		return "pages/data_setting/company";
	}
	
	@RequestMapping("/v_company_list.do")
	public String companyList(Integer type, String searchName, Integer parentId, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;

		if(type==null)
			type = 0;
		model.addAttribute("type", type);
		Pagination pagination = companyDao.getPage(parentId, searchName, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		
		model.addAttribute("parentId", parentId);
		
		return "pages/data_setting/company_list";
	}
	
	@RequestMapping("/v_company_add.do")
	public String companyAdd(Integer type, HttpServletRequest request, ModelMap model) {
		model.addAttribute("openMode", "add");
		model.addAttribute("company", new Company());
		model.addAttribute("type", type);
		return "pages/data_setting/company_detail";
	}

	@RequestMapping("/o_company_save.do")
	public void companySave(Company company, Integer type, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		companyDao.save(company);
		if(type!=null && type >0)
			ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJsonAndCloseCurrent("保存往来单位成功!", "company_select_dialog").toString());
		else
			ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存往来单位成功!", "v_company.do?type=0", "往来单位").toString());
	}
	
	@RequestMapping("/v_company_edit.do")
	public String companyEdit(Integer companyId, HttpServletRequest request, ModelMap model) {
		Company company = companyDao.findById(companyId);
		model.addAttribute("company", company);
		model.addAttribute("openMode", "edit");
		return "pages/data_setting/company_detail";
	}
	
	@RequestMapping("/o_company_update.do")
	public void companyUpdate(Company company, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		companyDao.update(company);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存往来单位成功!", "v_company.do?type=0", "往来单位").toString());
	}
	
	@RequestMapping("/o_company_delete.do")
	public void companyDelete(Integer companyId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			companyDao.deleteById(companyId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_company.do?type=0", "往来单位").toString());
	}
	
	@Autowired
	private CompanyDao companyDao;
	
	@Autowired
	private CategoryDao categoryDao;
}
