package com.berp.mrp.web;

import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

public class PageListPara {
	public String searchName;
	public String searchRecordName;
	public Integer searchStatus;
	public Integer pageNum;
	public Integer numPerPage;
	
	public PageListPara(){ 
	}
	
	//必须要getter和setter才能作为参数
	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchRecordName() {
		return searchRecordName;
	}

	public void setSearchRecordName(String searchRecordName) {
		this.searchRecordName = searchRecordName;
	}

	public Integer getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(Integer searchStatus) {
		this.searchStatus = searchStatus;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(Integer numPerPage) {
		this.numPerPage = numPerPage;
	}

	public PageListPara(String searchName, String searchRecordName, Integer searchStatus, Integer pageNum, Integer numPerPage){
		this.searchName = searchName;
		this.searchRecordName = searchRecordName;
		this.searchStatus = searchStatus;
		this.pageNum = pageNum;
		this.numPerPage = numPerPage;
	}
	
	public void addToModel(ModelMap model){
		//先判读是为了防止出现转换为String时，出现null的情况
		if(StringUtils.isEmpty(searchName) == false)
			model.addAttribute("searchName", searchName);
		
		if(StringUtils.isEmpty(searchRecordName) == false)
			model.addAttribute("searchRecordName", searchRecordName);
		
		if(searchStatus != null)
			model.addAttribute("searchStatus", searchStatus);
		
		if(pageNum != null)
			model.addAttribute("pageNum", pageNum);
		
		if(numPerPage != null)
			model.addAttribute("numPerPage", numPerPage);
	}
	
	public String getUrlPara(){		
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isEmpty(searchName) == false)
			sb.append("&searchName="+searchName);
		if(StringUtils.isEmpty(searchRecordName) == false)
			sb.append("&searchRecordName="+searchRecordName);
		if(searchStatus != null)
			sb.append("&searchStatus="+searchStatus);
		if(pageNum != null)
			sb.append("&pageNum="+pageNum);
		if(numPerPage != null)
			sb.append("&numPerPage="+numPerPage);
		String result = sb.toString();
		if(result.startsWith("&"))
			result = result.substring(1);
		return result;
		
	}
}
