package com.berp.framework.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.berp.framework.load.FileDownload;
import com.berp.framework.web.DwzJsonUtils;
import com.berp.framework.web.ResponseUtils;
import com.berp.mrp.dao.MaterialAttachDao;
import com.berp.mrp.entity.MaterialAttach;

@Controller
public class UploadAct {
	
	protected String savePathSuffix = "e:/berp/attach";
	
	//上传文件的保存路径  
    protected String configPath = "upload/widget";  
  
    protected String dirTemp = "upload/widget/temp";  
      
    protected String dirName = "file";  
    
    @RequestMapping(value="/attach_upload.do", method = RequestMethod.POST)
	public void upload(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws ServletException, IOException {
    	request.setCharacterEncoding("UTF-8");  
        response.setContentType("text/html; charset=UTF-8");  
        PrintWriter out = response.getWriter();  
          
        //文件保存目录路径  
        String savePath = savePathSuffix;  
        //String savePath = this.getServletContext().getRealPath("/") + configPath;  
          
        // 临时文件目录   
        String tempPath =  request.getServletContext().getRealPath("/") + dirTemp;  
          
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");  
        String ymd = sdf.format(new Date());  
        savePath += "/" + ymd + "/";  
        //创建文件夹  
        File dirFile = new File(savePath);  
        if (!dirFile.exists()) {  
            dirFile.mkdirs();  
        }  
          
        tempPath += "/" + ymd + "/";  
        //创建临时文件夹  
        File dirTempFile = new File(tempPath);  
        if (!dirTempFile.exists()) {  
            dirTempFile.mkdirs();  
        }  
          
        DiskFileItemFactory  factory = new DiskFileItemFactory();  
        factory.setSizeThreshold(20 * 1024 * 1024); //设定使用内存超过5M时，将产生临时文件并存储于临时目录中。     
        factory.setRepository(new File(tempPath)); //设定存储临时文件的目录。     
        ServletFileUpload upload = new ServletFileUpload(factory);  
        upload.setHeaderEncoding("UTF-8");  
        try {  
            List items = upload.parseRequest(request);  
            System.out.println("items = " + items);  
            Iterator itr = items.iterator();  
              
            while (itr.hasNext())   
            {  
                FileItem item = (FileItem) itr.next();  
                String fileName = item.getName();  
                long fileSize = item.getSize();  
                if (!item.isFormField()) {  
                    String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();  
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");  
                    String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;  
                    try{  
                        File uploadedFile = new File(savePath, newFileName);  
                        
                        OutputStream os = new FileOutputStream(uploadedFile);  
                        InputStream is = item.getInputStream();  
                        byte buf[] = new byte[1024];//可以修改 1024 以提高读取速度  
                        int length = 0;    
                        while( (length = is.read(buf)) > 0 ){    
                            os.write(buf, 0, length);    
                        }    
                        //关闭流    
                        os.flush();  
                        os.close();    
                        is.close();    
                        System.out.println("上传成功！路径："+savePath+"/"+newFileName);  
                        out.print(String.format("/%s/%s", ymd, newFileName));  
                    }catch(Exception e){  
                        e.printStackTrace();  
                    }  
                }else {  
                    String filedName = item.getFieldName();  
                    if(filedName.equals("customData")){
                    	System.out.println("用户自定义参数===============");
                    	System.out.println("FieldName："+filedName);  
                    	System.out.println(URLDecoder.decode(URLDecoder.decode(item.getString(), "utf-8"), "utf-8"));   
                    }else if(filedName.equals("tailor")){
	                    System.out.println("裁剪后的参数===============");   
	                    System.out.println("FieldName："+filedName);  
	                    System.out.println(new String(item.getString().getBytes("iso-8859-1"), "utf-8"));  
	                    // 获得裁剪部分的坐标和宽高
	                    System.out.println("String："+item.getString()); 
                    }
                }             
            }   
              
        } catch (FileUploadException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        out.flush();  
        out.close(); 
        //ResponseUtils.renderJson(response, DwzJsonUtils.getSuccessJson("test").toString());
	}
    
	@RequestMapping("/attach_view.do")
	public void viewAttach(String location, HttpServletRequest request,  HttpServletResponse response, ModelMap model){
		MaterialAttach attach = attachDao.findByLocation(location);
		if(attach != null && attach.getLocation()!=null)
			fileDownload.downloadByFullpath(this.savePathSuffix + attach.getLocation(), attach.getName(), request, response, model);
		else{
			String fileExt = location.substring(location.lastIndexOf(".") + 1).toLowerCase();  
			fileDownload.downloadByFullpath(this.savePathSuffix + location, "临时文件"+"."+fileExt, request, response, model);
		}
	}
	
	@Autowired
	private FileDownload fileDownload;
	
	@Autowired
	private MaterialAttachDao attachDao;
}

