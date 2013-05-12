<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page language="java" contentType="text/html; charset=US-ASCII" pageEncoding="US-ASCII"%>
<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="title" /></title>

    <meta http-equiv="Content-Type" content="text/html; charset=US-ASCII">

    <link rel='stylesheet' type='text/css' href='jquery/css/smoothness/jquery-ui-1.10.2.custom.min.css'>
    <link rel='stylesheet' type='text/css' href='jquery/css/receipt.css'>

    <script type="text/javascript" src="jquery/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="jquery/js/jquery-ui-1.10.2.custom.min.js"></script>
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


    <c:if test="${receipts.size() > 0}">
        <table style="width: 450px" class="etable">
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
                    <td style="padding:3px;" title="${receipt.description}">
                        <spring:eval expression="receipt.bizName.name" />
                    </td>
                    <td style="padding:3px;">
                        <spring:eval expression="receipt.receiptDate" />
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
    </c:if>
</body>