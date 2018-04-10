package com.berp.core.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.entity.Material;
import com.berp.core.dao.CategoryDao;
import com.berp.core.entity.Category;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class CategoryAct {
	
	//rootParentId为总的类型，用于新增和修改页面
	@RequestMapping("/v_category_list.do")
	public String categoryList(Integer rootParentId, Integer parentId, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		if(parentId == null)
			parentId = rootParentId;
		
		Pagination pagination = categoryDao.getPage(parentId, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		
		Category parent = categoryDao.findById(parentId);
		model.addAttribute("parent", parent);
		
		model.addAttribute("rootParentId", rootParentId);
		return "pages/data_setting/category_list";
	}
	
	//只有一个根节点
	@RequestMapping("/v_category_tree.do")
	public String categoryTree(Integer parentId, HttpServletRequest request, ModelMap model) {
		Category category = categoryDao.findById(parentId);
		JSONObject object = categoryDao.getCategoryTree(category);
		String test = object.toString();
		model.addAttribute("tree", test);
		return "pages/data_setting/category_tree";
	}
	
	@RequestMapping("/v_category_add.do")
	public String categoryAdd(Integer rootParentId, HttpServletRequest request, ModelMap model) {
		model.addAttribute("rootParentId", rootParentId);
		return "pages/data_setting/category_detail";
	}
	
	@RequestMapping("/o_category_save.do")
	public void categorySave(Category category, Integer rootParentId, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		//stone: how to deal with exception, need to re-organize
		categoryDao.save(category);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_category_list.do?rootParentId="+rootParentId, returnTitle).toString());
	}
	
	@RequestMapping("/v_category_edit.do")
	public String categoryEdit(Integer rootParentId, Integer categoryId, HttpServletRequest request, ModelMap model) {
		model.addAttribute("rootParentId", rootParentId);
		
		Category category = categoryDao.findById(categoryId);
		model.addAttribute("category", category);
		model.addAttribute("openMode", "edit");
		model.addAttribute("rootParentId", rootParentId);
		return "pages/data_setting/category_detail";
	}
	
	@RequestMapping("/o_category_update.do")
	public void categoryUpdate(Category category, Integer rootParentId, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		//stone: how to deal with exception, need to re-organize
		categoryDao.update(category);

		//stone：这个里面也有exception处理
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功!", "v_category_list.do?rootParentId="+rootParentId, returnTitle).toString());		
	}
	
	@RequestMapping("/o_category_delete.do")
	public void categoryDelete(Integer categoryId, Integer rootParentId, String returnTitle, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			categoryDao.deleteById(categoryId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_category_list.do?rootParentId="+rootParentId, returnTitle).toString());
	}
	
	@Autowired
	private CategoryDao categoryDao;
}
