package com.berp.core.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.berp.core.dao.RoleDao;
import com.berp.core.dao.UserDao;
import com.berp.core.entity.Category;
import com.berp.core.entity.Role;
import com.berp.core.entity.User;
import com.berp.framework.page.Pagination;
import com.berp.framework.security.BadCredentialsException;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ModelUtils;
import com.berp.framework.web.RequestInfoUtils;
import com.berp.framework.web.ResponseUtils;

@Controller
public class UserAct {
	
	//这个路径不包括dispatch servlet那个前面那部分
	@RequestMapping("/v_user_list.do")
	public String userList(Integer pageNum, Integer numPerPage, String searchName, HttpServletRequest request, ModelMap model) {
		pageNum = pageNum == null?1:pageNum;
		numPerPage = numPerPage == null?20:numPerPage;
		
		Pagination pagination = userDao.getPage(searchName, pageNum, numPerPage);
		model.addAttribute("pagination", pagination);
		return "pages/system_setting/user_list";
	}
	
	@RequestMapping("/v_user_add.do")
	public String userAdd(HttpServletRequest request, ModelMap model) {
		model.addAttribute("openMode", "add");
		User user = new User();
		model.addAttribute("user", user);
		List<Role> roles = roleDao.getList();
		model.addAttribute("roles", roles);
		return "pages/system_setting/user_detail";
	}
	
	@RequestMapping("/o_user_save.do")
	public void userSave(User user, Integer [] roleIds, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		userDao.save(user, roleIds);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功", "v_user_list.do", "查询用户").toString());
	}
	
	@RequestMapping("/v_user_edit.do")
	public String userEdit(Integer userId, HttpServletRequest request, ModelMap model) {
		
		User user = userDao.findById(userId);
		List<Role> roles = roleDao.getList();
		model.addAttribute("user", user);
		//model.addAttribute("roleIds", user.getRoles());
		model.addAttribute("roles", roles);
		model.addAttribute("openMode", "edit");
		return "pages/system_setting/user_detail";
	}
	
	@RequestMapping("/o_user_update.do")
	public void userUpdate(User user, Integer [] roleIds, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		
		//stone: how to deal with exception, need to re-organize
		userDao.update(user, roleIds);
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessAndRedirectJson("保存成功", "v_user_list.do", "查询用户").toString());
	}
	
	@RequestMapping("/o_user_delete.do")
	public void userDelete(Integer userId, HttpServletRequest request, HttpServletResponse response, ModelMap model) {	
		try{
			userDao.deleteById(userId);
		} catch (Exception ex) {
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("删除错误，数据可能在其他地方引用.").toString());
			return;
		}
		ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJson("删除成功!").toString());
	}
	
	@RequestMapping("/v_personal_change_pwd.do")
	public String changePwdView(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		return "pages/personal/change_pwd";
	}
	
	@RequestMapping("/o_personal_change_pwd.do")
	public void changePwd(String oldPassword, String newPassword, HttpServletRequest request, HttpServletResponse response, ModelMap model) {
		User user = RequestInfoUtils.getUser(request);
		if (userDao.checkPassword(user, oldPassword) == true){
			userDao.resetPassword(user, newPassword);
			ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJsonAndCloseCurrent("保存成功").toString());
		}else
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("旧密码不正确").toString());
	}
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private RoleDao roleDao;
}
