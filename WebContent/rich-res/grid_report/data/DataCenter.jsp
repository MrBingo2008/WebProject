<%@ page contentType="text/html; charset=utf-8"%>
<%@ include file = "GenXmlData.jsp" %>
<%
String data = request.getParameter("data"); 
String QuerySQL =  "";

if (data.equals("AppendBlankRow")) {
	QuerySQL = "select m.OrderId, m.OrderDate, d.Productid,p.ProductName,d.Quantity,"
		+ "d.UnitPrice*d.Quantity as Amount "
		+ "from orders m inner join (OrderDetails d inner join Products p on d.ProductID=p.ProductID) "
		+ "on m.orderid=d.orderid "
		+ "where (m.OrderDate between '1996-1-1' And '1997-9-30') and d.Productid<10 "
		+ "order by d.ProductID";
}
else if (data.equals("Categories")) {
	QuerySQL = "select * from categories ";
}
else if (data.equals("ContractOne")) {
	QuerySQL = "select m.OrderID,m.CustomerId,c.CompanyName,m.OrderDate, "
		+ "p.ProductName,d.UnitPrice,d.Quantity,d.UnitPrice*d.Quantity as Amount "
		+ "from (Orders m inner join "
		+ "(OrderDetails as d inner join Products p on P.ProductID=D.ProductID) on m.OrderId=d.OrderId) "
		+ "left join Customers c on c.CustomerID=m.CustomerID "
		+ "where m.OrderID=10252 and d.ProductID=20 "
		+ "order by m.OrderDate, m.OrderID";
}
else if (data.equals("CrossTab")) {
	QuerySQL =  "select c.City,m.CustomerId,c.CompanyName,d.ProductID,p.ProductName,"
		+ "d.Quantity, d.UnitPrice*d.Quantity as Amount "
		+ "from (Orders m inner join "
		+ "(OrderDetails as d inner join Products p "
		+ "on P.ProductID=D.ProductID) on m.OrderId=d.OrderId) "
		+ "left join Customers c on c.CustomerID=m.CustomerID "
		+ "where d.ProductID<8 "
		+ "order by c.City,m.CustomerId, d.ProductID";
}
else if (data.equals("CrossTabByDay")) {
	QuerySQL =  "select c.CompanyName,m.OrderDate,d.UnitPrice*d.Quantity as Amount "
	+ "from (Orders m inner join OrderDetails as d on m.OrderId=d.OrderId) "
	+ "left join Customers c on c.CustomerID=m.CustomerID "
	+ "where m.OrderDate between '1997-7-1' and '1997-7-10' "
	+ "order by c.CompanyName, m.OrderDate";
}
else if (data.equals("CrossTabByMonth")) {
	QuerySQL =  "select c.CompanyName,m.OrderDate,d.UnitPrice*d.Quantity as Amount "
	+ "from (Orders m inner join OrderDetails as d on m.OrderId=d.OrderId) "
	+ "left join Customers c on c.CustomerID=m.CustomerID "
	+ "where m.OrderDate between '1997-1-1' and '1997-12-31' "
	+ "order by c.CompanyName, m.OrderDate";
}
else if (data.equals("CrossTabCalendar")) {
	QuerySQL =  "select m.OrderDate,sum(d.Quantity) as Qty,sum(d.UnitPrice*d.Quantity) as Amount " +
		"from (Orders m inner join OrderDetails as d on m.OrderId=d.OrderId) " +
		"where m.OrderDate between '1997-1-1' and '1997-12-31' " +
		"group by m.OrderDate " +
		"order by m.OrderDate";
}
else if (data.equals("CrossTabSubtotal")) {
	QuerySQL =  "select t.CategoryName, p.ProductName,c.City,c.CompanyName,d.Quantity " +
		"from (Orders m inner join  " +
		"(OrderDetails as d inner join (Products p inner join Categories t on p.CategoryID=t.CategoryID) " +
		"on P.ProductID=D.ProductID) on m.OrderId=d.OrderId) " +
		"left join Customers c on c.CustomerID=m.CustomerID " +
		"where m.OrderDate between '1997-1-1' and '1997-3-31' " +
		"order by t.CategoryName,p.ProductName,c.City,c.CompanyName ";
}
else if (data.equals("CrossTabYearMonth")) {
	QuerySQL =  
	"select Year(m.OrderDate) As TheYear,Month(m.OrderDate) As TheMonth, sum(d.UnitPrice*d.Quantity) as Amount " +
	"from Orders m inner join OrderDetails as d on m.OrderId=d.OrderId " +
	"group by Year(m.OrderDate),Month(m.OrderDate) " +
	"order by Year(m.OrderDate),Month(m.OrderDate) ";    
}
else if (data.equals("Customer")) {
	QuerySQL =  "select * from Customers order by Region,City";
}
else if (data.equals("EmployeeOne")) {
	QuerySQL =  "select * from Employees where EmployeeID=5";
}
else if (data.equals("InvoiceMany")) {
	QuerySQL =  "select m.OrderID,m.CustomerId,c.CompanyName,m.OrderDate,M.Freight,"
	+ "d.ProductID,p.ProductName,d.UnitPrice,d.Quantity,d.UnitPrice*d.Quantity as Amount "
	+ "from (Orders m inner join "
	+ "(OrderDetails as d inner join Products p on P.ProductID=D.ProductID) on m.OrderId=d.OrderId) "
	+ "left join Customers c on c.CustomerID=m.CustomerID "
	+ "where m.OrderID>=10255 and m.OrderID<10260";
}
else if (data.equals("Picture")) {
	QuerySQL =  "select EmployeeID,LastName,FirstName,Title,TitleOfCourtesy,BirthDate,HireDate,"
	+ "Address,City,Region,PostalCode,Country,HomePhone,Extension,Photo,Notes from Employees";
}
else if (data.equals("RTFSample")) {
	QuerySQL =  "select m.OrderID,m.CustomerId,c.CompanyName,c.ContactName,c.Address,c.city,c.Region,c.Country,c.Postalcode,"
	+ "m.OrderDate,M.Freight,d.ProductID,p.ProductName,"
	+ "d.UnitPrice,d.Quantity,d.Discount,"
	+ "d.UnitPrice*d.Quantity as Amount,"
	+ "d.UnitPrice*d.Quantity*d.Discount as DiscountAmt,"
	+ "d.UnitPrice*d.Quantity-d.UnitPrice*d.Quantity*d.Discount as NetAmount "
	+ "from (Orders m inner join "
	+ "(OrderDetails as d inner join Products p on P.ProductID=D.ProductID) on m.OrderId=d.OrderId) "
	+ "left join Customers c on c.CustomerID=m.CustomerID "
	+ "where m.OrderDate between '1997-1-1' And '1997-1-15' " 
	+ "order by m.CustomerID,m.OrderDate, m.OrderID";
}
else if (data.equals("SaleByProduct")) {
	QuerySQL =  "select m.OrderID,m.OrderDate, " +
		"d.ProductID,p.ProductName,d.UnitPrice,d.Quantity,d.UnitPrice*d.Quantity as Amount  " +
		"from Orders m inner join  " +
		"(OrderDetails as d inner join Products p on P.ProductID=D.ProductID) on m.OrderId=d.OrderId " +
		"where m.OrderDate between '1997-6-1' and '1997-12-31' " +
		"order by d.ProductID, m.OrderDate";
}

else if (data.equals("SaleDetail")) {
	QuerySQL =  "select m.OrderID,m.CustomerId,c.CompanyName,m.OrderDate,M.Freight,"
	+ "d.ProductID,p.ProductName,d.UnitPrice,d.Quantity,d.Discount,"
	+ "d.UnitPrice*d.Quantity as Amount, d.UnitPrice*d.Quantity*d.Discount as DiscountAmt,"
	+ "d.UnitPrice*d.Quantity-d.UnitPrice*d.Quantity*d.Discount as NetAmount "
	+ "from (Orders m inner join "
	+ "(OrderDetails as d inner join Products p on P.ProductID=D.ProductID) on m.OrderId=d.OrderId) "
	+ "left join Customers c on c.CustomerID=m.CustomerID "
	+ "where m.OrderID<=10300 "
	+ "order by m.OrderDate, m.OrderID";
}
else if (data.equals("SaleSumByProduct")) {
	QuerySQL =  "select d.Productid,p.ProductName,sum(d.Quantity) as Quantity, " +
		"sum(d.UnitPrice*d.Quantity*(1-d.Discount)) as Amount " +
		"from orders m inner join (OrderDetails d inner join Products p " +
		"on d.ProductID=p.ProductID) " +
		"on m.orderid=d.orderid " +
		"where m.OrderDate between '1997-1-1' and '1997-12-31' " +
		"group by d.Productid,p.ProductName " +
		"order by d.Productid";
}
else if (data.equals("Report_7_3g")) {
	QuerySQL =  "select * from Products " +
		"where ProductID>=" + request.getParameter("BeginNo") + " and ProductID<=" + request.getParameter("EndNo") +
		" order by ProductID";
}
else if (data.equals("FilterSaleSummary")) {
	QuerySQL =  
		"select d.Productid,p.ProductName,sum(d.Quantity) as Quantity," +
		"sum(d.UnitPrice*d.Quantity) as Amount " + 
		"from orders m inner join (OrderDetails d inner join Products p " +
		"on d.ProductID=p.ProductID) on m.orderid=d.orderid " +
		"where m.OrderDate between '" + request.getParameter("BeginDate") + "' And '" + request.getParameter("EndDate") + "'" +
		"group by d.Productid,p.ProductName " +
		"order by d.Productid";
}
else if (data.equals("FilterSaleDetail")) {
	QuerySQL =  "select m.OrderId, m.OrderDate, d.Productid,p.ProductName,d.Quantity, " +
		"d.UnitPrice*d.Quantity as Amount " +
		"from orders m inner join (OrderDetails d inner join Products p on d.ProductID=p.ProductID) " +
		"on m.orderid=d.orderid " +
		"where (m.OrderDate between '" + request.getParameter("BeginDate") + "' And '" + request.getParameter("EndDate") + "') " +
		"and d.Productid=" + request.getParameter("ProductID") + " " +
		"order by m.OrderDate ";
}

if (QuerySQL != "") {
	XML_GenOneRecordset(response, QuerySQL);
}
else {
	ArrayList<ReportQueryItem> QueryItems = new ArrayList<ReportQueryItem>();
  
    if (data.equals("FreeGridwithDetailGrid")) {
		QueryItems.add(new ReportQueryItem("select * from Employees where EmployeeID=8", "master"));
		QueryItems.add(new ReportQueryItem("select * from Employees where EmployeeID<8", "detail"));
    }
    else if (data.equals("InvoiceOne")) {
		QueryItems.add( new ReportQueryItem("select d.ProductID,p.ProductName,d.UnitPrice,d.Quantity,d.UnitPrice*d.Quantity as Amount "
			+ "from OrderDetails as d inner join Products p on P.ProductID=D.ProductID "
			+ "where d.OrderID=10255", "Master") );
		QueryItems.add( new ReportQueryItem("select m.OrderID,m.CustomerId,c.CompanyName,C.Address,m.OrderDate,c.ContactName+c.Phone as Remark "
			+ "from Orders m left join Customers c on c.CustomerID=m.CustomerID "
			+ "where m.OrderID=10255", "Detail") );
    }
    else if (data.equals("SubReport_4a")) {
		QueryItems.add(new ReportQueryItem("select * from Customers order by CustomerID", "Customer"));
		QueryItems.add(new ReportQueryItem("select * from Products order by ProductName", "Product"));
		QueryItems.add(new ReportQueryItem(    "select c.CustomerID, c.CompanyName, sum(o.Quantity*o.UnitPrice) As SumAmt " +
			"from OrderDetails o, Orders m, customers c " +
			"where o.OrderID=m.OrderID and m.CustomerID=c.CustomerID " +
			"group by c.CustomerID, c.CompanyName " +
			"order by sum(o.Quantity*o.UnitPrice) desc", "Top10Customer"));
		QueryItems.add(new ReportQueryItem(    "select p.ProductID, p.ProductName, sum(o.Quantity*o.UnitPrice) As SumQty " +
			"from OrderDetails o, Products p " +
			"where o.ProductID=p.ProductID " +
			"group by p.ProductID, p.ProductName " +
			"order by sum(Quantity*o.UnitPrice) desc", "Top10Product"));
    }
    else if (data.equals("SubReport_4b")) {
		QueryItems.add(new ReportQueryItem("select * from Customers order by CustomerID", "Customer"));
		QueryItems.add(new ReportQueryItem("select * from Products order by ProductName", "Product"));
		QueryItems.add(new ReportQueryItem("select * from Customers order by CustomerID", "Customer2"));
    }
    else if (data.equals("SubReport_4c")) {
		QueryItems.add( new ReportQueryItem("select * from Orders where OrderID<=10260 order by OrderID", "Master") );
		QueryItems.add( new ReportQueryItem("select * from OrderDetails where OrderID<=10260", "Detail1") );
		QueryItems.add( new ReportQueryItem("select o.OrderID, o.ShipCity, c.* from Customers c, Orders o " +
			"where OrderID<=10260 and c.City=o.ShipCity " +
			"order by o.OrderID", "Detail2") );
    }
    else if (data.equals("SubReport_4d")) {
		//TBD...这里传汉字参数没做对，如 北京，天津 到这里成为乱码
		String RawCity = request.getParameter("city"); 
		String City = new String(RawCity.getBytes("ISO-8859-1"), "UTF-8");  
		//String City = request.getParameter("city"); 

		//ResponseText(response, "city='" + City + "'", ResponseDataType.PlainText);

		String CustomerQuerySQL = "select * from Customers where City='" + City + "'";
		String SupplierQuerySQL = "select * from Suppliers where City='" + City + "'";

		QueryItems.add(new ReportQueryItem(CustomerQuerySQL, "Customer"));
		QueryItems.add(new ReportQueryItem(SupplierQuerySQL, "Supplier"));
    }
    else if (data.equals("Chart_8b")) {
		QueryItems.add(new ReportQueryItem("select * from scatter order by Name X", "Table1"));
		QueryItems.add(new ReportQueryItem("select * from scatter order by Name X", "Table2"));
		QueryItems.add(new ReportQueryItem("select * from scatter order by Name X", "Table3"));
		QueryItems.add(new ReportQueryItem("select * from scatter order by Name X", "Table4"));
    }
    else if (data.equals("Chart_8d")) {
      String SQL = "select c.Region,  d.ProductID,p.ProductName, " +
          "sum(d.UnitPrice * d.Quantity) as Amount " +
          "from(Orders m inner join (OrderDetails as d inner join Products p on P.ProductID = D.ProductID) on m.OrderId = d.OrderId) " +
          "left join Customers c on c.CustomerID = m.CustomerID " +
          "where d.ProductID in (1, 10, 11, 21) and m.OrderDate between #1997/1/1# and #1997/12/31# " +
          "group by c.Region, d.ProductID, p.ProductName " +
          "order by d.ProductID, c.Region";
      String SQL2 = "select c.Region, sum(d.UnitPrice * d.Quantity) as Amount, sum(d.Quantity) as Quantity " +
          "from(Orders m inner join OrderDetails d on m.OrderId = d.OrderId) " +
          "left join Customers c on c.CustomerID = m.CustomerID " +
          "where d.ProductID = 11 and m.OrderDate between #1997/1/1# and #1997/12/31# " +
          "group by c.Region " +
          "order by c.Region";

		QueryItems.add(new ReportQueryItem(SQL, "Table1"));
		QueryItems.add(new ReportQueryItem(SQL, "Table2"));
		QueryItems.add(new ReportQueryItem(SQL, "Table3"));
		QueryItems.add(new ReportQueryItem(SQL2, "Table4"));
    }

	if (QueryItems.size() == 0) {
		ResponseText(response, "没有为数据 '" + data + "' 分配处理程序，无法获取到报表数据！", ResponseDataType.PlainText);
	}
	else {
		XML_GenMultiRecordset(response, QueryItems);
	}
}
%> 