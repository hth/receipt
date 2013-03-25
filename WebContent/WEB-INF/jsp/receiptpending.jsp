<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title><fmt:message key="receipt.title" /></title>		
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

	<c:choose>
		<c:when test="${receipts.size() > 0}">
			<h2 class="demoHeaders">Pending receipt</h2>	
		
			<table>
				<tr>
					<td valign="top">
						<table border="1" style="width: 400px" class="atable">
							<tr>
								<th align="left">&nbsp;</th>
								<th align="left">&nbsp;Description</th>
								<th align="left">&nbsp;Upload Date</th>
								<th align="left">&nbsp;</th>
							</tr>
							<c:forEach items="${receipts}" var="receipt" varStatus="status">
							<tr>
								<td align="right">
									${status.count}
								</td>
								<td align="left">
						    		${receipt.description}
								</td>
								<td align="left">
									${receipt.created}
								</td>
								<td>
									<%-- <FORM>
										<INPUT type="button" value="Show Receipt" onClick="window.open('${pageContext.request.contextPath}/receiptimage.htm?id=${receipt.receiptBlobId}','mywindow','width=400,height=200')">
									</FORM> --%>	
									
									<a href="${pageContext.request.contextPath}/receiptupdate.htm?id=${receipt.id}">
										Show Receipt				
									</a>								
								</td>
							</tr>
							</c:forEach>
						</table>
					</td>
					<td width="6px">&nbsp;</td>
					<td>
					 Image goes here
					</td>
				</tr>
			</table>
		</c:when>
		<c:otherwise>
			<h2 class="demoHeaders">No pending receipt</h2>			
		</c:otherwise>
	</c:choose>	
</body>
</html>