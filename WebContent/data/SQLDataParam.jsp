﻿<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file = "GenXmlData.jsp" %>
<%
int DataLen = request.getContentLength();
byte[] DataBuf = new byte[DataLen];   

ServletInputStream sif = request.getInputStream();
sif.read(DataBuf, 0, DataLen);

String QuerySQL = new String(DataBuf);  

XML_GenOneRecordset(response, QuerySQL);
%> 