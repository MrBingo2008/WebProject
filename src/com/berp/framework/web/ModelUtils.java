package com.berp.framework.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.berp.core.entity.User;

public class ModelUtils {
	/**
	 * 为前台模板设置公用数据
	 * 
	 * @param request
	 * @param model
	 */
	//public static void frontData(HttpServletRequest request,
	//		Map<String, Object> map, CmsSite site) {
		//CmsUser user = CmsUtils.getUser(request);
		//String location = RequestUtils.getLocation(request);
		//Long startTime = (Long) request.getAttribute(START_TIME);
		//frontData(map, site, user, location, startTime);
	//}

	public static void frontData(Map<String, Object> map, User user/*, String location, Long startTime*/) {
		/*if (startTime != null) {
			map.put(START_TIME, startTime);
		}*/
		if (user != null) {
			//stone: 原来是USER，改为'user'
			map.put("user", user);
		}
		
		//String ctx = site.getContextPath() == null ? "" : site.getContextPath();
		//map.put(BASE, ctx);
		//map.put(RES_SYS, ctx + RES_PATH);
		//String res = ctx + RES_PATH + "/" + site.getPath() + "/"
		//		+ site.getTplSolution();
		// res路径需要去除第一个字符'/'
		//map.put(RES_TPL, res.substring(1));
		//map.put(LOCATION, location);
		
		//stone: 变量中间不能有-
		map.put("richRes", "/berp/rich-res");
	}
}
