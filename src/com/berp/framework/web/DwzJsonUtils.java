package com.berp.framework.web;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DwzJsonUtils {
	public static final Logger log = LoggerFactory
			.getLogger(DwzJsonUtils.class);

	/**
	 * 发送文本。使用UTF-8编码。
	 * 
	 * @param response
	 *            HttpServletResponse
	 * @param text
	 *            发送的字符串
	 */
	public static JSONObject getSuccessJson(String text) {
		JSONObject object = new JSONObject();
		try {
			object.put("statusCode", "200");
			object.put("message", text);
			object.put("navTabId", "");
			object.put("rel","main");
			object.put("callbackType", "");
			object.put("forwardUrl", "");
			object.put("confirmMsg", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	//只有user change pwd的时候会调用到
	public static JSONObject getSuccessJsonAndCloseCurrent(String text) {
		JSONObject object = new JSONObject();
		try {
			object.put("statusCode", "200");
			object.put("message", text);
			
			//navTabId和rel是同个if-else判断的，二取一, rel好像是本页面的某个区域
			object.put("navTabId", "");
			object.put("rel","");
			
			//callbackType=closeCurrent, forward(forwardUrl), forwardConfirm
			object.put("callbackType", "closeCurrent");
			object.put("forwardUrl", "");
			object.put("confirmMsg", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	//
	public static JSONObject getSuccessJsonAndCloseCurrent(String text, String dialogId) {
		JSONObject object = new JSONObject();
		try {
			object.put("statusCode", "200");
			object.put("message", text);
			object.put("dialogId", dialogId);
			object.put("rel","");
			object.put("callbackType", "closeCurrent");
			object.put("forwardUrl", "");
			object.put("confirmMsg", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	public static JSONObject getSuccessAndRedirectJson(String text, String url, String title) {
		JSONObject object = new JSONObject();
		try {
			object.put("statusCode", "200");
			object.put("message", text);
			
			object.put("navTabId", "");
			object.put("rel","main");
			
			object.put("callbackType", "forward");
			object.put("forwardUrl", url);
			object.put("confirmMsg", "");
			object.put("title", title);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	public static JSONObject getFailedJson(String text) {
		JSONObject object = new JSONObject();
		try {
			if(text == null || text == "")
				text="输入有误";
			
			object.put("statusCode", "300");
			object.put("message", text);
			object.put("navTabId", "");
			object.put("rel","");
			object.put("callbackType", "");
			object.put("forwardUrl", "");
			object.put("confirmMsg", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	public static JSONObject getTimeoutJson() {
		JSONObject object = new JSONObject();
		try {
			object.put("statusCode", "301");
			object.put("message", "操作超时，请重新登录!");
			object.put("navTabId", "");
			object.put("callbackType", "");
			object.put("forwardUrl", "");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
}
