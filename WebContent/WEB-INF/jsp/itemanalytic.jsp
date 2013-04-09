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
	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="jquery/fullcalendar/fullcalendar.min.js"></script>
</head>
<body>
    <div id=?content? style='width:210px;'>
        <div id=?leftcolumn? style='width:60px; height: 16px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/landing.htm">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>Home</span>
            </a>
        </div>
        <div id=?rightcolumn? style='width:130px; height: 16px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">${sessionScope['userSession'].emailId}</a>
        </div>
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