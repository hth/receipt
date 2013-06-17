<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
	<title><fmt:message key="receipt.title" /></title>

	<meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">
    <link rel="icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />
    <link rel="shortcut icon" type="image/x-icon" href="images/circle-leaf-sized_small.png" />

	<link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
	<link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

	<script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>

</head>
<body>
<div class="wrapper">
    <div style='width:243px;'>
        <div style='width:20.25px; height: 20.25px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em 0em .0em; padding: .14em;'>
            <img src="images/circle-leaf-sized_small.png" alt="receipt-o-fi logo" height="19px" width="19px">
        </div>
        <div style='width:65px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:left; margin: .0em .0em .0em .0em; padding: .5em;'>
            &nbsp;&nbsp;&nbsp;
            <a href="${pageContext.request.contextPath}/landing.htm" style="text-decoration:none;">
                <img src="images/home.png" width="10px" height="10px" alt="Home"><span>&nbsp;&nbsp;Home</span>
            </a>
        </div>
        <div style='width:130px; height: 12px; display:inline-block; background-color:rgba(0,0,0,0.1); float:right; margin: .0em .0em 0em .0em; padding: .5em;'>
            <a href="${pageContext.request.contextPath}/userprofilepreference/i.htm" style="text-decoration:none;">${sessionScope['userSession'].emailId}</a>
        </div>
    </div>

    <p>&nbsp;</p>

    <c:choose>
        <c:when test="${pendingReceipts.size() gt 0}">
            <h2 class="demoHeaders">Pending receipt<c:if test="${pendingReceipts.size() gt 1}">s</c:if></h2>

            <table>
                <tr>
                    <td valign="top">
                        <table style="width: 500px" class="etable">
                            <tr>
                                <th style="padding: 3px;" align="left">&nbsp;</th>
                                <th style="padding: 3px;" align="left">&nbsp;Upload Date</th>
                                <th style="padding: 3px;" align="left">&nbsp;File Name</th>
                                <th style="padding: 3px;" align="left">&nbsp;</th>
                            </tr>
                            <c:forEach items="${pendingReceipts}" var="pendingReceiptForm" varStatus="status">
                            <tr>
                                <td style="padding: 3px;" align="right">
                                    ${status.count}
                                </td>
                                <td style="padding: 3px;" align="left">
                                    <fmt:formatDate value="${pendingReceiptForm.receiptEntityOCR.created}" type="both"/>
                                </td>
                                <td style="padding: 3px;">
                                    ${pendingReceiptForm.fileName}
                                </td>
                                <td style="padding: 3px;">
                                    <a href="${pageContext.request.contextPath}/emp/update.htm?id=${pendingReceiptForm.receiptEntityOCR.id}">
                                        View Receipt
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
    <p>&copy; 2013 receipt-o-fi. All Rights Reserved.</p>
</div>

</body>
</html>