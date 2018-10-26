package com.berp.mrp.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//stone: 这几个类是否属于springmvc?
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.mrp.dao.BatchFlowDao;
import com.berp.mrp.dao.MaterialDao;
import com.berp.mrp.dao.OrderDao;
import com.berp.mrp.dao.OrderRecordDao;
import com.berp.mrp.dao.RawBatchFlowDao;
import com.berp.mrp.entity.BatchFlow;
import com.berp.mrp.entity.Material;
import com.berp.mrp.entity.MaterialAttach;
import com.berp.mrp.entity.Order;
import com.berp.mrp.entity.OrderRecord;
import com.berp.mrp.entity.ProductMaterial;
import com.berp.core.dao.CategoryDao;
import com.berp.core.entity.Category;
import com.berp.framework.page.Pagination;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.framework.web.session.SessionProvider;


@Controller
public class MaterialAct {
	public static final String MATERIAL_PARENT_ID = "material_parent_id";
	public static final String MATERIAL_SEARCH_NAME = "material_search_name";
	public static final String MATERIAL_PAGE_NUM = "material_page_num";
	public static final String MATERIAL_NUM_PER_PAGE = "material_num_per_page";
	
	@RequestMapping("/v_material.do")
	//不用parentId，但是v_material.do也要用到
	//有两种情况会调用到v_material.do，一个是普通查询（包括navTab和dialog），一个是修改查看返回
	//如果是普通查询的话，parentId在list那里登记到session，session主要是用于修改查看返回，然后从session里取出来
	//type=0: navTab list
	//type=1: dialog single lookup
	//type=2: dialog batch list single select
	//type=3: dialog batch list multi select
	//type=4: navTab batch detail
	public String material(Integer type, Integer useSession, HttpServletRequest request, ModelMap model) {
		Category category = categoryDao.findById(1);
		JSONObject object = categoryDao.getCategoryTree(category);
		
		Integer parentId = null;
		//如果是修改或返回，就用session的值，否则是初始化
		if(useSession != null && useSession == 1)
			parentId = (Integer)sessionProvider.getAttribute(request, MATERIAL_PARENT_ID);
		if(parentId == null)
			parentId = category.getId();
		model.addAttribute("parentId", parentId);
		
		//useSession设成integer，主要是javascript里会用到，boolean获取有问题
		if(useSession == null)
			useSession = 0;
		model.addAttribute("useSession", useSession);
		
		model.addAttribute("tree", object.toString());
		model.addAttribute("type", type);
		
		return "pages/data_setting/material";
	}
	
	//parentId参数为什么要单列出来，在session里也是会记忆这个parentId的，不过有树点击的操作，所以放在这里方便
	@RequestMapping("/v_material_list.do")
	public String materialList(Integer type, Integer parentId, Integer useSession, String searchName, Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		if(useSession!=null && useSession == 1){
			searchName = (String)sessionProvider.getAttribute(request, MATERIAL_SEARCH_NAME);
			pageNum = (Integer)sessionProvider.getAttribute(request, MATERIAL_PAGE_NUM);
			numPerPage = (Integer)sessionProvider.getAttribute(request, MATERIAL_NUM_PER_PAGE);
		}
		
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Pagination pagination = materialDao.getPage(parentId, searchName, pageNum, numPerPage);
		model.addAttribute("type", type);
		
		model.addAttribute("parentId", parentId);
		model.addAttribute("searchName", searchName);
		model.addAttribute("pagination", pagination);
		
		//放session
		sessionProvider.setAttribute(request, response, MATERIAL_PARENT_ID, parentId);
		sessionProvider.setAttribute(request, response, MATERIAL_SEARCH_NAME, searchName);
		sessionProvider.setAttribute(request, response, MATERIAL_PAGE_NUM, pageNum);
		sessionProvider.setAttribute(request, response, MATERIAL_NUM_PER_PAGE, numPerPage);
		
		return "pages/data_setting/material_list";
	}
	
	@RequestMapping("/v_material_add.do")
	public String materialAdd(Integer type, HttpServletRequest request, ModelMap model) {
		Material m = new Material();
		model.addAttribute("material", m);
		model.addAttribute("type", type);
		return "pages/data_setting/material_detail";
	}
	
	@RequestMapping("/o_material_save.do")
	public void materialSave(Material material, Integer type, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		/*if(DateUtils.getYear()!=2018){
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("保存失败，系统授权过期!").toString());
			return;
		}*/
		
		//stone: how to deal with exception, need to re-organize
		material.setStatus(0);
		if(material.getCompany().getId() == null)
			material.setCompany(null);
		
		if(material.getSurface().getId() == null)
			material.setSurface(null);
		
		if(material.getAssemblies() != null && material.getAssemblies().size() > 0)
			for(ProductMaterial assembly : material.getAssemblies())
				assembly.setProduct(material);
		
		List<MaterialAttach> attachs = material.getAttachs();
		if(attachs != null && attachs.size() >0 ){
			int i=0;
			for(; i<attachs.size()&& !StringUtils.isBlank(attachs.get(i).getLocation());i++){
				attachs.get(i).setMaterial(material);
			}
			if(i < attachs.size()){
				for(int j=attachs.size() -1 ;j>= i;j--)
					attachs.remove(j);
			}
		}
		
		if(material.getProcess().getId() == null)
			material.setProcess(null);
		
		materialDao.save(material);

		//type=1应该是用于dialog的add
		if(type!=null && type >0)
			ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJsonAndCloseCurrent("保存物料成功!", "material_select_dialog").toString());
		else
			ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存物料成功!", "v_material.do?type=0&useSession=1", "物料").toString());
	}

	@RequestMapping("/v_material_view.do")
	public String materialView(Integer materialId, Integer parentId, HttpServletRequest request, ModelMap model) {
		Material m = materialDao.findById(materialId);
		model.addAttribute("material", m);
		model.addAttribute("openMode", "view");
		
		model.addAttribute("parentId", parentId);
		
		return "pages/data_setting/material_detail";
	}
	
	@RequestMapping("/v_material_edit.do")
	public String materialEdit(Integer materialId, Integer parentId, HttpServletRequest request, ModelMap model) {
		Material m = materialDao.findById(materialId);
		model.addAttribute("material", m);
		model.addAttribute("openMode", "edit");
		
		model.addAttribute("parentId", parentId);
		
		return "pages/data_setting/material_detail";
	}
	
	@RequestMapping("/o_material_update.do")
	public void materialUpdate(Material material,  Integer parentId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        
		//stone: how to deal with exception, need to re-organize
		material.setStatus(0);
		if(material.getCompany().getId() == null)
			material.setCompany(null);
		
		if(material.getSurface().getId() == null)
			material.setSurface(null); 
		
		if(material.getAssemblies() != null && material.getAssemblies().size() > 0)
			for(ProductMaterial assembly : material.getAssemblies())
				assembly.setProduct(material);
		else
			material.setAssemblies(new ArrayList<ProductMaterial>());
		
		List<MaterialAttach> attachs = material.getAttachs();
		if(attachs != null && attachs.size() >0 ){
			//这里不需要setMaterial也可以，很奇怪!
			//这里的步骤是1 update property, update material_id null, update material_id value
			int i=0;
			for(; i<attachs.size()&& !StringUtils.isBlank(attachs.get(i).getLocation());i++){
				attachs.get(i).setMaterial(material);
			}
			if(i < attachs.size()){
				for(int j=attachs.size() -1 ;j>= i;j--)
					attachs.remove(j);
			}
			
		}else
			material.setAttachs(new ArrayList<MaterialAttach>());
		
		if(material.getProcess().getId() == null)
			material.setProcess(null);
		
		materialDao.update(material);
		
		String url = "v_material.do?type=0&useSession=1";
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存物料成功!", url, "物料").toString());
	}
	
	@RequestMapping("/o_material_delete.do")
	public void orderDelete(Integer materialId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		try{
			materialDao.deleteById(materialId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("删除成功!", "v_material.do?type=0&useSession=1", "物料").toString());
	}
	
	@RequestMapping("/v_record_list.do")
	//direction表示方向，1为进，2为出 0为生产选择
	public String recordList(Integer direction, String searchName, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		Pagination pagination = recordDao.getPage(direction==1?1:2,searchName, 1, 2, null, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("direction", direction);
		model.addAttribute("searchName", searchName);
		//这个是为了选择窗口的预定数量label
		model.addAttribute("orderType", direction==1?"purchase":"sell");
		return "pages/data_setting/record_list";
	}
	
	@RequestMapping("/v_record_multi_list.do")
	//type=0, 用于+生产
	//type=1用于plan_detail和order_detail, multiLookup
	//type=2用于purchaseIn, multiAddLookup
	//type=3用于sellOut, multiAddLookup
	public String recordMultiList(Integer type, Integer orderId, String searchName, HttpServletRequest request, ModelMap model) {
		List<OrderRecord> records = null;
		Pagination pagination = null;
		if(type == 0){
			Order order = orderDao.findById(orderId);
			model.addAttribute("order", order);
			
			pagination = new Pagination();
			records = order.getRecords();
			
			//stone：为了兼容type = 1的html页面
			pagination.setList(records);
			pagination.setTotalCount(records.size());
			
			model.addAttribute("orderType", "sell");
		}
		else if(type ==1 || type == 3){
			pagination = recordDao.getPage(2, searchName, 1, 2, null, null, 20);
			model.addAttribute("orderType", "sell");
		}
		else if(type == 2){
			pagination = recordDao.getPage(1, searchName, 1, 2, null, null, 20);
			
			model.addAttribute("orderType", "purchase");
		}
		
		model.addAttribute("pagination", pagination);
		model.addAttribute("type", type);
		model.addAttribute("searchName", searchName);
		
		return "pages/data_setting/record_multi_list";
	}
	
	@RequestMapping("/v_record_multi_list_more.do")
	public String recordMultiListMore(Integer type, Integer orderId, String searchName, Integer maxId, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		Pagination pagination = null;
		if(type == 1 || type == 3){
			pagination = recordDao.getPage(2, searchName, 1, 2, maxId, null, numPerPage);
		}
		else if(type == 2){
			pagination = recordDao.getPage(1, searchName, 1, 2, maxId, null, numPerPage);
		}
		model.addAttribute("pagination", pagination);
		model.addAttribute("type", type);
		model.addAttribute("searchName", searchName);
		return "pages/data_setting/record_multi_list_more";
	}
	
	@RequestMapping("/v_record_material_multi_list.do")
	//目前这个全都是sell
	//type:0，表示加入购物车模式，1，选择模式
	public String recordMaterialMultiList(Integer orderId, HttpServletRequest request, ModelMap model) {
		Order order = orderDao.findById(orderId);
		model.addAttribute("order", order);
		return "pages/data_setting/record_material_multi_list";
	}
	
	//recordId不合理, 在sellOut的v_record_multi_list.do showBatch里要用到
	//type=0：全部，type=1库存大于0, type=2表示multi
	@RequestMapping("/v_batch_list.do")
	public String batchAvailableList(Integer type, Integer materialId, Integer recordId, HttpServletRequest request, ModelMap model) {
		List<BatchFlow> flows = null;
		if(type == null)
			type = 0;
		
		if(type == 0){
			flows = flowDao.getList(materialId, null, 1, null, null, null);
			model.addAttribute("type", "all");
		}
		else{
			flows = flowDao.getList(materialId, 1, 1, 0.00, null, null);
			if(type == 1)
				model.addAttribute("type", "lib_single");
			else
				model.addAttribute("type", "lib_multi");
			model.addAttribute("recordId", recordId);
			if(recordId!=null){
				model.addAttribute("orderSerial", recordDao.findById(recordId).getOrd().getInfo());
			}
		}
		model.addAttribute("flows", flows);
		return "pages/data_setting/batch_list";
	}
	
	//type=1 out; type=2 in，0预留，只用type作为参数就可以，cirType根据type再定
	/*@RequestMapping("/v_raw_batch_list.do")
	public String rawBatchList(Integer type, String searchName, Integer parentId, Integer pageNum, Integer numPerPage, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		//type=1 out：选择leftNumber大于0，in：只需要status = 1 2就可以
		Pagination pagination = rawFlowDao.getPage(type-1, 1,2, searchName, type==1?0.00:null, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		model.addAttribute("searchName", searchName);
		model.addAttribute("type", type);
		model.addAttribute("cirType", type==2?"outsideIn":"outsideOut");
		return "pages/data_setting/raw_batch_list";
	}*/
	
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
	private MaterialDao materialDao;
	
	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderRecordDao recordDao;
	
	@Autowired
	private SessionProvider sessionProvider;
}
