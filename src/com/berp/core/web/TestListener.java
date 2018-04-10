package com.berp.core.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TestListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try{
			ServletContext app = arg0.getServletContext();
			String test = app.getInitParameter("url");
			test.toCharArray();
		}catch(Exception e){
			
		}
		
	}

}
