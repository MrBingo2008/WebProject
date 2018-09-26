package com.berp.framework.web;

import java.util.List;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.UrlPathHelper;

import com.berp.core.dao.UserDao;

import static com.berp.framework.web.Constants.MESSAGE;
import static com.berp.core.dao.UserDao.USER_ID;
import static com.berp.framework.action.LoginAct.PROCESS_URL;
import static com.berp.framework.action.LoginAct.RETURN_URL;
import com.berp.core.entity.User;
import com.berp.framework.springmvc.MessageResolver;
import com.berp.framework.util.DateUtils;
import com.berp.framework.web.session.SessionProvider;

/**
 * CMS上下文信息拦截器，相当于Servlet的filter
 * 
 * 包括登录信息、权限信息、站点信息
 * 
 * 
	preHandle在请求处理前运行，必须要返回true才能继续
	postHandle在controller处理完，渲染之前运行，主要可以修改modelAndView
	completion在渲染完调用
	
	Interceptor跟filter有什么区别?
 * 
 */
public class AdminContextInterceptor extends HandlerInterceptorAdapter {
	//private static final Logger log = Logger
	//		.getLogger(AdminContextInterceptor.class);
	public static final String PERMISSION_MODEL = "_permission_key";

	//stone: preHandle是处理controller前执行，postHandle是controller处理后渲染前、且preHandle返回true执行
	//afterCompletion是渲染后、且preHandle返回true执行
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		// 获得用户
		User user = null;
		Integer userId = (Integer) sessionProvider.getAttribute(request, USER_ID);
		if (userId != null) {
			user = userDao.findById(userId);
		}

		//RequestInfoUtils感觉跟sessionProvider的功能差不多，考虑合并
		RequestInfoUtils.setUser(request, user);
		ThreadVariable.setUser(user);
		
		String uri = getURI(request);
		// 不在验证的范围内
		if (exclude(uri)) {
			return true;
		}
		//用户为null跳转到登陆页面
		if (user == null) {
			//如果是普通url请求，则返回页面
			String header = request.getHeader("x-requested-with");
			if(header == null || header == "")
				response.sendRedirect(getLoginUrl(request));
			//如果是ajax请求，直接返回301，让dwz去处理
			else
				ResponseUtils.renderJson(response, DwzJsonUtils.getTimeoutJson().toString());
			return false;
		}
		
		//modified by stone
		boolean viewonly = false; //user.getViewonlyAdmin();
		// 没有访问权限，提示无权限。
		if (auth && !user.isSuper() &&
				 !permistionPass(uri, user.getPerms(), viewonly)) {
			request.setAttribute(MESSAGE, MessageResolver.getMessage(request,
					"login.notPermission"));
			//response.sendError(HttpServletResponse.SC_FORBIDDEN);
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson(uri+"无权限访问").toString());
			return false;
		}
		
		//added by stone
		if(uri.startsWith("/o_") && DateUtils.getYear()!=2018){
			request.setAttribute(MESSAGE, "授权过期");
			ResponseUtils.renderJson(response, DwzJsonUtils.getFailedJson("操作失败，系统授权过期!").toString());
			return false;
		}
		
		return true;
	}

	//渲染前调用，主要给modelAndView的_permission_key赋值，freemarker的<@perm>根据此值来判断是否显示
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler, ModelAndView mav)
			throws Exception {
		
		User user = RequestInfoUtils.getUser(request);
		// 不控制权限时perm为null，PermistionDirective标签将以此作为依据不处理权限问题。
		if (auth && user != null && !user.isSuper() && mav != null
				&& mav.getModelMap() != null && mav.getViewName() != null
				&& !mav.getViewName().startsWith("redirect:")) {
			mav.getModelMap().addAttribute(PERMISSION_MODEL, user.getPerms());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// Sevlet容器有可能使用线程池，所以必须手动清空线程变量。
		ThreadVariable.removeUser();
	}

	private String getLoginUrl(HttpServletRequest request) {
		StringBuilder buff = new StringBuilder();
		if (loginUrl.startsWith("/")) {
			String ctx = request.getContextPath();
			if (!StringUtils.isBlank(ctx)) {
				buff.append(ctx);
			}
		}
		buff.append(loginUrl).append("?");
		buff.append(RETURN_URL).append("=").append(returnUrl);
		if (!StringUtils.isBlank(processUrl)) {
			buff.append("&").append(PROCESS_URL).append("=").append(
					getProcessUrl(request));
		}
		return buff.toString();
		//return null;
	}

	private String getProcessUrl(HttpServletRequest request) {
		StringBuilder buff = new StringBuilder();
		if (loginUrl.startsWith("/")) {
			String ctx = request.getContextPath();
			if (!StringUtils.isBlank(ctx)) {
				buff.append(ctx);
			}
		}
		buff.append(processUrl);
		return buff.toString();
	}

	private boolean exclude(String uri) {
		if (excludeUrls != null) {
			for (String exc : excludeUrls) {
				if (exc.equals(uri)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean permistionPass(String uri, Set<String> perms,
			boolean viewOnly) {
		String u = null;
		int i;
		for (String perm : perms) {
			if (uri.startsWith(perm)) {
				// 只读管理员
				if (viewOnly) {
					// 获得最后一个 '/' 的URI地址。
					i = uri.lastIndexOf("/");
					if (i == -1) {
						throw new RuntimeException("uri must start width '/':"
								+ uri);
					}
					u = uri.substring(i + 1);
					// 操作型地址被禁止
					if (u.startsWith("o_")) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 获得第三个路径分隔符的位置
	 * 
	 * @param request
	 * @throws IllegalStateException
	 *             访问路径错误，没有三(四)个'/'
	 */
	private static String getURI(HttpServletRequest request)
			throws IllegalStateException {
		UrlPathHelper helper = new UrlPathHelper();
		String uri = helper.getOriginatingRequestUri(request);
		String ctxPath = helper.getOriginatingContextPath(request);
		//modified from count=1 to 0
		int start = 0, i = 0, count = 0;
		if (!StringUtils.isBlank(ctxPath)) {
			count++;
		}
		while (i < count && start != -1) {
			start = uri.indexOf('/', start + 1);
			i++;
		}
		if (start <= 0) {
			throw new IllegalStateException(
					"admin access path not like '/jeeadmin/jspgou/...' pattern: "
							+ uri);
		}
		return uri.substring(start);
	}

	@Autowired
	private SessionProvider sessionProvider;
	//modified by stone: auth is no used
	//private AuthenticationMng authMng;
	//private CmsSiteMng cmsSiteMng;
	
	@Autowired
	private UserDao userDao;
	
	//可以在xml里赋值，方便调试，这种直接赋值的不能用@Autowired，@Autowired必须有xml bean
	private boolean auth = true;
	
	private String[] excludeUrls;

	private String loginUrl;
	private String processUrl;
	private String returnUrl;

	
	public void setAuth(boolean auth) {
		this.auth = auth;
	}
	
	public void setExcludeUrls(String[] excludeUrls) {
		this.excludeUrls = excludeUrls;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public void setProcessUrl(String processUrl) {
		this.processUrl = processUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}
}