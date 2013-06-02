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
<div id="wrapper">
    <div style='width:231px;'>
        <div style='width:14px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <img src="images/circle-leaf.jpg" alt="receipt-o-fi logo" height="12px" width="12px">&nbsp;&nbsp;&nbsp;
        </div>
        <div style='width:60px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 1em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 1em .0em; padding: .5em;'>
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
                        <table style="width: 400px" class="etable">
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
                                    <fmt:formatDate value="${receipt.created}" type="both"/>
                                </td>
                                <td>
                                    <%-- <FORM>
                                        <INPUT type="button" value="Show Receipt" onClick="window.open('${pageContext.request.contextPath}/receiptimage.htm?id=${receipt.receiptBlobId}','mywindow','width=400,height=200')">
                                    </FORM> --%>

                                    <a href="${pageContext.request.contextPath}/emp/update.htm?id=${receipt.id}">
                                        Show Receipt
                                    </a>
                                </td>
                            </tr>
                            </c:forEach>
                        </table>
                    </td>
                    <td width="6px">&nbsp;</td>
                    <td>
                     Empty. This should be removed
                    </td>
                </tr>
            </table>
        </c:when>
        <c:otherwise>
            <h2 class="demoHeaders">No pending receipt</h2>
        </c:otherwise>
    </c:choose>
</div>

<div class="footer">
    <p>
        <a href="${pageContext.request.contextPath}/aboutus.html">About Us</a> -
        <a href="${pageContext.request.contextPath}/tos.html">Terms of Service</a>
    </p>
    <p>Copyright &copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>
</html>