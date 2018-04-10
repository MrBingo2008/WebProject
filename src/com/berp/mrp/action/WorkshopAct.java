package com.berp.mrp.action;

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

import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.entity.Material;
import com.berp.framework.web.ResponseUtils;


@Controller
public class WorkshopAct {
	@RequestMapping("/v_workshop.do")
	public String material(HttpServletRequest request, ModelMap model) {
		return "pages/data_setting/material";
	}
	
	@RequestMapping("/v_workshop_list.do")
	public String materialList(HttpServletRequest request, ModelMap model) {
		List ms = materialDao.getList();
		model.addAttribute("materials", ms);
		return "pages/data_setting/material_list";
	}
	
	@RequestMapping("/v_workshop_add.do")
	public String materialAdd(HttpServletRequest request, ModelMap model) {
		return "pages/data_setting/material_detail";
	}
	
	@RequestMapping("/o_workshop_save.do")
	public void materialSave(Material material, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		//stone: how to deal with exception, need to re-organize
		material.setStatus(0);
		materialDao.save(material);

		JSONObject object = new JSONObject();
		try {
			object.put("statusCode", "200");
			object.put("message", "保存物料成功!");
			object.put("navTabId", "");
			object.put("rel","");
			object.put("callbackType", "");
			object.put("forwardUrl", "");
			object.put("confirmMsg", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResponseUtils.renderJson(response, object.toString());
	}
	
	@Autowired
	private MaterialDao materialDao;
}
