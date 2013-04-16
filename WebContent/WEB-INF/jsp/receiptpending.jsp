<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.title" /></title>
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
    <div id="content" style='width:210px;'>
        <div id="leftcolumn" style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div id="rightcolumn" style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

	<br/>

	<c:choose>
		<c:when test="${receipts.size() gt 0}">
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

									<a href="${pageContext.request.contextPath}/emp/receiptupdate.htm?id=${receipt.id}">
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