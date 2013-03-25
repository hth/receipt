<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="item.analytic.title" /></title>
	
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
	
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.9.2.custom.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>
	
	<script type="text/javascript" src="jquery/js/jquery-1.8.3.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.9.2.custom.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
</head>
<body>
	<div>
		<p>User Id <a href="${pageContext.request.contextPath}/userprofilepreference.htm">${sessionScope['userSession'].emailId}</a></p>		
	</div>
	
	<br/>
	
	<table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
		<tbody>
			<tr style="background-color:orange;color:white;">			
				<th style="padding:3px;">Name</th>
				<th style="padding:3px;">Price</th>
				<th style="padding:3px;">Average Price</th>
			</tr>	
		</tbody>
		<tr>
			<td align="left">
	    		${item.name}
			</td>
			<td align="right">
	    		<spring:eval expression="item.price" />
	    		&nbsp;
			</td>
			<td align="right">
				<spring:eval expression="averagePrice"/>
				&nbsp;
			</td>
		</tr>
	</table>
	
</body>
</html>