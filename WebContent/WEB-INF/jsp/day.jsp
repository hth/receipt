<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="title" /></title>

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
    <c:when test="${!empty receipts}">
    <table style="width: 650px" class="etable">
        <tbody>
        <tr style="background-color:orange;color:white;">
            <th style="padding:3px;"></th>
            <th style="padding:3px;">Title</th>
            <th style="padding:3px;">Receipt Date</th>
            <th style="padding:3px;">Tax</th>
            <th style="padding:3px;">Total</th>
        </tr>
        </tbody>
        <c:forEach var="receipt" items="${receipts}"  varStatus="status">
            <tr>
                <td style="padding:3px;" align="right">
                    ${status.count}
                </td>
                <td style="padding:3px;">
                    <spring:eval expression="receipt.bizName.name" />
                </td>
                <td style="padding:3px;">
                    <fmt:formatDate value="${receipt.receiptDate}" type="both"/>
                </td>
                <td style="padding:3px;" align="right">
                    <spring:eval expression="receipt.tax" />
                </td>
                <td style="padding:3px;" align="right">
                    <a href="${pageContext.request.contextPath}/receipt.htm?id=${receipt.id}">
                        <spring:eval expression="receipt.total" />
                    </a>
                </td>
            </tr>
        </c:forEach>
    </table>
    </c:when>
    <c:otherwise>
    <div class="ui-widget">
        <div class="ui-state-highlight ui-corner-all" style="margin-top: 0px; padding: 0 .7em;">
            <p>
                <span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span>
                <span style="display:block; width:410px;">
                    No receipt submitted for this day
                </span>
            </p>
        </div>
    </div>
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