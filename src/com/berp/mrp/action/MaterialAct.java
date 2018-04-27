package com.berp.mrp.action;

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

import com.berp.mrp.dao.BatchDao;
import com.berp.mrp.dao.BatchFlowDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.dao.RawBatchDao;
import com.berp.mrp.dao.RawBatchFlowDao;
import com.berp.mrp.entity.Batch;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.RawBatch;
import com.berp.mrp.entity.RawBatchFlow;
import com.berp.core.dao.CategoryDao;
import com.berp.core.entity.Category;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;


@Controller
public class MaterialAct {
	@RequestMapping("/v_material.do")
	public String material(Integer type, HttpServletRequest request, ModelMap model) {
		Category category = categoryDao.findById(1);
		JSONObject object = categoryDao.getCategoryTree(category);
		model.addAttribute("tree", object.toString());
		model.addAttribute("type", type);
		
		return "pages/data_setting/material";
	}
	
	@RequestMapping("/v_material_list.do")
	public String materialList(Integer type, String searchName, Integer parentId, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Pagination pagination = materialDao.getPage(parentId, searchName, pageNum, numPerPage);
		
		model.addAttribute("pagination", pagination);
		model.addAttribute("type", type);
		model.addAttribute("searchName", searchName);
		model.addAttribute("parentId", parentId);
		
		return "pages/data_setting/material_list";
	}
	
	@RequestMapping("/v_material_add.do")
	public String materialAdd(HttpServletRequest request, ModelMap model) {
		Material m = new Material();
		model.addAttribute("material", m);
		return "pages/data_setting/material_detail";
	}
	
	@RequestMapping("/o_material_save.do")
	public void materialSave(Material material, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		//stone: how to deal with exception, need to re-organize
		material.setStatus(0);
		if(material.getCompany().getId() == null)
			material.setCompany(null);
		
		if(material.getSurface().getId() == null)
			material.setSurface(null);
		
		materialDao.save(material);

		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存物料成功!", "v_material.do?type=0", "物料").toString());
	}

	@RequestMapping("/v_material_edit.do")
	public String materialEdit(Integer materialId, HttpServletRequest request, ModelMap model) {
		Material m = materialDao.findById(materialId);
		model.addAttribute("material", m);
		model.addAttribute("openMode", "edit");
		return "pages/data_setting/material_detail";
	}
	
	@RequestMapping("/o_material_update.do")
	public void materialUpdate(Material material, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		//stone: how to deal with exception, need to re-organize
		material.setStatus(0);
		if(material.getCompany().getId() == null)
			material.setCompany(null);
		
		if(material.getSurface().getId() == null)
			material.setSurface(null);
		
		materialDao.update(material);

		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存物料成功!", "v_material.do?type=0", "物料").toString());
	}
	
	@RequestMapping("/o_material_delete.do")
	public void orderDelete(Integer materialId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			materialDao.deleteById(materialId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_material.do?type=0", "物料").toString());
	}
	
	@RequestMapping("/v_record_list.do")
	//direction表示方向，1为进，2为出 0为生产选择
	public String recordList(Integer direction, String searchName, HttpServletRequest request, ModelMap model) {
		List<OrderRecord> records = recordDao.findByCompanyAndMaterial(null, null, searchName, direction==1?1:2, 1, 2, null, null);
		model.addAttribute("records", records);
		model.addAttribute("direction", direction);
		model.addAttribute("searchName", searchName);
		//这个是为了选择窗口的预定数量label
		model.addAttribute("orderType", direction==1?"purchase":"sell");
		return "pages/data_setting/record_list";
	}
	
	//recordId不合理
	@RequestMapping("/v_batch_list.do")
	public String batchAvailableList(Integer materialId, Integer recordId, String orderSerial, HttpServletRequest request, ModelMap model) {
		List<BatchFlow> flows = flowDao.getList(materialId, 1, 1, 0.00, null, null);
		model.addAttribute("flows", flows);
		model.addAttribute("type", "lib");
		model.addAttribute("recordId", recordId);
		model.addAttribute("orderSerial", orderSerial);
		return "pages/data_setting/batch_list";
	}
	
	@RequestMapping("/v_batch_all_list.do")
	public String batchList(Integer materialId, HttpServletRequest request, ModelMap model) {
		List<BatchFlow> flows = flowDao.getList(materialId, null, 1, null, null, null);
		model.addAttribute("flows", flows);
		model.addAttribute("type", "all");
		return "pages/data_setting/batch_list";
	}
	
	//type=1 in; type=2 out，只用type作为参数就可以，cirType根据type再定
	@RequestMapping("/v_raw_batch_list.do")
	public String rawBatchList(Integer type, String searchName, Integer parentId, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Pagination pagination = rawFlowDao.getPage(0, 1,2, searchName, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("type", type);
		model.addAttribute("cirType", type==1?"outsideIn":"outsideOut");
		return "pages/data_setting/raw_batch_list";
	}
	
	@RequestMapping("/v_material_unit.do")
	public void materialUnit(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		JSONArray array = new JSONArray();
		try {
			JSONObject object = new JSONObject();
			object.put("unit", "PCS");
			array.put(object);
			JSONObject object1 = new JSONObject();
			object1.put("unit", "公斤");
			array.put(object1);
			JSONObject object2 = new JSONObject();
			object2.put("unit", "卷");
			array.put(object2);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = array.toString();
		ResponseUtils.renderJson(response, result);
	}
	
	@Autowired
	private CategoryDao categoryDao;

	@Autowired
	private BatchFlowDao flowDao;
	
	@Autowired
	private RawBatchFlowDao rawFlowDao;
	
	@Autowired
	private MaterialDao materialDao;
	
	@Autowired
	private OrderRecordDao recordDao;
}
