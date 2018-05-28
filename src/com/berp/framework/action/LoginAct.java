package com.berp.framework.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.berp.core.dao.UserDao.USER_ID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/*
import com.jeecms.common.security.BadCredentialsException;
import com.jeecms.common.security.UsernameNotFoundException;
import com.jeecms.common.web.RequestUtils;
import com.jeecms.common.web.session.SessionProvider;
import com.jeecms.core.manager.AuthenticationMng;
import com.jeecms.core.manager.ConfigMng;
import com.jeecms.core.web.WebErrors;
import com.octo.captcha.service.image.ImageCaptchaService;*/

import com.berp.core.dao.UserDao;
import com.berp.core.entity.User;
import com.berp.framework.security.BadCredentialsException;
import com.berp.framework.security.UsernameNotFoundException;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ModelUtils;
import com.berp.framework.web.RequestUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.framework.web.session.SessionProvider;

@Controller
public class LoginAct {
	private static final Logger log = LoggerFactory
			.getLogger(LoginAct.class);

	public static final String PROCESS_URL = "processUrl";
	public static final String RETURN_URL = "returnUrl";
	public static final String MESSAGE = "message";
	public static final String COOKIE_ERROR_REMAINING = "_error_remaining";

	@RequestMapping(value = "/login.do", method = RequestMethod.GET)
	public String input(HttpServletRequest request,
			HttpServletResponse response, ModelMap model) {
		
		//added by stone，这个函数就是往model里填入一些前台常用的变量(user、richRes)
		ModelUtils.frontData(model, null);
		
		String processUrl = RequestUtils.getQueryParam(request, PROCESS_URL);
		String returnUrl = RequestUtils.getQueryParam(request, RETURN_URL);
		String message = RequestUtils.getQueryParam(request, MESSAGE);
		Integer userId = (Integer) sessionProvider.getAttribute(request, USER_ID);
		
		//added by stone：写死returnUrl
		if (StringUtils.isBlank(returnUrl) || StringUtils.isEmpty(returnUrl))
			returnUrl = "/index.do";
		
		if (userId != null) {
			// 存在认证信息，且未过期
			if (userId != null) {
				String view = getView(processUrl, returnUrl, userId);
				if (view != null) {
					return view;
				}
			}
		}
		if (!StringUtils.isBlank(processUrl)) {
			model.addAttribute(PROCESS_URL, processUrl);
		}
		
		if (!StringUtils.isBlank(returnUrl)) {
			model.addAttribute(RETURN_URL, returnUrl);
		}
		
		if (!StringUtils.isBlank(message)) {
			model.addAttribute(MESSAGE, message);
		}
		return "login";
	}

	@RequestMapping(value = "/login.do", method = RequestMethod.POST)
	public String submit(String username, String password, String captcha,
			String processUrl, String returnUrl, String message,
			HttpServletRequest request, HttpServletResponse response,
			ModelMap model) {
		//WebErrors errors = new WebErrors();
		try {
			String ip = RequestUtils.getIpAddr(request);
			
			//commented by stone: this method may throw usernameNotFound and baddCredential exception
			User user = userDao.login(username, password, ip, USER_ID, request, response, sessionProvider);
			//是否需要在这里加上登录次数的更新？按正常的方式，应该在process里面处理的，不过这里处理也没大问题。
			//cmsUserMng.updateLoginInfo(user.getId(), ip);
			
			String view = getView(processUrl, returnUrl, user.getId());
			//cmsLogMng.loginSuccess(request, user, "login.log.loginSuccess");
			if (view != null) {
				//这里也是redirect:，相当于重新输入url请求给服务器，request的信息重置
				return view;
			} else {
				return "redirect:login.do";
			}
		} catch (UsernameNotFoundException e) {
			model.addAttribute("userNameNotFound", "true");
		} catch (BadCredentialsException e) {
			model.addAttribute("passwordError", "true");
		}
		
		//added by stone
		ModelUtils.frontData(model, null);
		return "login";
	}

	@RequestMapping(value = "/logout.do")
	public String logout(HttpServletRequest request,
			HttpServletResponse response) {
		Integer userId = (Integer) sessionProvider.getAttribute(request, USER_ID);
		if (userId != null) {
			sessionProvider.logout(request, response);
		}
		String processUrl = RequestUtils.getQueryParam(request, PROCESS_URL);
		String returnUrl = RequestUtils.getQueryParam(request, RETURN_URL);
		String view = getView(processUrl, returnUrl, userId);
		if (view != null) {
			return view;
		} else {
			return "redirect:login.do";
		}
	}
	
	/**
	 * 获得地址
	 * 
	 * @param processUrl
	 * @param returnUrl
	 * @param authId
	 * @param defaultUrl
	 * @return
	 */
	private String getView(String processUrl, String returnUrl, Integer authId) {
		if (!StringUtils.isBlank(processUrl)) {
			StringBuilder sb = new StringBuilder("redirect:");
			sb.append(processUrl).append("?").append(USER_ID).append("=")
					.append(authId);
			if (!StringUtils.isBlank(returnUrl)) {
				sb.append("&").append(RETURN_URL).append("=").append(returnUrl);
			}
			return sb.toString();
		} else if (!StringUtils.isBlank(returnUrl)) {
			StringBuilder sb = new StringBuilder("redirect:");
			sb.append(returnUrl);
			return sb.toString();
		} else {
			return null;
		}
	}

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private SessionProvider sessionProvider;
	
	/*
	@Autowired
	private AuthenticationMng authMng;*/
	/*@Autowired
	private ImageCaptchaService imageCaptchaService;
	@Autowired
	private CmsLogMng cmsLogMng;*/
}
