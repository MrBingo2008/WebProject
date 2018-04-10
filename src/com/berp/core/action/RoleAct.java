package com.berp.core.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.core.dao.RoleDao;
import com.berp.core.entity.Role;
import com.berp.framework.page.Pagination;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;

@Controller
public class RoleAct {

	@RequestMapping("/v_role_add.do")
	public String roleAdd(HttpServletRequest request,ModelMap model) {
		Role role = new Role();
		role.setPerms(new HashSet<String>());
		model.addAttribute("role", role);
		return "pages/system_setting/role_detail";
	}
	
	@RequestMapping("/o_role_save.do")
	public void roleSave(Role bean, String[] perms, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		bean = roleDao.save(bean, splitPerms(perms));	
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存角色成功", "v_role_list.do", "查询角色").toString());
	}

	@RequestMapping("/v_role_edit.do")
	public String roleEdit(Integer roleId, HttpServletRequest request,ModelMap model) {
		Role role = roleDao.findById(roleId);
		model.addAttribute("role", role);
		model.addAttribute("openMode", "edit");
		return "pages/system_setting/role_detail";
	}

	@RequestMapping("/o_role_update.do")
	public void roleUpdate(Role bean, String[] perms, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		bean = roleDao.update(bean, splitPerms(perms));	
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存角色成功", "v_role_list.do", "查询角色").toString());
	}
	
	@RequestMapping("/v_role_list.do")
	public String roleList(Integer pageNum, Integer numPerPage, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		Pagination pagination = roleDao.getPage(pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		return "pages/system_setting/role_list";
	}
	
	@RequestMapping("/o_role_delete.do")
	public void roleDelete(Integer id, HttpServletRequest request, HttpServletResponse response, ModelMap model) {	
		try{
			roleDao.deleteById(id);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJson("删除成功!").toString());
	}
	
	private Set<String> splitPerms(String[] perms) {
		Set<String> set = new HashSet<String>();
		if (perms != null) {
			for (String perm : perms) {
				for (String p : StringUtils.split(perm, ',')) {
					if (!StringUtils.isBlank(p)) {
						set.add(p);
					}
				}
			}
		}
		return set;
	}
	
	@Autowired
	private RoleDao roleDao;
}
