<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="receipt.admin.title" /></title>		
	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
	
	<link rel='stylesheet' type='text/css' href='../jquery/fullcalendar/fullcalendar.css' />
	<link rel='stylesheet' type='text/css' href='../jquery/fullcalendar/fullcalendar.print.css' media='print' />
	<link rel='stylesheet' type='text/css' href='../jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>	
	<link rel='stylesheet' type='text/css' href='../jquery/css/receipt.css'>
	
	<script type="text/javascript" src="../jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="../jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
	<script type='text/javascript' src="../jquery/fullcalendar/fullcalendar.min.js"></script>
 
</head>
<body>
	<div>
		<p>User Id <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm">${userSession.emailId}</a></p>
	</div>

<h2>Search users to change profile settings</h2>
<form:form method="post" modelAttribute="userSearchForm" action="landing.htm">
	<table>
	    <tr>
	        <th>Search Name: </th>
	        <td><form:input path="name" id="name"/></td>
	    </tr>
	    <tr>
	    	<td colspan="2"><br /> </td>
	    </tr>
	    <c:if test="${users.size() > 0}">
			<table border="1" style="background-color:#c5c021;border:1px dotted black;width:250px;border-collapse:collapse;">
				<tbody>
					<tr style="background-color:orange;color:white;">
						<th style="padding:3px;"></th>
						<th style="padding:3px;">Level</th>
						<th style="padding:3px;">Name</th>
					</tr>
				</tbody>
				<c:forEach var="user" items="${users}"  varStatus="status">
				<tr>
					<td style="padding:3px;" align="center">
						${status.count}
					</td>
					<td style="padding:3px;" align="center" title="${user.level}">
						<spring:eval expression="user.level.description" />
					</td>
					<td style="padding:3px;" align="left" title="${user.name}">
						<a href="${pageContext.request.contextPath}/userprofilepreference/their.htm?id=${user.id}">
							<spring:eval expression="user.name" />
						</a>
					</td>
				</tr>
				</c:forEach>
			</table>
			</c:if>
	</table>     
</form:form>


<script type="text/javascript">
	function split(val) {
	    return val.split(/,\s*/);
	}
	function extractLast(term) {
	    return split(term).pop();
	}
	 
	$(document).ready(function() {
	 
	    $( "#name" ).autocomplete({
	        source: "${pageContext. request. contextPath}/admin/find_user.htm"
	        /* source : ["Alex,Agnes,Alan,Bjok,Bill,John,Jason,Maria,Man"] */
	    });
	    
	});
</script>
</body>
</html>