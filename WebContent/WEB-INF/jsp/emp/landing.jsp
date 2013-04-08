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

<h2>Pending Receipts</h2>

<c:if test="${pending.size() > 0}">
    <table border="1" style="background-color:#c5c021;border:1px dotted black;width:450px;border-collapse:collapse;">
        <tbody>
        <tr style="background-color:orange;color:white;">
            <th style="padding:3px;"></th>
            <th style="padding:3px;">User Type</th>
            <th style="padding:3px;">Description</th>
            <th style="padding:3px;">Created</th>
            <th style="padding:3px;">Edit</th>
        </tr>
        </tbody>
        <c:forEach var="receipt" items="${pending}"  varStatus="status">
            <tr>
                <td style="padding:3px;" align="right">
                        ${status.count}
                </td>
                <td style="padding:3px;">
                    <spring:eval expression="receipt.level" />
                </td>
                <td style="padding:3px;">
                    <spring:eval expression="receipt.description" />
                </td>
                <td style="padding:3px;">
                    <spring:eval expression="receipt.created" />
                </td>
                <td style="padding:3px;" align="right" title="${receipt.idReceiptOCR}">
                    <a href="${pageContext.request.contextPath}/emp/receiptupdate.htm?id=${receipt.idReceiptOCR}">
                        Open
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
</body>
</html>