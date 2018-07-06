package com.berp.framework.load;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.ServletContextAware;


//added by stone
public class FileDownload implements ServletContextAware{

	public void download(String path, String fileName, HttpServletRequest request,  HttpServletResponse response, ModelMap model){
		
		String filePath = ctx.getRealPath(path);
		filePath = filePath.replaceFirst("\\\\berp", "");
    	// 下载本地文件
        InputStream inStream = null;
		try {
			inStream = new FileInputStream(filePath);
	        response.reset();
	        response.setContentType("bin");
	        
	        String name = java.net.URLEncoder.encode(fileName,"UTF-8");
	        if(name.endsWith(".xls") || name.endsWith(".xlsx"))
	        	name = "attachment"+name.substring(name.lastIndexOf("."), name.length());
	        
	        response.addHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
	        // 循环取出流中的数据
	        byte[] b = new byte[100];
	        int len;
	        while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
        	inStream.close();
		} catch (FileNotFoundException e1) { 
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (IOException e) {
            //e.printStackTrace();
        }
	}

	public void downloadByFullpath(String path, String fileName, HttpServletRequest request,  HttpServletResponse response, ModelMap model){
		
    	// 下载本地文件
        InputStream inStream = null;
		try {
			inStream = new FileInputStream(path);
	        response.reset();
	        response.setContentType("bin");

	        String name = java.net.URLEncoder.encode(fileName,"UTF-8");
	        if(name.endsWith(".xls") || name.endsWith(".xlsx"))
	        	name = "attachment"+name.substring(name.lastIndexOf("."), name.length());

	        response.addHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");
	        // 循环取出流中的数据
	        byte[] b = new byte[100];
	        int len;
	        while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
        	inStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (IOException e) {
            //e.printStackTrace();
        }
	}
	
	private ServletContext ctx;

	public void setServletContext(ServletContext servletContext) {
		ctx = servletContext;
	}
}
